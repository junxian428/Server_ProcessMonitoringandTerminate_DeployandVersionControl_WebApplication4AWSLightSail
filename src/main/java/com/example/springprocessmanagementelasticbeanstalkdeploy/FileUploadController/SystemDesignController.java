package com.example.springprocessmanagementelasticbeanstalkdeploy.FileUploadController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SystemDesignController {
    
    @GetMapping("/systemdesign")
    public String showSystemForm() {
        return "systemdesign";
    }

}
