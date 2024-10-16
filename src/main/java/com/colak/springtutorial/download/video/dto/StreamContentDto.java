package com.colak.springtutorial.download.video.dto;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public record StreamContentDto(boolean partial,
                               String mediaType,
                               long contentLength,
                               String contentRange,
                               StreamingResponseBody streamingResponseBody) {

}
