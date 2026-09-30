package lab.web.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import lab.web.client.LibraryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Серверная часть сервиса книг.
 */
public class LibraryServiceImpl extends RemoteServiceServlet
        implements LibraryService {

    private static final long serialVersionUID = 1L;

    private static final String[][] BOOKS = {
            {"Михаил Булгаков", "Мастер и Маргарита", "read"},
            {"Виктор Пелевин", "Чапаев и Пустота", "unread"},
            {"Лев Толстой", "Война и мир", "read"},
            {"Фёдор Достоевский", "Идиот", "unread"}
    };

    /**
     * Проверяет параметры и фильтрует список книг.
     *
     * @param name имя читателя
     * @param status фильтр: all, read или unread
     * @return отфильтрованные сведения о книгах
     * @throws IllegalArgumentException при неверных параметрах
     */
    @Override
    public List<String[]> getBooks(String name, String status)
            throws IllegalArgumentException {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Введите имя читателя.");
        }

        if (name.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Имя должно содержать не более 100 символов."
            );
        }

        if (!"all".equals(status)
                && !"read".equals(status)
                && !"unread".equals(status)) {
            throw new IllegalArgumentException("Неизвестный фильтр книг.");
        }

        List<String[]> result = new ArrayList<>();

        for (String[] book : BOOKS) {
            if (!"all".equals(status) && !status.equals(book[2])) {
                continue;
            }

            result.add(new String[]{
                    book[0],
                    book[1],
                    "read".equals(book[2]) ? "Да" : "Нет"
            });
        }

        return result;
    }
}