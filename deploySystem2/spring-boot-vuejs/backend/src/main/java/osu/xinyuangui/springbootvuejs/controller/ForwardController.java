package osu.xinyuangui.springbootvuejs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Without this controller, the vue-router 'history' mode won't work
 */
@Controller
public class ForwardController {
    @RequestMapping(value = {"/single-file-code/**", "/web-code/**"})
    public String redirect() {
        // Forward to home page so that route is preserved.
        return "forward:/";
    }
}
