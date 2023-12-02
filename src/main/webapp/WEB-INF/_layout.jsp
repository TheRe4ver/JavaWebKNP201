<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String pageBody = (String) request.getAttribute("page-body");
    String context = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <!-- Compiled and minified CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
    <!--Import Google Icon Font-->
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <!--Site CSS-->
    <link rel="stylesheet" href="<%=context%>/css/site.css?time=<%= new Date().getTime()%>">
    <title>Java web</title>
</head>
<body>

<nav>
    <div class="nav-wrapper green darken-4">

        <!-- Modal Trigger -->
        <a class="right modal-trigger auth-icon" href="#auth-modal">
            <i class="material-icons">exit_to_app</i>
        </a>

        <a href="<%= context %>/" class="site-logo right">Java</a>
        <ul id="nav-mobile">
            <li><a href="<%= context %>/jsp">Jsp</a></li>
            <li <%= "filters.jsp".equals(pageBody) ? "class='active'" : "" %> ><a href="<%= context %>/filters">Filters</a></li>
            <li <%= "ioc.jsp".equals(pageBody) ? "class='active'" : "" %> ><a href="<%= context %>/ioc">IoC</a></li>
            <li <%= "db.jsp".equals(pageBody) ? "class='active'" : "" %> ><a href="<%= context %>/db">DB</a></li>
            <li <%= "db.jsp".equals(pageBody) ? "class='active'" : "" %> ><a href="<%= context %>/spa">SPA</a></li>
            <li <%= "db.jsp".equals(pageBody) ? "class='active'" : "" %> ><a href="<%= context %>/ws">WS</a></li>
        </ul>
    </div>
</nav>

<div class="container">
<jsp:include page="<%= pageBody%>"/>
</div>

<footer class="page-footer green darken-4">
    <div class="container">
        <div class="row">
            <div class="col l6 s12">
                <h5 class="white-text">Footer Content</h5>
                <p class="grey-text text-lighten-4">You can use rows and columns here to organize your footer content.</p>
            </div>
            <div class="col l4 offset-l2 s12">
                <h5 class="white-text">Links</h5>
                <ul>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 1</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="footer-copyright">
        <div class="container">
            © 2023 It Step Uni
            <a class="grey-text text-lighten-4 right" href="#!">More Links</a>
        </div>
    </div>
</footer>

<!-- Modal Structure -->
<div id="auth-modal" class="modal">
    <div class="modal-content">
        <h4>Автентифікація на сайті</h4>
    <div class="row">
        <div class="input-field col s6">
            <i class="material-icons prefix">person</i>
            <input id="auth-login" type="text" class="validate">
            <label for="auth-login">Логін</label>
        </div>

        <div class="input-field col s6">
            <i class="material-icons prefix">lock</i>
            <input id="auth-password" type="password" class="validate">
            <label for="auth-password">Пароль</label>
        </div>
    </div>
    </div>
    <div class="modal-footer">
        <b id="auth-message"></b>
        <a href="<%=context%>/signup" class="modal-close waves-effect waves-green btn-flat green accent-4">Реєстрація</a>
        <a href="#!" id="auth-sign-in" class="waves-effect waves-green btn-flat green accent-3" >Вхід</a>
    </div>
</div>

<!-- Compiled and minified JavaScript -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
<!-- Site JS -->
<script src="<%=context%>/js/site.js"></script>
<script src="<%=context%>/js/spa.js?time=<%= new Date().getTime()%>"></script>
</body>
</html>




