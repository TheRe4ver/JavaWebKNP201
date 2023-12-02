<%@ page import="step.learning.dto.models.RegFormModel" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // Перевіряємо чи є повідомлення попередньої форми, формуємо значення полів
    RegFormModel model = ( RegFormModel ) request.getAttribute("model");
    String loginValue = model == null ? "" : model.getLogin();
    String nameValue = model == null ? "" : model.getName();
    String emailValue = model == null ? "" : model.getEmail();
    String birthdateValue = model == null ? "" : model.getBirthdateAsString();
    Map<String, String> errors = model == null ? new HashMap<String, String>() : (HashMap<String, String>) model.getErrorMessages();

    String nameClass = model == null ? "validate" : ( errors.containsKey("name") ? "invalid" : "valid");
    String loginClass = model == null ? "validate" : ( errors.containsKey("login") ? "invalid" : "valid");
    String emailClass = model == null ? "validate" : ( errors.containsKey("email") ? "invalid" : "valid");
    String passwordClass = model == null ? "validate" : ( errors.containsKey("password") ? "invalid" : "valid");
    String repeatClass = model == null ? "validate" : ( errors.containsKey("repeat") ? "invalid" : "valid");
    String birthdateClass = model == null ? "validate" : ( errors.containsKey("birthdate") ? "invalid" : "valid");

    String regMessage = (String) request.getAttribute("reg-message");
    if(regMessage == null) {
        regMessage = "";
    }
%>
<h2>Реєстрація користувача</h2>
<p><%=regMessage%></p>
<div class="row">
    <form class="col s12" method="post" enctype="multipart/form-data" >
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">person</i>
                <input value="<%=loginValue%>" name="reg-login" id="reg-login" type="text" class=<%=loginClass%>>
                <label for="reg-login">Логін</label>
                <% if(errors.containsKey("login")) { %>
                <span class="helper-text" data-error="<%=errors.get("login")%>" ></span>
                <% } %>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">badge</i>
                <input value="<%=nameValue%>" name="reg-name" id="reg-name" type="tel" class=<%=nameClass%>>
                <label for="reg-name">Ім'я</label>
                <% if(errors.containsKey("name")) { %>
                <span class="helper-text" data-error="<%=errors.get("name")%>" ></span>
                <% } %>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">alternate_email</i>
                <input value="<%=emailValue%>" name="reg-email" id="reg-email" type="email" class=<%=errors.containsKey("email") ? "invalid" : ""%>>
                <label for="reg-email">E-mail</label>
                <% if(errors.containsKey("email")) { %>
                <span class="helper-text" data-error="<%=errors.get("email")%>" ></span>
                <% } %>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">cake</i>
                <input value="<%=birthdateValue%>" name="reg-birthdate" id="reg-birthdate" type="date" class=<%=errors.containsKey("birthdate") ? "invalid" : ""%>>
                <label for="reg-birthdate">Дата народження</label>
                <% if(errors.containsKey("birthdate")) { %>
                <span class="helper-text" data-error="<%=errors.get("birthdate")%>" ></span>
                <% } %>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">alternate_email</i>
                <input name="reg-password" id="reg-password" type="password" class=<%=errors.containsKey("password") ? "invalid" : ""%>>
                <label for="reg-password">Пароль</label>
                <% if(errors.containsKey("password")) { %>
                <span class="helper-text" data-error="<%=errors.get("password")%>" ></span>
                <% } %>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">cake</i>
                <input name="reg-repeat" id="reg-repeat" type="password" class=<%=errors.containsKey("repeat") ? "invalid" : ""%>>
                <label for="reg-repeat">Повторіть пароль</label>
                <% if(errors.containsKey("repeat")) { %>
                <span class="helper-text" data-error="<%=errors.get("repeat")%>" ></span>
                <% } %>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s6">
                <label for="reg-rules" class=<%=errors.containsKey("rules") ? "invalid" : ""%>>
                    <input id="reg-rules" name="reg-rules" type="checkbox" class="filled-in" />
                    <span>Не буду нічо порушувати</span>
                    <% if(errors.containsKey("rules")) { %>
                    <span class="helper-text" data-error="<%=errors.get("rules")%>" ></span>
                    <% } %>
                </label>
            </div>
            <div class="file-field input-field col s6">
                <div class="btn green ">
                    <i class="material-icons ">account_box</i>
                    <input name="reg-avatar" type="file">
                </div>
                <div class="file-path-wrapper">
                    <input class="file-path validate" type="text" placeholder="Ава вашу сюди додаєш">
                </div>
            </div>
        </div>
        <div class="input-field row right-align">
        <button class="waves-effect waves-light btn green accent-4">Реєстрація</button>
    </div>
    </form>
</div>

<p class="gray-text">
    Передача файлів через форми.
    По-перше, передача файлів можлива лише методом POST та з кодуванням пакету <code>multipart/form-data</code>
    (за замовченням, форма передається з іншим кодуванням <code>application/x-www-form-urlencoded</code>. Також переконуємось
    у наявності атрибута name
    <br/>
    ПО-друге, прийомання таких пакетів з боку червера вимагає окремої обробки. Для цього вживаються додаткові модулі-
    залежності. Наприклад, <a href="https://mvnrepository.com/artifact/commons-fileupload/commons-fileupload">Apache Commons FileUpload</a>
    <br/>
    В ASP для роботи з файлами є інтерфейс IFormFile, його аналог в обраному пакеті - FileItem. У формі, окрім файлів,
    також передаються інші поля ( у текстовому вигляді ). Відповідно результат розбору (парсингу) форми є дві колекції -
    файлів та полів. Для повернення єдиного результату (з двох колекцій) слід зробити спільний інтерфейс.
</p>
