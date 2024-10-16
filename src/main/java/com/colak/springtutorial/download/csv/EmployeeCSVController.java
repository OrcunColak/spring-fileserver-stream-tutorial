package com.colak.springtutorial.download.csv;

import com.opencsv.ICSVWriter;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("api/v1/streamingresponsebodycsv")

public class EmployeeCSVController {
    private List<Book> books;

    public record Book(String title, String author, int year, double price) {
    }

    @PostConstruct
    private void postConstruct() {
        books = List.of(
                new Book("book1", "author1", 2023, 10),
                new Book("book2", "author2", 2024, 10)
        );
    }

    // http://localhost:8080/api/v1/streamingresponsebodycsv/download
    // Download as file
    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> download() {
        StreamingResponseBody streamingResponseBody = getStreamingResponseBody();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=employee.csv")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(streamingResponseBody);
    }

    // StreamingResponseBody is for asynchronous request processing where the application can write directly to the
    // response OutputStream without holding up the Servlet container thread.
    // If request is not  processed within the time-out duration Spring throws org.springframework.web.context.request.async.AsyncRequestTimeoutException
    private StreamingResponseBody getStreamingResponseBody() {
        // Use lambda for writeTo() method of StreamingResponseBody
        return outputStream -> {
            // Convert outputStream to Writer. Use Writer to write CSV
            try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                try {
                    // Use the StatefulBeanToCsv to convert bean to string and write into the response.
                    new StatefulBeanToCsvBuilder<Book>(writer)
                            .withQuotechar(ICSVWriter.DEFAULT_QUOTE_CHARACTER)
                            .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                            // Changes order of the list
                            // .withOrderedResults(false)
                            .build()
                            .write(books);
                } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        };
    }
}
