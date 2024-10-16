package com.colak.springtutorial.download.video;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    // http://localhost:8080/video
    @RequestMapping("/video")
    public String playVideo() {
        return "stream_video.html";
    }

}
