package com.hanksha.mple.controller

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class HomeController {

    @RequestMapping(value = '/', method = RequestMethod.GET)
    String home() {
        'index'
    }

    @GetMapping('/mple/info')
    @ResponseBody
    ResponseEntity info() {
        ResponseEntity.ok().build()
    }
}
