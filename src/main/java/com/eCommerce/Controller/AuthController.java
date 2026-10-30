package com.eCommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eCommerce.Entity.Users;
import com.eCommerce.Service.UserService;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Users());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@Validated @ModelAttribute Users user,BindingResult result,Model model) {
        // Check if username already exists (case-sensitive)
        if (userService.usernameExists(user.getUsername())) {
            result.rejectValue("username", "error.username", "Username already exists");
            return "register";
        }
        
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            // Encode password before saving
            userService.register(user);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. Please try again.");
            return "register";
        }
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                       @RequestParam(required = false) String registered,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (registered != null) {
            model.addAttribute("registered", "Registration successful! Please login.");
        }
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}