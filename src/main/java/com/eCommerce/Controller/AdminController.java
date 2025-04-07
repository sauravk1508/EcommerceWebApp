package com.eCommerce.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eCommerce.Entity.Product;
import com.eCommerce.Service.ProductService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    // Method to list all products
    @GetMapping("/products")
    public ModelAndView listProducts(Model model) {
    	ModelAndView mav = new ModelAndView("admin");
    	
        List<Product> products = productService.getAllProduct();
        mav.addObject("products", products);
        return mav; // Return the view name for product listing
    }

    // Method to show the form for adding a product
    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product"; // Return the view name for adding a product
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        try {
            String uploadDir = "src/main/resources/static/uploads/";
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // Check if the image file already exists
            String imageFileName = imageFile.getOriginalFilename();
            File existingImageFile = new File(uploadDir + imageFileName);
            String relativeImagePath;

            if (existingImageFile.exists()) {
                // If the file exists, use the existing file details
                relativeImagePath = "/uploads/" + imageFileName;
            } else {
                // If it does not exist, save the new file
                relativeImagePath = "/uploads/" + imageFileName;
                // Save the file to the specified directory
                Path filePath = Paths.get(uploadDir + imageFileName);
                Files.copy(imageFile.getInputStream(), filePath);
            }

            // Save image details in product
            product.setImageName(imageFileName);
            product.setImageType(imageFile.getContentType());
            product.setImagePath(relativeImagePath); // Store the relative path

            // Save the product details in the database
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully.");
            return "redirect:/admin/products"; // Redirect to product list after success

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while saving the product image: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while adding the product: " + e.getMessage(), e);
        }
    }


    // Method to show the edit form for a product
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Integer idd, Model model) {
        Product product = productService.getProductById(idd);
        if (product != null) {
            model.addAttribute("product", product);
            return "product"; // Return the view name for editing a product
        } else {
            model.addAttribute("error", "Error while showing the edit form.");
            return "error"; // Redirect if product not found
        }
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Integer id, @ModelAttribute Product product,
                              @RequestParam MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        try {
            String uploadDir = "src/main/resources/static/uploads/";
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                redirectAttributes.addFlashAttribute("error", "Product not found for editing.");
                return "redirect:/admin/products"; // Redirect if the product does not exist
            }

            String imageFileName = imageFile.getOriginalFilename();
            File existingImageFile = new File(uploadDir + imageFileName);
            String relativeImagePath;

            if (!imageFile.isEmpty()) {
                // If a new image is uploaded, check if it already exists
                if (existingImageFile.exists()) {
                    // If the file exists, use the existing file details
                    relativeImagePath = "/uploads/" + imageFileName;
                } else {
                    // If it does not exist, save the new file
                    relativeImagePath = "/uploads/" + imageFileName;
                    Path filePath = Paths.get(uploadDir + imageFileName);
                    Files.copy(imageFile.getInputStream(), filePath);
                }

                // Save the new image details in the product
                product.setImageName(imageFileName);
                product.setImageType(imageFile.getContentType());
                product.setImagePath(relativeImagePath);
            } else {
                // Retain existing image data if no new image is uploaded
                product.setImageName(existingProduct.getImageName());
                product.setImageType(existingProduct.getImageType());
                product.setImagePath(existingProduct.getImagePath());
            }

            // Update the product details in the database
            productService.updateProduct(id, product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully.");
            return "redirect:/admin/products"; // Redirect to product list after success

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error while updating the product.");
            return "redirect:/admin/products/edit/" + id; // Return to the form with the error
        }
    }


 // Method to delete a product
    @GetMapping("/products/{id}")
    public String deleteProduct(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product != null) {
            // Get the image path and delete the file
            String imagePath = "src/main/resources/static/uploads/" + product.getImageName();
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                if (!imageFile.delete()) {
                    model.addAttribute("error", "Failed to delete the image file.");
                    return "error"; // Return error page if image deletion fails
                }
            }

            // Delete the product from the database
            productService.deleteProduct(id);
            return "redirect:/admin/products"; // Redirect to product list after deletion
        } else {
            model.addAttribute("error", "Error while deleting the Product, Please Try Again.");
            return "error"; // Return error page if product not found
        }
    }


    // Method to view a product
    @GetMapping("/products/{category}/{id}")
    public String viewProduct(@PathVariable String category, @PathVariable Integer id, Model model) {
        Product product = productService.getProductByCategoryAndId(category, id);
        if (product != null) {
            model.addAttribute("product", product);
            return "viewProduct";
        } else {
            model.addAttribute("error", "Product not found.");
            return "error"; // Return to the error page with the message
        }
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

    // Method to filter products by category
    @GetMapping("/products/category/{categoryName}")
    public String filterProductsByCategory(@PathVariable String categoryName, Model model) {
        List<Product> products = productService.getProductsByCategory(categoryName);
        model.addAttribute("products", products);
        model.addAttribute("categoryName", categoryName);
        return "admin";
    }

    // Method to search for products
    @GetMapping("/products/search")
    public String searchProduct(@RequestParam String keyword, Model model) {
        List<Product> products = productService.getSearchedProduct(keyword);
        if (products != null && !products.isEmpty()) {
            model.addAttribute("products", products);
            return "admin"; // Return the view name for displaying products
        } else {
            model.addAttribute("error", "Product not found.");
            return "error"; // Return error page if no products are found
        }
    }

    // Exception handler
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", ex.getMessage());
        return modelAndView;
    }

    // Method to get the logged-in username
    @ModelAttribute
    public void addAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        }
    }
}
