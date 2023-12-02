<%@ page contentType="text/html;charset=UTF-8" %>
<h1>SPA</h1>
<p>
    Автентифікація та авторизація за допомогою токенів здійснюється наступним чином: <br/>
    - користувач вводить логін та пароль, формується асинхронний запит до API авторизації, у відповідь отримується токен<br/>
    - токен перевіряється на цілісність та зберігається у локальному сховищі; подальші запити включають одержаний токен до заголовків
</p>
<p>
    Наявність токену на сторінці: <b id="spa-token-status"></b>
</p>
<auth-part></auth-part>
<button class="btn green" id="spa-get-data">Дані</button>
<button class="btn green" id="spa-log-out">Вихід</button>

<%--<button class="btn green" id="spa-page-1">Сторінка 1</button>--%>
<%--<button class="btn green" id="spa-page-2">Сторінка 2</button>--%>

<%--<button class="btn green" id="404">404</button>--%>
