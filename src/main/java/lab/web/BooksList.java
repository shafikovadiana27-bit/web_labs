package lab.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Сервлет для отображения списка книг читателя.
 *
 * <p>Параметр инициализации libraryName задаёт название библиотеки.
 * Динамический параметр name задаёт имя читателя,
 * а status определяет фильтр по состоянию чтения.</p>
 *
 * @author Иван Тишко
 */
public class BooksList extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Название библиотеки из параметров инициализации. */
    private String libraryName;

    /**
     * Инициализирует сервлет и считывает название библиотеки.
     *
     * @throws ServletException если произошла ошибка инициализации
     */
    @Override
    public void init() throws ServletException {
        libraryName = getServletConfig()
                .getInitParameter("libraryName");

        if (libraryName == null || libraryName.isBlank()) {
            libraryName = "Учебная библиотека";
        }
    }

    /**
     * Формирует HTML-страницу со списком книг.
     *
     * @param request HTTP-запрос с параметрами name и status
     * @param response HTTP-ответ с HTML-страницей
     * @throws IOException если произошла ошибка записи ответа
     */
    protected void processRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        // Кодировка задаётся до чтения параметров.
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String name = request.getParameter("name");
        String status = request.getParameter("status");

        if (name == null || name.isBlank()) {
            name = "без имени";
        } else {
            name = name.trim();
        }

        if (!"read".equals(status) && !"unread".equals(status)) {
            status = "all";
        }

        String filterTitle = switch (status) {
            case "read" -> "Прочитанные";
            case "unread" -> "Непрочитанные";
            default -> "Все книги";
        };

        // Автор, название, признак прочтения.
        String[][] books = {
                {"Михаил Булгаков", "Мастер и Маргарита", "read"},
                {"Виктор Пелевин", "Чапаев и Пустота", "unread"},
                {"Лев Толстой", "Война и мир", "read"},
                {"Фёдор Достоевский", "Идиот", "unread"}
        };

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='ru'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Список книг</title>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h1>" + escapeHtml(libraryName) + "</h1>");
            out.println("<h2>Список книг читателя "
                    + escapeHtml(name) + "</h2>");

            out.println("<p>Фильтр: " + filterTitle + "</p>");
            out.println("<p>Метод запроса: "
                    + escapeHtml(request.getMethod()) + "</p>");

            out.println("<table border='1' cellpadding='8'>");
            out.println("<tr>"
                    + "<th>Автор</th>"
                    + "<th>Название книги</th>"
                    + "<th>Прочитал</th>"
                    + "</tr>");

            int count = 0;

            for (String[] book : books) {
                if (!"all".equals(status) && !status.equals(book[2])) {
                    continue;
                }

                out.println("<tr>");
                out.println("<td>" + escapeHtml(book[0]) + "</td>");
                out.println("<td>" + escapeHtml(book[1]) + "</td>");
                out.println("<td>"
                        + ("read".equals(book[2]) ? "Да" : "Нет")
                        + "</td>");
                out.println("</tr>");

                count++;
            }

            out.println("</table>");
            out.println("<p>Найдено книг: " + count + "</p>");

            String backUrl = request.getContextPath() + "/index.html";
            out.println("<p><a href='" + escapeHtml(backUrl)
                    + "'>Вернуться к форме</a></p>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Обрабатывает GET-запрос.
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @throws IOException если произошла ошибка записи ответа
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
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @throws IOException если произошла ошибка записи ответа
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        processRequest(request, response);
    }

    /**
     * Экранирует специальные символы для безопасного вывода в HTML.
     *
     * @param value исходная строка
     * @return строка с экранированными символами
     */
    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}