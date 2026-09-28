package lab.web;

import java.util.ArrayList;
import java.util.List;

/**
 * Проверка данных читателя и выборка книг.
 */
public class BookService {

    /**
     * Проверяет имя читателя.
     *
     * @param name введённое имя
     * @return имя без пробелов по краям
     * @throws IllegalArgumentException если имя не заполнено
     */
    public static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Необходимо ввести имя читателя.");
        }

        return name.trim();
    }

    /**
     * Возвращает книги с учётом фильтра.
     *
     * @param status all, read или unread
     * @return список строк: автор, название, признак прочтения
     * @throws IllegalArgumentException если фильтр неизвестен
     */
    public static List<String[]> findBooks(String status) {
        if (!"all".equals(status)
                && !"read".equals(status)
                && !"unread".equals(status)) {
            throw new IllegalArgumentException("Неизвестный фильтр книг.");
        }

        String[][] books = {
                {"Михаил Булгаков", "Мастер и Маргарита", "read"},
                {"Виктор Пелевин", "Чапаев и Пустота", "unread"},
                {"Лев Толстой", "Война и мир", "read"},
                {"Фёдор Достоевский", "Идиот", "unread"}
        };

        List<String[]> result = new ArrayList<>();

        for (String[] book : books) {
            if ("all".equals(status) || status.equals(book[2])) {
                result.add(book);
            }
        }

        return result;
    }

    /**
     * Подготавливает строку для вывода в HTML.
     *
     * @param value исходная строка
     * @return экранированная строка
     */
    public static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}