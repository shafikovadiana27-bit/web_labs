<%@ page pageEncoding="UTF-8" %>

<table border="1" cellpadding="8">
    <tr>
        <th>Автор</th>
        <th>Название книги</th>
        <th>Прочитал</th>
    </tr>

    <% for (String[] book : books) { %>
    <tr>
        <td><%= BookService.escapeHtml(book[0]) %></td>
        <td><%= BookService.escapeHtml(book[1]) %></td>
        <td><%= "read".equals(book[2]) ? "Да" : "Нет" %></td>
    </tr>
    <% } %>
</table>