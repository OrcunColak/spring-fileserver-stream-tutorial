package com.colak.springtutorial.download.controller.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * See <a href="https://blog.devgenius.io/efficient-large-scale-data-streaming-spring-boot-and-javascript-guide-d0139b67a0a3">...</a>
 */
@RestController
@RequestMapping("api/v1/streamingresponsebodyjson")
public class EmployeeJsonController {

    private final List<Employee> employees = new ArrayList<>();

    private record Employee(int id, String name, int departmentId) {
    }

    @PostConstruct
    private void postConstruct() {
        // Emulate having 10k rows
        // For 10k you won't notice significant improvement
        // Increase this number, and you will see how performance gets impacted
        for (int i = 1; i <= 1_000_000; i++) {
            int departmentId = (int) (Math.random() * 10000);
            Employee employee = new Employee(i, "name " + i, departmentId);
            employees.add(employee);
        }
    }

    // http://localhost:8080/api/v1/streamingresponsebodyjson/download
    // Download as JSON not file
    @GetMapping(value = "/download")
    public ResponseEntity<StreamingResponseBody> streamEmployees() {
        StreamingResponseBody responseBody = getStreamingResponseBody();
        return ResponseEntity.
                ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody);
    }

    // StreamingResponseBody is for asynchronous request processing where the application can write directly to the
    // response OutputStream without holding up the Servlet container thread.
    // If request is not  processed within the time-out duration Spring throws org.springframework.web.context.request.async.AsyncRequestTimeoutException
    private StreamingResponseBody getStreamingResponseBody() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Use lambda for writeTo() method of StreamingResponseBody
        return outputStream -> {
            for (Employee employee : employees) {
                String jsonChunk = objectMapper.writeValueAsString(employee);
                outputStream.write(jsonChunk.getBytes());
                // We have to add a token so that we can split it using this in client side
                outputStream.write("#".getBytes());
            }
        };
    }
}
