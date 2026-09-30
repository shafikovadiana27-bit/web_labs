package lab.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Отображает список книг и форму на русском или английском языке.
 * Язык задаётся параметром lang, имя — name, фильтр — status.
 */
public class BooksList extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Формирует локализованную страницу.
     *
     * @param request запрос пользователя
     * @param response ответ сервера
     * @throws IOException при ошибке вывода
     */
    protected void processRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        request.setCharacterEncoding("UTF-8");

        String lang = request.getParameter("lang");

        if (lang == null
                || (!"ru".equalsIgnoreCase(lang)
                && !"en".equalsIgnoreCase(lang))) {
            response.sendError(
                    HttpServletResponse.SC_NOT_ACCEPTABLE,
                    "Expected lang=ru or lang=en"
            );
            return;
        }

        lang = lang.toLowerCase(Locale.ROOT);

        Locale locale = "en".equals(lang)
                ? Locale.ENGLISH
                : Locale.forLanguageTag("ru");

        ResourceBundle res = ResourceBundle.getBundle("Book", locale);

        response.setLocale(locale);
        response.setContentType("text/html;charset=UTF-8");

        String name = request.getParameter("name");
        name = name == null ? "" : name.trim();

        String reader = name.isEmpty()
                ? res.getString("unnamed")
                : name;

        String status = request.getParameter("status");

        if (!"read".equals(status) && !"unread".equals(status)) {
            status = "all";
        }

        String action = request.getContextPath() + "/BooksList";
        boolean[] readFlags = {true, false, true, false};
        response.setLocale(locale);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='" + lang + "'>");
            out.println("<head><meta charset='UTF-8'>");
            out.println("<title>" + text(res, "title") + "</title>");
            out.println("</head><body>");

            out.println("<h1>" + text(res, "library") + "</h1>");
            out.println("<h2>" + text(res, "title") + "</h2>");

            out.println("<form action='" + escapeHtml(action)
                    + "' method='get' accept-charset='UTF-8'>");

            out.println("<p><label for='name'>"
                    + text(res, "name") + ":</label> ");
            out.println("<input id='name' name='name' value='"
                    + escapeHtml(name) + "'></p>");

            out.println("<p><label for='lang'>"
                    + text(res, "language") + ":</label> ");
            out.println("<select id='lang' name='lang'>");

            out.println("<option value='ru'"
                    + ("ru".equals(lang) ? " selected" : "")
                    + ">Русский</option>");

            out.println("<option value='en'"
                    + ("en".equals(lang) ? " selected" : "")
                    + ">English</option>");

            out.println("</select></p>");

            out.println("<p><label for='status'>"
                    + text(res, "filter") + ":</label> ");
            out.println("<select id='status' name='status'>");

            for (String value : new String[]{"all", "read", "unread"}) {
                out.println("<option value='" + value + "'"
                        + (value.equals(status) ? " selected" : "")
                        + ">" + text(res, value) + "</option>");
            }

            out.println("</select></p>");

            out.println("<button type='submit'>"
                    + text(res, "submit") + " — GET</button>");

            out.println("<button type='submit' formmethod='post'>"
                    + text(res, "submit") + " — POST</button>");

            out.println("</form>");

            out.println("<h3>" + text(res, "reader") + ": "
                    + escapeHtml(reader) + "</h3>");

            out.println("<p>" + text(res, "method") + ": "
                    + escapeHtml(request.getMethod()) + "</p>");

            out.println("<table border='1' cellpadding='8'>");
            out.println("<tr><th>" + text(res, "author")
                    + "</th><th>" + text(res, "book.title")
                    + "</th><th>" + text(res, "book.read")
                    + "</th></tr>");

            int count = 0;

            for (int i = 0; i < readFlags.length; i++) {
                boolean isRead = readFlags[i];

                if ("read".equals(status) && !isRead
                        || "unread".equals(status) && isRead) {
                    continue;
                }

                String prefix = "book." + (i + 1);

                out.println("<tr><td>"
                        + text(res, prefix + ".author")
                        + "</td><td>"
                        + text(res, prefix + ".title")
                        + "</td><td>"
                        + text(res, isRead ? "yes" : "no")
                        + "</td></tr>");

                count++;
            }

            out.println("</table>");
            out.println("<p>" + text(res, "count") + ": " + count + "</p>");
            out.println("</body></html>");
        }
    }

    /**
     * Обрабатывает GET-запрос.
     *
     * @param request запрос пользователя
     * @param response ответ сервера
     * @throws IOException при ошибке вывода
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        processRequest(request, response);
    }

    /**
     * Обрабатывает POST-запрос.
     *
     * @param request запрос пользователя
     * @param response ответ сервера
     * @throws IOException при ошибке вывода
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        processRequest(request, response);
    }

    private static String text(ResourceBundle res, String key) {
        return escapeHtml(res.getString(key));
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}