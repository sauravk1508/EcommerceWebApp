package com.eCommerce.Controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eCommerce.Entity.CartItem;
import com.eCommerce.Entity.Product;
import com.eCommerce.Entity.UserPrincipal;
import com.eCommerce.Entity.Users;
import com.eCommerce.Service.CartService;
import com.eCommerce.Service.ProductService;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @PostMapping("/cart/view")
    public String addToCart(@RequestParam Integer productId, @RequestParam int quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            Product product = productService.getProductById(productId);

            if (product != null) {
                if (principal instanceof UserPrincipal) {
                    UserPrincipal userPrincipal = (UserPrincipal) principal;
                    Users user = userPrincipal.getUser();
                    cartService.addToCart(user, product, quantity);
                }
            } else {
                return "redirect:/error?error=Product not found";
            }
            return "redirect:/cart/view";
        }
        return "redirect:/login";
    }

    @GetMapping("/cart/view")
    public String viewCart(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                Users user = userPrincipal.getUser();

                List<CartItem> cartItems = cartService.getCartItems(user.getId());

                // Calculate the total amount
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (CartItem cartItem : cartItems) {
                    Product product = cartItem.getProduct();
                    if (product != null) {
                        // Directly use the image path to display in the view
                    }
                    BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                    totalAmount = totalAmount.add(itemTotal);
                }

                model.addAttribute("cartItems", cartItems);
                model.addAttribute("totalAmount", totalAmount); // Add total amount to the model

                return "Cart"; // Make sure your view is able to render the cart items
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return "redirect:/cart/view";
    }
    // Method to populate categories for all requests
    @ModelAttribute("categories")
    public List<String> populateCategories() {
        return getCategories();
    }

    // Private method to get the list of categories
    private List<String> getCategories() {
        return Arrays.asList("Laptop", "Headphone", "Mobile", "Electronics", "Toys", "Fashion");
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String username = userPrincipal.getUsername();
            model.addAttribute("username", username);
            Users user = userPrincipal.getUser();
            model.addAttribute("userDetails", user);
        }
    }
}
