package com.eCommerce.Controller;

import com.eCommerce.Entity.Product;
import com.eCommerce.Service.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AdminController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String listProducts(Model model) {
    	// Fetch all products
        List<Product> products = productService.getAllProduct();

        // For each product, convert the imageData (byte[]) to a Base64 string and store it
        List<Product> productsWithImages = products.stream().map(product -> {
            if (product.getImageData() != null && product.getImageData().length > 0) {
                String base64Image = Base64.encodeBase64String(product.getImageData());
                product.setBase64Image(base64Image); // Assuming Product class has a `base64Image` field
            }
            return product;
        }).collect(Collectors.toList());

        // Add products with Base64 images to the model
        model.addAttribute("products", productsWithImages);
    	
//        model.addAttribute("products", productService.getAllProduct()); // Add products to the model
        return "admin"; // Return the view name for product listing
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product()); // Add an empty product object to the model for the form
        return "product"; // Return the view name for adding a product
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product,
            					@RequestParam MultipartFile imageFile ,Model model) {
        try {
        	 // Save image details in product
            product.setImageName(imageFile.getOriginalFilename());
            product.setImageType(imageFile.getContentType());
            product.setImageData(imageFile.getBytes()); // Convert file to byte array
            
            productService.saveProduct(product);
            return "redirect:/products"; // Redirect to product list after success
        } catch (Exception e) {
            e.printStackTrace(); // Log the actual error
            model.addAttribute("error", "Error while adding the product."); // Add error message to the model
            model.addAttribute("product", product); // Add the current product to the model to retain input values
            return "product"; // Return to the form with the error
        }   
    }

    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Integer id, Model model) {
    	
    	
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get()); // Add the product to the model for editing
            return "product"; // Return the view name for editing a product
        } else {
            return "redirect:/products"; // Redirect if product not found
        }
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Integer id, @ModelAttribute Product product, 
    							@RequestParam MultipartFile imageFile, Model model) {
        try {
        	if (!imageFile.isEmpty()) {
                product.setImageName(imageFile.getOriginalFilename());
                product.setImageType(imageFile.getContentType());
                product.setImageData(imageFile.getBytes());
            }else {
            	// Retain existing image data if no new image is uploaded
                Optional<Product> existingProductOpt = productService.getProductById(id);
                if (existingProductOpt.isPresent()) {
                    Product existingProduct = existingProductOpt.get();
                    product.setImageData(existingProduct.getImageData());  // Retain existing image data
                    product.setImageType(existingProduct.getImageType());  // Retain existing image type
                    product.setImageName(existingProduct.getImageName());  // Retain existing image name
                }
            }
            productService.updateProduct(id, product); // Update the product
            return "redirect:/products"; // Redirect to product list after success
        } catch (Exception e) {
            e.printStackTrace(); // Log the actual error
            model.addAttribute("error", "Error while updating the product."); // Add error message to the model
            model.addAttribute("product", product); // Add the product back to the model for the form
            return "product"; // Return to the form with the error
        }
    }

    @GetMapping("/products/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            productService.deleteProduct(id);
            return "redirect:/products"; // Redirect to product list after deletion
        } else {
            return "error"; // Return error page if product not found
        }
    }
    
  
    
    
 // To get the logged-in username
    @ModelAttribute
    public void addAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // Get the logged-in username
            model.addAttribute("username", username); // Add username to the model for use in the views
        }
    }
}
