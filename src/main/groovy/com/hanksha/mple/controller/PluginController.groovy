package com.hanksha.mple.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping('/plugins')
class PluginController {

    @RequestMapping(value = '/{name}', method = RequestMethod.GET)
    String javascript(@PathVariable String name) {
        File file = new File("storage/plugins/$name/js/${name}.js")

        file.text
    }

    @RequestMapping(value = '/{name}/templates/{filename}', method = RequestMethod.GET)
    String javascript(@PathVariable String name, @PathVariable String filename) {
        File file = new File("storage/plugins/$name/templates/${filename}.html")

        file.text
    }

}
