package com.api.demo.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/Assess")
@CrossOrigin
public class ApiController {

    @GetMapping("/api/info")
    public String getWarehouse(){
        return "############# The API call is a success #############";
    }
}