<%@ page contentType="text/html;charset=UTF-8"%>
<%
    String connectionStatus = (String) request.getAttribute("connectionStatus");
%>
<h1>Робота з базами даних</h1>
<h2>JDBC</h2>
<p>
    <strong>JDBC</strong> - Java DataBase Connectivity - технологія доступу до даних, аналогічна подібним ADO, PDO тощо.
    Головна ідея - створення підключення, формування запиту та передача його до СУБД, одержання результатів виконання запиту
    та даних, що ним повертаються.
</p>
<p>
    Технологія надає універсальний інтерфейс доступу до даних (однаковий для різних СУБД), конкретна реалізація здійснюється
    шляхом підключення драйвер відповідної БД (які також називають коекторами).
    Налаштування підключення здійснюється шляхом реєстрації драйвера та надсилання запиту до СУБД щодо підключення
    (автентифікації). Нагадуємо, що паролі до БД слід зберігати в окремому файлі (конфігурації) який вилучено з репозиторія
    (у .gitignore).
</p>
<p>
    Оскільки дані про підключення можуть знадобитись у різних частинах проєкту найбільш доцільно створювати його у вигляді
    окремого сервісу.
</p>
<p>
    Статус підключення: <strong><%=connectionStatus%></strong>
</p>
<p>
    При роботі кількох сервісів з однією БД вживається підхід з розрізнення таблиць за допомогою схем або префіксів.
    Не всі БД підтримують схеми (MS SQL = так, MySQL - ні)
</p>
<h2>Управління даними</h2>
<p>
    <button id="db-create-button" class="waves-effect waves-light btn green accent-4"><i class="material-icons right">cloud</i>CREATE</button>
    <input name="user-name" placeholder="Ім'я">
    <input name="user-phone" placeholder="Телефон">
    <button id="db-insert-button" class="waves-effect waves-light btn green accent-4"><i class="material-icons right">phone_iphone</i>Замовити дзвінок</button>
    <br/>
    <u id="out"></u>
</p>
<div class="row">
    <button id="db-read-button"
            class="waves-effect waves-light btn green accent-4">
        <i class="material-icons right">view_list</i>
        Переглянути замовлення
    </button>
    <button id="db-read-deleted-button" class="waves-effect waves-light btn green accent-4"><i class="material-icons right">view_list</i>Переглянути включно з видаленими</button>
    <table id="callMeTable" style="display: none">
        <thead>
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Phone</th>
            <th>Moment</th>
            <th>Call Moment</th>
            <th>Delete/Restore</th>
        </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
</div>
