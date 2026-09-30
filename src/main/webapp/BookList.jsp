<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         errorPage="/WEB-INF/ErrorManager.jsp" %>
<%@ page import="java.util.List,java.util.ArrayList,lab.web.BookService" %>

<%
    request.setCharacterEncoding("UTF-8");

    boolean submitted = "POST".equalsIgnoreCase(request.getMethod())
            || request.getParameter("name") != null;

    String name = "";
    String status = request.getParameter("status");

    if (status == null) {
        status = "all";
    }

    List<String[]> books = new ArrayList<>();

    if (submitted) {
        name = BookService.validateName(request.getParameter("name"));
        books = BookService.findBooks(status);
    }
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Список книг — JSP</title>
</head>
<body>
<h1>Библиотека</h1>
<p>
    Пользователь:
    <%= BookService.escapeHtml(request.getRemoteUser()) %>
</p>

<p>
    Аутентификация:
    <%= BookService.escapeHtml(request.getAuthType()) %>
</p>

<p>
    Роль reader:
    <%= request.isUserInRole("reader") ? "Да" : "Нет" %>
</p>

<p>
    Защищённое соединение HTTPS:
    <%= request.isSecure() ? "Да" : "Нет" %>
</p>

<form action="<%= request.getContextPath() %>/BookList.jsp"
      method="post"
      accept-charset="UTF-8">

    <p>
        <label for="name">Имя читателя:</label>
        <input id="name"
               name="name"
               type="text"
               value="<%= BookService.escapeHtml(name) %>">
    </p>

    <p>
        <label for="status">Какие книги показать:</label>

        <select id="status" name="status">
            <option value="all"
                    <%= "all".equals(status) ? "selected" : "" %>>
                Все книги
            </option>

            <option value="read"
                    <%= "read".equals(status) ? "selected" : "" %>>
                Прочитанные
            </option>

            <option value="unread"
                    <%= "unread".equals(status) ? "selected" : "" %>>
                Непрочитанные
            </option>
        </select>
    </p>

    <button type="submit">Показать книги</button>
</form>

<% if (submitted) { %>
<h2>Список книг читателя <%= BookService.escapeHtml(name) %></h2>

<%@ include file="/WEB-INF/ListData.jsp" %>

<p>Найдено книг: <%= books.size() %></p>
<% } %>
</body>
</html>