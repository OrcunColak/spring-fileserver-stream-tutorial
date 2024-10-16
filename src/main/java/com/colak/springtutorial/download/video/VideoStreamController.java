package com.colak.springtutorial.download.video;

import com.colak.springtutorial.download.video.dto.StreamContentDto;
import com.colak.springtutorial.download.video.service.ObjectStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


@RestController
@RequestMapping("/stream")
@RequiredArgsConstructor
public class VideoStreamController {

    private final ObjectStorage objectStorage;

    @GetMapping("/{fileName}")
    public ResponseEntity<StreamingResponseBody> stream(@PathVariable String fileName,
                                                        @RequestHeader(value = "Range", required = false) String range) {
        StreamContentDto streamContentDto = objectStorage.getStreamContent(fileName, range);
        return asStreamResponse(streamContentDto);
    }

    private ResponseEntity<StreamingResponseBody> asStreamResponse(StreamContentDto streamContent) {
        if (streamContent.partial()) {
            return asPartialStreamResponse(streamContent);
        } else {
            return asFullStreamResponse(streamContent);
        }
    }

    private ResponseEntity<StreamingResponseBody> asPartialStreamResponse(StreamContentDto streamingContent) {
        return ResponseEntity
                .status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.CONTENT_TYPE, streamingContent.mediaType())
                .header(HttpHeaders.CONTENT_LENGTH, streamingContent.contentLength())
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE, streamingContent.contentRange())
                .body(streamingContent.streamingResponseBody());
    }

    private ResponseEntity<StreamingResponseBody> asFullStreamResponse(StreamContentDto streamingContent) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, streamingContent.mediaType())
                .header(HttpHeaders.CONTENT_LENGTH, streamingContent.contentLength())
                .body(streamingContent.streamingResponseBody());
    }

}
