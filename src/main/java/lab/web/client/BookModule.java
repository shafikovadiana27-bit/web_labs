package lab.web.client;
import java.util.List;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Клиентская часть приложения со списком книг.
 */
public class BookModule implements EntryPoint {

    private final LibraryServiceAsync service =
            GWT.create(LibraryService.class);

    private final TextBox nameInput = new TextBox();
    private final ListBox statusInput = new ListBox();
    private final Button showButton = new Button("Показать книги");

    private final Label message = new Label();
    private final Label reader = new Label();
    private final Label count = new Label();
    private final FlexTable table = new FlexTable();

    /**
     * Создаёт интерфейс после загрузки GWT-модуля.
     */
    @Override
    public void onModuleLoad() {
        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(12);

        Label title = new Label("Библиотека");
        title.setStyleName("title");

        nameInput.setMaxLength(100);
        nameInput.getElement().setAttribute(
                "placeholder", "Введите имя читателя"
        );

        statusInput.addItem("Все книги", "all");
        statusInput.addItem("Прочитанные", "read");
        statusInput.addItem("Непрочитанные", "unread");

        message.setStyleName("message");
        table.setStyleName("books");

        panel.add(title);
        panel.add(new Label("Имя читателя:"));
        panel.add(nameInput);
        panel.add(new Label("Фильтр:"));
        panel.add(statusInput);
        panel.add(showButton);
        panel.add(message);
        panel.add(reader);
        panel.add(table);
        panel.add(count);

        RootPanel.get("app").add(panel);

        showButton.addClickHandler(event -> loadBooks());
    }

    private void loadBooks() {
        final String name = nameInput.getText().trim();
        String status = statusInput.getSelectedValue();

        table.removeAllRows();
        reader.setText("");
        count.setText("");

        if (name.isEmpty()) {
            message.setText("Введите имя читателя.");
            return;
        }

        showButton.setEnabled(false);
        message.setText("Получение данных с сервера...");

        service.getBooks(name, status, new AsyncCallback<List<String[]>>() {

            @Override
            public void onFailure(Throwable caught) {
                showButton.setEnabled(true);
                message.setText(
                        "Ошибка: " + caught.getClass().getName()
                                + " — " + caught.getMessage()
                );
            }

            @Override
            public void onSuccess(List<String[]> books) {
                showButton.setEnabled(true);
                message.setText("Данные получены с сервера через GWT RPC.");
                reader.setText("Читатель: " + name);

                table.setText(0, 0, "Автор");
                table.setText(0, 1, "Название");
                table.setText(0, 2, "Прочитана");
                table.getRowFormatter().setStyleName(0, "table-header");

                for (int i = 0; i < books.size(); i++) {
                    String[] book = books.get(i);

                    table.setText(i + 1, 0, book[0]);
                    table.setText(i + 1, 1, book[1]);
                    table.setText(i + 1, 2, book[2]);
                }

                count.setText("Количество книг: " + books.size());
            }
        });
    }

}