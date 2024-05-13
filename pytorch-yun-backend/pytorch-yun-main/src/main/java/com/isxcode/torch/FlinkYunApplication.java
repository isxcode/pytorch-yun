package com.isxcode.torch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
@SpringBootApplication
public class FlinkYunApplication {

    public static void main(String[] args) {

        SpringApplication.run(FlinkYunApplication.class, args);
    }

    @RequestMapping(value = {"/*", "/home/**", "/share/**"})
    public String index() {

        return "index";
    }
}
