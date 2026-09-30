<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         isErrorPage="true" %>
<%@ page import="lab.web.BookService" %>

<%
    boolean inputError = exception instanceof IllegalArgumentException;

    response.setStatus(inputError ? 400 : 500);

    String message = inputError
            ? exception.getMessage()
            : "Не удалось обработать запрос. Попробуйте ещё раз.";
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Ошибка</title>
</head>
<body>
<h1>Ошибка обработки запроса</h1>

<p><%= BookService.escapeHtml(message) %></p>

<a href="<%= request.getContextPath() %>/BookList.jsp">
    Вернуться к форме
</a>
</body>
</html>