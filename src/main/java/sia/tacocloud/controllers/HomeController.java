package sia.tacocloud.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller         // The Controller
public class HomeController {
    @GetMapping("/")    // Handles requests for the root path
    public String home() {
        System.out.println("Hello World ~");
        return "home";  // Return the view name
    }
}
