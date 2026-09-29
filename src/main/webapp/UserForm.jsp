<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.http.Cookie" %>
<%@ page import="java.net.URLDecoder,java.net.URLEncoder" %>
<%@ page import="java.time.ZonedDateTime,java.time.ZoneId" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="lab.web.BookService" %>

<%
    request.setCharacterEncoding("UTF-8");
    response.setHeader("Cache-Control", "no-store");

    // Считаем обращения к странице формы в текущей сессии.
    Integer visits = (Integer) session.getAttribute("visits");
    visits = visits == null ? 1 : visits + 1;
    session.setAttribute("visits", visits);

    String previousVisit = (String) session.getAttribute("lastVisit");

    String now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"))
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

    session.setAttribute("previousVisit",
            previousVisit == null ? "Первое обращение" : previousVisit);
    session.setAttribute("lastVisit", now);

    String name = "";
    String color = "#ffffff";
    String error = "";

    // Восстанавливаем сохранённые значения из Cookie.
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("lab7_name".equals(cookie.getName())) {
                try {
                    name = URLDecoder.decode(cookie.getValue(), "UTF-8");
                } catch (IllegalArgumentException e) {
                    name = "";
                }
            }

            if ("lab7_color".equals(cookie.getName())) {
                color = cookie.getValue();
            }
        }
    }

    if ("POST".equalsIgnoreCase(request.getMethod())) {
        String enteredName = request.getParameter("name");
        name = enteredName == null ? "" : enteredName.trim();
        color = request.getParameter("color");
    }

    // Разрешаем только цвет в формате #RRGGBB.
    if (color == null || !color.matches("#[0-9a-fA-F]{6}")) {
        color = "#ffffff";
    }

    if ("POST".equalsIgnoreCase(request.getMethod())) {
        if (name.isEmpty()) {
            error = "Введите имя пользователя.";
        } else if (name.length() > 100) {
            error = "Имя должно содержать не больше 100 символов.";
        } else {
            Cookie nameCookie = new Cookie(
                    "lab7_name",
                    URLEncoder.encode(name, "UTF-8")
            );

            Cookie colorCookie = new Cookie("lab7_color", color);

            String cookiePath = request.getContextPath();
            if (cookiePath.isEmpty()) {
                cookiePath = "/";
            }

            // Cookie сохраняются на 7 дней и относятся к этому приложению.
            for (Cookie cookie : new Cookie[]{nameCookie, colorCookie}) {
                cookie.setPath(cookiePath);
                cookie.setMaxAge(7 * 24 * 60 * 60);
                cookie.setHttpOnly(true);
                cookie.setSecure(request.isSecure());
                response.addCookie(cookie);
            }

            // Новый запрос позволит прочитать Cookie, полученные браузером.
            response.sendRedirect(response.encodeRedirectURL(
                    request.getContextPath() + "/UserInfo.jsp"
            ));
            return;
        }
    }
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Лабораторная работа № 7</title>
</head>
<body>
<h1>Настройки пользователя</h1>

<% if (!error.isEmpty()) { %>
<p style="color: red;">
    <%= BookService.escapeHtml(error) %>
</p>
<% } %>

<form action="<%= request.getContextPath() %>/UserForm.jsp"
      method="post"
      accept-charset="UTF-8">

    <p>
        <label for="name">Имя пользователя:</label>
        <input id="name"
               name="name"
               type="text"
               maxlength="100"
               required
               value="<%= BookService.escapeHtml(name) %>">
    </p>

    <p>
        <label for="color">Цвет страницы:</label>
        <input id="color"
               name="color"
               type="color"
               value="<%= color %>">
    </p>

    <button type="submit">Сохранить и показать</button>
</form>

<p>Обращений к форме в этой сессии: <%= visits %></p>
</body>
</html>