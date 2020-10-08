package scalable.solutions.oborder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Locale;

@Controller
public class UiController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Locale locale, Model model) {
        String language = locale.getLanguage();
        if ("ro".equalsIgnoreCase(language)) {
            model.addAttribute("username", "Utilizator:");
            model.addAttribute("password", "Parola:");
        } else {
            model.addAttribute("username", "Username:");
            model.addAttribute("password", "Password:");
        }
        return "index";
    }

    @RequestMapping(value = "/popup", method = RequestMethod.GET)
    public String popup() {
        return "pop-up";
    }
}
