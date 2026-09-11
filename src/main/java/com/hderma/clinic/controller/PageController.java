package com.hderma.clinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "redirect:/admin/index.html";
    }

    @GetMapping("/about/{page}")
    public String about(@PathVariable String page) {
        return "about/" + page;
    }

    @GetMapping("/service/{page}")
    public String service(@PathVariable String page) {
        return "service/" + page;
    }

    @GetMapping("/inquiry/{page}")
    public String inquiry(@PathVariable String page) {
        return "inquiry/" + page;
    }

    @GetMapping("/participate/{page}")
    public String participate(@PathVariable String page) {
        return "participate/" + page;
    }

    @GetMapping("/irb")
    public String irb() {
        return "irb/index";
    }

    @GetMapping("/auth/{page}")
    public String auth(@PathVariable String page) {
        return "auth/" + page;
    }

    @GetMapping("/irb/detail")
    public String irbDetail() {
        return "irb/detail";
    }

    @GetMapping("/about/notice/{id}")
    public String noticeDetail(@PathVariable Long id) {
        return "about/notice-detail";
    }

    @GetMapping("/about/newsletter/{id}")
    public String newsletterDetail(@PathVariable Long id) {
        return "about/newsletter-detail";
    }
}