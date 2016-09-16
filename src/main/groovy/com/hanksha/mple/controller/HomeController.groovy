package com.hanksha.mple.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by vivien on 8/23/16.
 */

@Controller
class HomeController {

    @RequestMapping(value = '/', method = RequestMethod.GET)
    String home() {
        'index'
    }

}
