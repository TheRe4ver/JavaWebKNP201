package step.learning.servlets;

import com.google.gson.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import step.learning.dao.CallMeDao;
import step.learning.dto.entities.CallMe;
import step.learning.services.db.DbProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Singleton
public class DbServlet extends HttpServlet {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final CallMeDao callMeDao;
    @Inject
    public DbServlet(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, CallMeDao callMeDao) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.callMeDao = callMeDao;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // метод який запускається перед тим, як буде здійснено "розподіл" за HTTP методами
        // тут можна додати реакцію на додаткові методи запиту
        switch (req.getMethod().toUpperCase()) {
            case "PATCH": doPatch(req, resp); break;
            case "COPY": doCopy(req, resp); break;
            case "LINK": doLink(req, resp); break;
            case "PURGE": doPurge(req, resp); break;
            case "MOVE": doMove(req, resp); break;
            // case "UNLINK": break;
            default: super.service(req, resp);
        }

    }

    protected void doCopy(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CallMe> calls =  callMeDao.getAll() ;
        // List<CallMe> calls = new ArrayList<>() ;
        // calls.add(new CallMe("100500", "Петрович", "+380987654321", new Date()));
        // calls.add(new CallMe("100501", "Петрович2", "+380987654322", new Date()));
        Gson gson = new GsonBuilder().create();
        resp.setContentType("application/json");
        String jsonData = gson.toJson(calls);
        resp.getWriter().print(jsonData);
    }
    protected void doLink(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String callId = req.getParameter("call-id");
        if( callId == null)
        {
            resp.setStatus(400);
            resp.getWriter().print("\"Missing required parameter 'call-id' \"");
            return;
        }
        CallMe item = callMeDao.getById(callId);
        if(item == null)
        {
            resp.setStatus(404);
            resp.getWriter().print("\"Item not found for given parameter 'call-id' \"");
            return;
        }
        if(item.getCallMoment() != null){
            resp.setStatus(422);
            resp.getWriter().print("\"Unprocessable Content: Item was processed early \"");
            return;
        }
        if(callMeDao.updateCallMoment(item)){
            resp.setStatus(202);
            resp.getWriter().print(new Gson().toJson(item));
        }
        else
        {
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs \"");
            return;
        }
    }
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String callId = req.getParameter("call-id");
        if(callId == null) {
            resp.setStatus(400);
            resp.getWriter().print("\"Missing required parameter 'call-id'\"");
            return;
        }
        CallMe item = callMeDao.getById(callId);
        if(item == null ){
            resp.setStatus(404);
            resp.getWriter().print("\"Item not found for given parameter 'call-id'\"");
            return;
        }
        if(item.getCallMoment() != null) {
            resp.setStatus(422);
            resp.getWriter().print("\"Unprocessable Content: Item was processed early\"");
            return;
        }
        if(callMeDao.updateCallMoment(item)){
            resp.setStatus(202);
            resp.getWriter().print(new Gson().toJson(item));
            return;
        }
        else
        {
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs \"");
            return;
        }
    }
    protected void doPurge(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CallMe> calls = callMeDao.getAll(true);
        Gson gson = new GsonBuilder().create();
        resp.getWriter().print(gson.toJson(calls));
    }
    protected void doMove(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        resp.setContentType("application/json");
        String callId = req.getParameter("call-id");
        if( callId == null)
        {
            resp.setStatus(400);
            resp.getWriter().print("\"Missing required parameter 'call-id' \"");
            return;
        }
        CallMe item = callMeDao.getById(callId,true);
        if(item == null)
        {
            resp.setStatus(404);
            resp.getWriter().print("\"Item not found for given parameter 'call-id' \"");
            return;
        }
        if(item.getDeleteMoment() == null){
            resp.setStatus(422);
            resp.getWriter().print("\"Unprocessable Content: Item was processed early \"");
            return;
        }
        if(callMeDao.restore(item)){
            resp.setStatus(202);
            resp.getWriter().print("\"Operation completed\"");
        }
        else
        {
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs \"");
            return;
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String connectionStatus;
        try {
            dbProvider.getConnection();
            connectionStatus = "Connection OK" ;
        }
        catch (RuntimeException e) {
            connectionStatus = "Connection error: " + e.getMessage();
        }
        req.setAttribute("connectionStatus", connectionStatus);

        req.setAttribute("page-body", "db.jsp");
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Реакція на кнопку "create" - створюємо таблицю БД "замовлення двінків"
        String status;
        String message;
        String sql = "CREATE TABLE " + dbPrefix + "call_me (" +
                "`id`     BIGINT UNSIGNED PRIMARY KEY DEFAULT (UUID_SHORT())," +
                "`name`   VARCHAR(64)     NULL," +
                "`phone`  CHAR(13)        NOT NULL COMMENT '+38 098 765 43 21'," +
                "`moment` DATETIME        DEFAULT CURRENT_TIMESTAMP," +
                "`call_moment` DATETIME NULL," +
                "`delete_moment` DATETIME NULL " +
                ") ENGINE = InnoDB DEFAULT CHARSET = UTF8;";
        try (Statement statement = dbProvider.getConnection().createStatement()) {
            statement.executeUpdate(sql);
            status = "OK";
            message = "Table created";
        }
        catch (SQLException e) {
            status = "error";
            message = e.getMessage();
        }

        JsonObject result = new JsonObject();
        result.addProperty("status", status);
        result.addProperty("message", message);
        resp.getWriter().print(result.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String contentType = req.getContentType() ;
        if( contentType == null || ! contentType.startsWith("application/json")) {
            resp.setStatus(415);
            resp.getWriter().print("\"Unsupported Media Type: 'application/json' only\"");
            return;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];

        int len ;
        String json;
        JsonObject result = new JsonObject();

        try(InputStream body = req.getInputStream()) {
            while((len = body.read(buffer)) > 0) {
                bytes.write(buffer, 0, len);
            }
            json = bytes.toString(StandardCharsets.UTF_8.name());

        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs.\"");
            return;
        }

        JsonObject data ;

        try {
            data = JsonParser.parseString(json).getAsJsonObject();
        }
        catch (JsonSyntaxException | IllegalStateException e) {
            resp.setStatus(400);
            resp.getWriter().print("\"Invalid JSON. Object required\"");
            return;
        }

        String status, name, phone, id ;

        try {
            name = data.get("name").getAsString();
            phone = data.get("phone").getAsString();
        }
        catch (Exception ignored) {
            resp.setStatus(400);
            resp.getWriter().print("\"Invalid JSON data: required 'name' and 'phone' fields\"");
            return;
        }

        if("".equals(name)) {
            result.addProperty("status", "validation error");
            result.addProperty("message", "name can't be empty");
            resp.getWriter().print(result.toString());
            return;
        }

        if("".equals(phone)) {
            result.addProperty("status", "validation error");
            result.addProperty("message", "phone can't be empty");
            resp.getWriter().print(result.toString());
            return;
        }

        if(! Pattern.matches("^\\+38\\s?(\\(\\d{3}\\)|\\d{3})\\s?\\d{3}(-|\\s)?\\d{2}(-|\\s)?\\d{2}$", phone)) {
            resp.setStatus(400);
            resp.getWriter().print("\"Invalid 'phone' field: required '+\\d{12}' format\"");
            return;
        }

        phone = phone.replaceAll("[\\s()-]+", "");

        String sql = "INSERT INTO " + dbPrefix + "call_me( id, name, phone ) " +
                "VALUES ( UUID_SHORT(), ?, ? )" ;
        try ( PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql) ) {
            prep.setString(1, name);
            prep.setString(2, phone);
            prep.execute();

            long lastInsertedId = -1;
            String lastInsertedSql = "SELECT id FROM " + dbPrefix + "call_me ORDER BY id DESC LIMIT 1";
            try ( PreparedStatement stmt = dbProvider.getConnection().prepareStatement(lastInsertedSql) ) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    lastInsertedId = rs.getLong(1);
                    id = Long.toString(lastInsertedId);
                }
                else {
                    resp.setStatus(500);
                    resp.getWriter().print("\"Server error. Details on server's logs.\"");
                    return;
                }
            }

            // Use the 'lastInsertedId' as the ID of the newly created item
            System.out.println("Last Inserted ID: " + lastInsertedId);
        }
        catch (SQLException e) {
            System.err.println(e.getMessage() + " " + sql);
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs.\"");
            return;
        }

        resp.setStatus(201);
        result.addProperty("status", "OK");
        result.addProperty("id", id);
        result.addProperty("name", name);
        result.addProperty("phone", phone);

        resp.getWriter().print(result.toString());

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String callId = req.getParameter("call-id");
        if(callId == null) {
            resp.setStatus(400);
            resp.getWriter().print("\"Missing required parameter 'call-id'\"");
            return;
        }
        CallMe item = callMeDao.getById(callId);
        if(item == null ){
            resp.setStatus(404);
            resp.getWriter().print("\"Item not found for given parameter 'call-id'\"");
            return;
        }
        if(item.getDeleteMoment() != null) {
            resp.setStatus(422);
            resp.getWriter().print("\"Unprocessable Content: Item was processed early\"");
            return;
        }

        if(callMeDao.delete(item)){
            resp.setStatus(202);
            resp.getWriter().print("\"Operation completed\"");
            return;
        }
        else
        {
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs \"");
            return;
        }
    }
}