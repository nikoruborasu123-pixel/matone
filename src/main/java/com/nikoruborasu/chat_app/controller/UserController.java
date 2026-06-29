package com.nikoruborasu.chat_app.controller;

import jakarta.servlet.http.HttpSession;
import com.nikoruborasu.chat_app.entity.User;
import com.nikoruborasu.chat_app.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;

@Controller

public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        User existingUser = userRepository.findByUsername(username);

        if (existingUser != null) {

            model.addAttribute("error", "そのユーザー名は既に使われています");

            return "register";
        }

        User user = new User();

        user.setUsername(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        user.setPassword(encoder.encode(password));

        userRepository.save(user);

        return "redirect:/";
    }
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        User user = userRepository.findByUsername(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (user != null && encoder.matches(password, user.getPassword())) {

            session.setAttribute("loginUser",user);

            return "redirect:/";
        }

        return "redirect:/login";
    }
    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}