package rs.raf.springusers.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    String hello()
    {
        return "hello";
    }
}
