package lab.web.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/** Асинхронный интерфейс сервиса книг. */
public interface LibraryServiceAsync {

    /**
     * Запрашивает список книг.
     *
     * @param name имя читателя
     * @param status выбранный фильтр
     * @param callback обработчик ответа
     */
    void getBooks(
            String name,
            String status,
            AsyncCallback<List<String[]>> callback
    );
}