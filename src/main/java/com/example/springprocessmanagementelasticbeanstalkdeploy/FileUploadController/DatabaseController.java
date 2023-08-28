package com.example.springprocessmanagementelasticbeanstalkdeploy.FileUploadController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DatabaseController {



    @GetMapping("/database")
    public String showDatabaesForm() {
        return "database";
    }

}
