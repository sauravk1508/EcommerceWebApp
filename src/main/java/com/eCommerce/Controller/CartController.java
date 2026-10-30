package com.eCommerce.Controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eCommerce.Entity.CartItem;
import com.eCommerce.Entity.Product;
import com.eCommerce.Entity.ProductCategory;
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
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity) {
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
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserPrincipal) {
				UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
				Users user = userPrincipal.getUser();
				List<CartItem> cartItems = cartService.getCartItems(user.getId());
				BigDecimal totalAmount = BigDecimal.ZERO;
				for (CartItem cartItem : cartItems) {
					Product product = cartItem.getProduct();
					if (product != null && product.getPrice() != null) {
						BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
						totalAmount = totalAmount.add(itemTotal);
					}
				}
				model.addAttribute("cartItems", cartItems);
				model.addAttribute("totalAmount", totalAmount);
				return "Cart";
			}
			return "redirect:/login";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "An error occurred while loading your cart: " + e.getMessage());
			return "error"; // Make sure you have an 'error.html' or 'error.jsp' page
		}
	}


    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return "redirect:/cart/view";
    }
    // Method to populate categories for all requests
    @ModelAttribute("categories")
    public List<ProductCategory> populateCategories() {
        return productService.findAllProdCategory();
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
