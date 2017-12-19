package com.lifenautjoe.bol.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// For self-containment sake.
// This forwarding shouldn't be done on application level but server level.

@Controller
public class ForwardingController {
    @RequestMapping("/{path:[^\\.]+}/**")
    public String forward() {
        return "forward:/";
    }
}