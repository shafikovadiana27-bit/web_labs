package lab.web.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/** Сервис получения списка книг. */
@RemoteServiceRelativePath("library")
public interface LibraryService extends RemoteService {

    /**
     * Возвращает книги с учётом фильтра.
     *
     * @param name имя читателя
     * @param status выбранный фильтр
     * @return список строк с данными книг
     */
    List<String[]> getBooks(String name, String status)
            throws IllegalArgumentException;
}