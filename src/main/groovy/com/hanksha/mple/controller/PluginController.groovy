package com.hanksha.mple.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by vivien on 9/3/16.
 */

@RestController
@RequestMapping('/plugins')
class PluginController {

    @RequestMapping(value = '/{name}', method = RequestMethod.GET)
    String javascript(@PathVariable String name) {
        File file = new File("storage/plugins/$name/js/${name}.js")

        file.text
    }

}
