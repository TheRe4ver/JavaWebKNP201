package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.UserDao;
import step.learning.dto.models.RegFormModel;
import step.learning.services.formparse.FormParseResult;
import step.learning.services.formparse.FormParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;

@Singleton
public class SignupServlet extends HttpServlet {
    private final FormParseService formParseService;
    private final UserDao userDao;

    @Inject
    public SignupServlet(FormParseService formParseService, UserDao userDao) {
        this.formParseService = formParseService;
        this.userDao = userDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // перевіряємо чи є повідомлення у сесії
        HttpSession session = req.getSession();
        Integer regStatus = (Integer) session.getAttribute("reg-status");
        if(regStatus != null) { // є повідомлення
            // видаляємо його з сесії
            session.removeAttribute("reg-status");
            // та передаємо дані у атрибути запиту
            String message ;
            if(regStatus == 0) {
                message = "Помилка оброблення даних форми";
            }
            else if (regStatus == 1) {
                message = "Помилка валідації даних форми";
                req.setAttribute("model", session.getAttribute("reg-model"));
                session.removeAttribute("reg-model");
            }
            else {
                message = "Реєстрація успішна";
            }
            req.setAttribute("reg-message", message);
        }
        // resp.getWriter().print("HomeServlet");
        req.setAttribute("page-body", "signup.jsp");
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FormParseResult formParseResult = formParseService.parse(req);
        RegFormModel model;

        try {
            model = new RegFormModel(formParseResult);
        } catch (ParseException e) {
            // throw new RuntimeException(e);
            model = null;
        }

        HttpSession session = req.getSession();

        // Зберігаємо необхідні дані у сесії та повертаємо на ГЕТ шляхом відповіді-редиректу
        if(model == null) {
            // стан помилки розбору форм
            session.setAttribute("reg-status", 0);
        }
        else if(!model.getErrorMessages().isEmpty()) {
            // стан помилки валідації - зберігаємо саму модель
            // для відновлення даних на формі введення
            session.setAttribute("reg-model", model);
            session.setAttribute("reg-status", 1);
        }
        else {
            // стан успішної обробки моделі - передаємо лише повідомлення
            if(userDao.addFromForm(model)) {
                session.setAttribute("reg-status", 2);
            }

        }
        //
        resp.sendRedirect(req.getRequestURI());
    }
}
