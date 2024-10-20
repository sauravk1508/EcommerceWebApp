package com.eCommerce.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eCommerce.Entity.Users;
import com.eCommerce.Service.UserService;



@Controller
public class AuthController {
	@Autowired
	UserService userService;
	
	@GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Users());
        return "register";
    }
	
	@PostMapping("/register")
	public String register(@ModelAttribute Users user, Model model) {
		try {
            userService.register(user);
           
            return "redirect:/login";
        } catch (Exception e) {
           
            model.addAttribute("user", user); // Keep the input values
            return "register"; // Return to the registration form
        }
	}
	
	@GetMapping("/login")
    public String login() {
        return "login";
    }
	
	  @GetMapping("/logout")
	    public String logout() {
	        // This method will be called for logout. Spring Security handles the session invalidation.
	        return "redirect:/login"; // Redirect to login page after logout
	    }

	    
}
