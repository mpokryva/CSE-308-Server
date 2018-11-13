package com.broncos.gerrymandering.gerrymandering;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mpokr on 11/12/2018.
 */
@RestController
public class TestController {

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name") String name) {
        return "Hello " + name;
    }
}
