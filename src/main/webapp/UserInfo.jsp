<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.http.Cookie" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="lab.web.BookService" %>

<%
    response.setHeader("Cache-Control", "no-store");

    String name = "";
    String color = "#ffffff";
    boolean hasName = false;
    boolean hasColor = false;

    // Читаем Cookie, которые браузер прислал в новом запросе.
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("lab7_name".equals(cookie.getName())) {
                try {
                    name = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    hasName = !name.isBlank();
                } catch (IllegalArgumentException e) {
                    hasName = false;
                }
            }

            if ("lab7_color".equals(cookie.getName())) {
                String value = cookie.getValue();

                if (value.matches("#[0-9a-fA-F]{6}")) {
                    color = value;
                    hasColor = true;
                }
            }
        }
    }

    // Получаем данные, сохранённые первой JSP-страницей в сессии.
    Integer visits = (Integer) session.getAttribute("visits");
    String lastVisit = (String) session.getAttribute("lastVisit");
    String previousVisit = (String) session.getAttribute("previousVisit");

    String visitsText = visits == null ? "Нет данных" : visits.toString();
    String lastVisitText = lastVisit == null ? "Нет данных" : lastVisit;
    String previousVisitText = previousVisit == null
            ? "Нет данных" : previousVisit;

    // Подбираем цвет текста, чтобы он читался на выбранном фоне.
    int red = Integer.parseInt(color.substring(1, 3), 16);
    int green = Integer.parseInt(color.substring(3, 5), 16);
    int blue = Integer.parseInt(color.substring(5, 7), 16);

    double brightness = 0.299 * red + 0.587 * green + 0.114 * blue;
    String textColor = brightness < 128 ? "#ffffff" : "#000000";
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Cookie и сессия</title>
    <style>
        body {
            background-color: <%= color %>;
            color: <%= textColor %>;
            font-family: Arial, sans-serif;
            padding: 24px;
        }

        table {
            border-collapse: collapse;
        }

        th, td {
            border: 1px solid currentColor;
            padding: 10px;
            text-align: left;
        }

        a {
            color: inherit;
        }
    </style>
</head>
<body>
<h1>Данные пользователя</h1>

<% if (!hasName || !hasColor) { %>
<p>
    Cookie с настройками отсутствуют или некорректны.
    Заполните форму и разрешите Cookie для этого сайта.
</p>
<% } %>

<h2>Содержимое Cookie</h2>

<table>
    <tr>
        <th>Cookie</th>
        <th>Значение</th>
    </tr>
    <tr>
        <td>lab7_name</td>
        <td>
            <%= BookService.escapeHtml(
                    hasName ? name : "Не сохранено") %>
        </td>
    </tr>
    <tr>
        <td>lab7_color</td>
        <td><%= hasColor ? color : "Не сохранено" %></td>
    </tr>
</table>

<h2>Переменные сессии</h2>

<table>
    <tr>
        <th>Параметр</th>
        <th>Значение</th>
    </tr>
    <tr>
        <td>Количество обращений к форме</td>
        <td><%= BookService.escapeHtml(visitsText) %></td>
    </tr>
    <tr>
        <td>Последнее обращение к форме, Москва</td>
        <td><%= BookService.escapeHtml(lastVisitText) %></td>
    </tr>
    <tr>
        <td>Предыдущее обращение к форме, Москва</td>
        <td><%= BookService.escapeHtml(previousVisitText) %></td>
    </tr>
</table>

<p>
    <a href="<%= request.getContextPath() %>/UserForm.jsp">
        Вернуться к форме
    </a>
</p>
</body>
</html>