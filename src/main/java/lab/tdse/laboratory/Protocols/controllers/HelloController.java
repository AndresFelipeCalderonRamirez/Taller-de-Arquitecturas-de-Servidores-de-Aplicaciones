package lab.tdse.laboratory.Protocols.controllers;

import lab.tdse.laboratory.Protocols.annotations.GetMapping;
import lab.tdse.laboratory.Protocols.annotations.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public static String index() {
        return "Greetings from Spring Boot!";
    }
    @GetMapping("/pi")
    public static String getPi() {
        return "PI = " + Math.PI;
    }
    @GetMapping("/hello")
    public static String hola() {
        return "LIMBUS COMPANYYYYYYYYYYYY";
    }
}

