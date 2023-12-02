package step.learning.services.formparse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Form parse service for both form enctypes:
 * multipart/form-data and application/x-www-form-urlencoded
 */
@Singleton
public class MixedFormParseService implements FormParseService {
    private static final int MEMORY_THRESHOLD = 10 * 1024 * 1024 ;
    private static final int MAX_FILE_SIZE = 20 * 1024 * 1024 ;
    private static final int MAX_FORM_SIZE = 40 * 1024 * 1024 ;
    private final ServletFileUpload fileUpload ;
    @Inject
    public  MixedFormParseService() {
        // Початкові налаштування для запобігання вразливості переповнення диска
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        // Максимальний розмір файлу, що залишається у пам'яті
        fileItemFactory.setSizeThreshold(MEMORY_THRESHOLD);
        // Місце для тимчасовом завантажених файлів (системна tmp)
        fileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        fileUpload = new ServletFileUpload(fileItemFactory);
        fileUpload.setFileSizeMax(MAX_FILE_SIZE);
        fileUpload.setSizeMax(MAX_FORM_SIZE);
    }
    @Override
    public FormParseResult parse(final HttpServletRequest request) {
        final Map<String, String> fields = new HashMap<>();
        final Map<String, FileItem> files = new HashMap<>();
        final HttpServletRequest req = request;
        // визначаємо тип запиту (multipart|urlencoded)
        boolean isMultipart = request.getHeader("Content-Type").startsWith("multipart/form-data");
        // кодування закладене у CharsetFilter
        String charsetName = (String) request.getAttribute("charsetName");
        if(charsetName == null) { // для перенесення в інші проєкти
            charsetName = StandardCharsets.UTF_8.name();
        }
        if(isMultipart) {
            // засоби commons.upload
            try {
                for(FileItem item : fileUpload.parseRequest(request) ){
                    // всі частини (parts) запиту подаються узагальненими FileItem
                    if(item.isFormField()) { // це текстове поле форми
                        fields.put(item.getFieldName(), item.getString(charsetName));
                    }
                    else { // файлове поле
                        files.put(item.getFieldName(), item);
                    }
                }
            } catch (FileUploadException | UnsupportedEncodingException e) {
                throw new RuntimeException(e); // TODO: replace by logger
            }
        }
        else { // urlencoded
            // servlet-api засоби вилучення параметрів
            Enumeration<String > paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                fields.put(name, request.getParameter(name));
            }
        }
        return new FormParseResult() {
            @Override public Map<String, String> getFields() { return fields; }
            @Override public Map<String, FileItem> getFiles() { return files; }
            @Override public HttpServletRequest getRequest() { return req; }
        };
    }
}
