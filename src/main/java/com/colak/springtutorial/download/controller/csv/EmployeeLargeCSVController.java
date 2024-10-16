package com.colak.springtutorial.download.controller.csv;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("api/v1/streamingresponsebodycsv")

public class EmployeeLargeCSVController {

    // http://localhost:8080/api/v1/streamingresponsebodycsv/downloadlarge
    // Download as file
    @GetMapping("/downloadlarge")
    public ResponseEntity<StreamingResponseBody> download() {
        StreamingResponseBody streamingResponseBody = getStreamingResponseBody();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employee-large.csv")
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
                writer.write("id,name,age\n");
                for (int i = 1; i <= 1_000_000; i++) {
                    writer.write(i + ",Name" + i + "," + (20 + i % 10) + "\n");
                    writer.flush(); // Flush periodically to send data to the client
                }
            }
        };
    }
}
