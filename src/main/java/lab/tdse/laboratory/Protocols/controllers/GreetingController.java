package lab.tdse.laboratory.Protocols.controllers;

import lab.tdse.laboratory.Protocols.annotations.GetMapping;
import lab.tdse.laboratory.Protocols.annotations.RequestParam;
import lab.tdse.laboratory.Protocols.annotations.RestController;

@RestController
public class GreetingController {
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}