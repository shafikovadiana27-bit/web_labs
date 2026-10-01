package lab.web;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Модульные тесты сервлета BooksList. */
public class BooksListTest {

    private BooksList servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter output;

    @Before
    public void setUp() throws Exception {
        servlet = new BooksList();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        output = new StringWriter();

        when(response.getWriter())
                .thenReturn(new PrintWriter(output));

        when(request.getContextPath())
                .thenReturn("/web_laba09");
    }

    private void prepareRequest(
            String method,
            String lang,
            String name,
            String status
    ) {
        when(request.getMethod()).thenReturn(method);
        when(request.getParameter("lang")).thenReturn(lang);
        when(request.getParameter("name")).thenReturn(name);
        when(request.getParameter("status")).thenReturn(status);
    }

    private int countBooks(String html) {
        return html.split("<tr><td>", -1).length - 1;
    }

    private ResourceBundle bundle(String lang) {
        return ResourceBundle.getBundle(
                "Book", Locale.forLanguageTag(lang)
        );
    }

    @Test
    public void getReturnsAllBooksInRussian() throws Exception {
        prepareRequest("GET", "ru", "Иван", "all");

        servlet.doGet(request, response);

        String html = output.toString();

        assertTrue(html.contains("<html lang='ru'>"));
        assertTrue(html.contains("Иван"));
        assertTrue(html.contains(": GET</p>"));
        assertTrue(html.contains(
                "action='/web_laba09/BooksList'"
        ));
        assertEquals(4, countBooks(html));

        verify(request).setCharacterEncoding("UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response, atLeastOnce()).setContentType(
                "text/html;charset=UTF-8"
        );
        verify(response, atLeastOnce()).setLocale(
                Locale.forLanguageTag("ru")
        );
        verify(response, never()).sendError(
                anyInt(), anyString()
        );
    }

    @Test
    public void postReturnsUnreadBooksInEnglish() throws Exception {
        prepareRequest("POST", "en", "Ivan", "unread");

        servlet.doPost(request, response);

        String html = output.toString();
        ResourceBundle res = bundle("en");

        assertTrue(html.contains("<html lang='en'>"));
        assertTrue(html.contains(": POST</p>"));
        assertEquals(2, countBooks(html));

        assertTrue(html.contains(
                res.getString("book.2.title")
        ));
        assertTrue(html.contains(
                res.getString("book.4.title")
        ));
        assertFalse(html.contains(
                res.getString("book.1.title")
        ));
        assertFalse(html.contains(
                res.getString("book.3.title")
        ));

        verify(response, atLeastOnce()).setLocale(
                Locale.ENGLISH
        );
    }

    @Test
    public void readFilterReturnsOnlyReadBooks() throws Exception {
        prepareRequest("GET", "ru", "Иван", "read");

        servlet.doGet(request, response);

        String html = output.toString();
        ResourceBundle res = bundle("ru");

        assertEquals(2, countBooks(html));

        assertTrue(html.contains(
                res.getString("book.1.title")
        ));
        assertTrue(html.contains(
                res.getString("book.3.title")
        ));
        assertFalse(html.contains(
                res.getString("book.2.title")
        ));
        assertFalse(html.contains(
                res.getString("book.4.title")
        ));
    }

    @Test
    public void missingLanguageReturns406() throws Exception {
        prepareRequest("GET", null, "Иван", "all");

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_NOT_ACCEPTABLE,
                "Expected lang=ru or lang=en"
        );
        verify(response, never()).getWriter();
        assertEquals("", output.toString());
    }

    @Test
    public void unsupportedLanguageReturns406() throws Exception {
        prepareRequest("POST", "de", "Иван", "all");

        servlet.doPost(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_NOT_ACCEPTABLE,
                "Expected lang=ru or lang=en"
        );
        verify(response, never()).getWriter();
        assertEquals("", output.toString());
    }

    @Test
    public void languageIsCaseInsensitive() throws Exception {
        prepareRequest("GET", "RU", "Иван", "all");

        servlet.doGet(request, response);

        assertTrue(output.toString().contains(
                "<html lang='ru'>"
        ));
        assertEquals(4, countBooks(output.toString()));

        verify(response, never()).sendError(
                anyInt(), anyString()
        );
    }

    @Test
    public void nameIsTrimmedAndHtmlIsEscaped() throws Exception {
        prepareRequest(
                "GET", "ru", "  <Иван & \"Олег\">'  ", "all"
        );

        servlet.doGet(request, response);

        String html = output.toString();
        String escaped =
                "&lt;Иван &amp; &quot;Олег&quot;&gt;&#39;";

        assertTrue(html.contains(
                "value='" + escaped + "'"
        ));
        assertTrue(html.contains(
                ": " + escaped + "</h3>"
        ));
        assertFalse(html.contains("<Иван"));
    }

    @Test
    public void missingNameUsesDefaultReader() throws Exception {
        prepareRequest("GET", "ru", null, "all");

        servlet.doGet(request, response);

        String html = output.toString();

        assertTrue(html.contains(
                "name='name' value=''"
        ));
        assertTrue(html.contains(
                bundle("ru").getString("unnamed")
        ));
        assertEquals(4, countBooks(html));
    }

    @Test
    public void unknownFilterReturnsAllBooks() throws Exception {
        prepareRequest("GET", "ru", "Иван", "unknown");

        servlet.doGet(request, response);

        String html = output.toString();

        assertEquals(4, countBooks(html));
        assertTrue(html.contains(
                "<option value='all' selected>"
        ));
    }
}