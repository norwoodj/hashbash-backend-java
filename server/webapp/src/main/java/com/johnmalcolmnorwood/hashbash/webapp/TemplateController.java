package com.johnmalcolmnorwood.hashbash.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;


@Controller
public class TemplateController {

    @RequestMapping("/")
    public String indexPage() {
        return "index";
    }

    @RequestMapping("/generate-rainbow-table")
    public String generateRainbowTablePage(
            Model model,
            @RequestParam(value = "error", defaultValue = "") String error
    ) {
        if (!StringUtils.isEmpty(error)) {
            model.addAttribute("error", error);
        }

        return "generate-rainbow-table";
    }

    @RequestMapping("/rainbow-tables")
    public String rainbowTablePage(
        Model model,
        @RequestParam(value = "error", defaultValue = "") String error
    ) {
        if (!StringUtils.isEmpty(error)) {
            model.addAttribute("error", error);
        }

        return "rainbow-tables";
    }

    @RequestMapping("/search-rainbow-table")
    public Object rainbowTablePage(
            Model model,
            @RequestParam(value = "rainbowTableId", defaultValue = "-1") short rainbowTableId
    ) {
        if (rainbowTableId < 1) {
            ModelAndView redirect = new ModelAndView("redirect:/rainbow-tables");
            redirect.getModelMap().addAttribute(
                    "error",
                    "'rainbowTableId' query parameter must be provided to /search-rainbow-tables page"
            );

            return redirect;
        }

        model.addAttribute("rainbowTableId", rainbowTableId);
        return "search-rainbow-table";
    }
}

