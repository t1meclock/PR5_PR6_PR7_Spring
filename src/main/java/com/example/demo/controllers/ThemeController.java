package com.example.demo.controllers;

import com.example.demo.models.Theme;
import com.example.demo.models.User;
import com.example.demo.repo.ThemeRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/theme")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class ThemeController {
    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public String themeMain(Model model) {
        Iterable<Theme> themes = themeRepository.findAll();

        model.addAttribute("themes", themes);

        return "theme/main";
    }

    @GetMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String themeAdd(Theme theme, Model model)
    {
        Iterable<User> users = userRepository.findAll();

        model.addAttribute("users", users);

        return "theme/add-theme";
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String bookPostAdd(
            @ModelAttribute("theme") @Valid Theme theme,
            BindingResult bindingResult,
            Model model
    )
    {
        if (bindingResult.hasErrors()) {
            Iterable<User> users = userRepository.findAll();

            model.addAttribute("users", users);

            return "theme/add-theme";
        }

        themeRepository.save(theme);

        return "redirect:/";
    }
}
