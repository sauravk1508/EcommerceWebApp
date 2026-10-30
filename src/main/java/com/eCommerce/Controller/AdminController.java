package com.eCommerce.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

import com.eCommerce.Entity.Brand;
import com.eCommerce.Entity.ImageDetails;
import com.eCommerce.Entity.Product;
import com.eCommerce.Entity.ProductCategory;
import com.eCommerce.Entity.ProductSubCategory;
import com.eCommerce.Service.ProductService;

@RemoteProxy(name = "adminController")
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
    	List<Brand> brandList = productService.findAllBrands();
    	model.addAttribute("brandList", brandList);
        model.addAttribute("product", new Product());
        return "product"; // Return the view name for adding a product
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
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
            ImageDetails imageObj = new ImageDetails();

            // Save image details in product
            imageObj.setName(imageFileName);
            imageObj.setType(imageFile.getContentType());
            imageObj.setPath(relativeImagePath); // Store the relative path
            
            productService.saveImageDetails(imageObj);
            product.generateProductNumber();
            product.setImageDetails(imageObj);
            // Save the product details in the database
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully.");
            return "redirect:/admin/products"; // Redirect to product list after success

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while saving the product image: " + e.getMessage(), e);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Error while adding the product: " + e.getMessage(), e);
        }
    }


    // Method to show the edit form for a product
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long idd, Model model) {
        Product product = productService.getProductById(idd);
        List<Brand> brandList = productService.findAllBrands();
    	model.addAttribute("brandList", brandList);
        if (product != null) {
            model.addAttribute("product", product);
            return "product"; // Return the view name for editing a product
        } else {
            model.addAttribute("error", "Error while showing the edit form.");
            return "error"; // Redirect if product not found
        }
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, @ModelAttribute Product product,
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
                ImageDetails imageObj = new ImageDetails();
                imageObj.setName(imageFileName);
                imageObj.setType(imageFile.getContentType());
                imageObj.setPath(relativeImagePath); 
                productService.saveImageDetails(imageObj);
                product.setImageDetails(imageObj);
                // Save the new image details in the product
            }  
          product.setProductNumber(existingProduct.getProductNumber());
            existingProduct.copyValuesFrom(product);
            // Update the product details in the database
            productService.updateProduct(id, existingProduct);
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
    public String deleteProduct(@PathVariable Long id, Model model) {
    	try {
	        Product product = productService.getProductById(id);
	        if (product != null) {
	            // Get the image path and delete the file
	            String imagePath = "src/main/resources/static/uploads/" + product.getImageDetails().getName();
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
    	}catch (DataIntegrityViolationException ex) {
            // This exception is thrown when a foreign key constraint prevents deletion
            ex.printStackTrace();
            model.addAttribute("error", "Cannot delete this Product as it is associated with one or more User's Cart Items.");
            return "error"; 

        } catch (ConstraintViolationException ex) {
            // Hibernate-specific constraint violation
            ex.printStackTrace();
            model.addAttribute("error", "Delete failed: This Product is linked to other records (foreign key constraint).");
            return "error"; 

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred while deleting the product: " + e.getMessage());
            return "error"; 
        }
        
    }
    

    // Method to view a product
    @GetMapping("/products/{categoryId}/{id}")
    public String viewProduct(@PathVariable Long categoryId, @PathVariable Long id, Model model) {
        Product product = productService.getProductByCategoryAndId(categoryId, id);
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
    public List<ProductCategory> populateCategories() {
        return productService.findAllProdCategory();
    }


    // Method to filter products by category
    @GetMapping("/products/category/{category_id}")
    public String filterProductsByCategory(@PathVariable Long category_id, Model model) {
        List<Product> products = productService.getProductsByCategory(category_id);
        ProductCategory proCategory = productService.getProdCategoryById(category_id);
        model.addAttribute("products", products);
        model.addAttribute("categoryName", proCategory.getName());
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

    @GetMapping("/products/uploadProduct")
    public String productUploadInput(Model model) {
        model.addAttribute("product", new Product());
        return "productUpload"; // Return the view name for adding a product
    }

    @RemoteMethod
    public Map<String, Object> saveUploadedProductData(String fileName, String base64Data) throws Exception {
        return productService.saveUploadedProductData(fileName,base64Data);
    }
    
    @GetMapping("/products/productCategoryInput")
    public ModelAndView getProductCategoryInput() {
    	ModelAndView mv = new ModelAndView("addCategory");
    	mv.addObject("prodCategory", new ProductCategory());
    	return mv;
    }
    
    @RemoteMethod
    public Map<String,Object> saveProductCategory(Long categoryId,String categoryName,String desc,boolean isForEdit) {
    	return productService.saveProdCategory(categoryId,categoryName,desc,isForEdit);
    	
    }
    
    @GetMapping("/products/brandInput")
    public ModelAndView getBrandInput() {
    	ModelAndView mv = new ModelAndView("addBrand");
    	mv.addObject("prodSubCategory", productService.findAllProdSubCategory());
    	mv.addObject("brand", new Brand());
    	return mv;
    }
    
    @RemoteMethod
    public Map<String,Object> saveProdBrand(String brandName,String desc,List<Long> subCategoryList,boolean isForEdit) {
    	return productService.saveProdBrand(brandName,desc,subCategoryList, isForEdit);
    }
    @RemoteMethod
    public Map<String,Object> getSubCategoryBasedOnBrand(Long brandId) {
    	return productService.getSubCategoryBasedOnBrand(brandId);
    }
    @RemoteMethod
    public Map<String,Object> getCategoryBasedOnSubCategory(Long subCategoryId) {
    	return productService.getCategoryBasedOnSubCategory(subCategoryId);
    }
    
    @GetMapping("/products/subCategoryInput")
    public ModelAndView getSubCategoryInput() {
    	ModelAndView mv = new ModelAndView("subCategory");
    	mv.addObject("prodCategory", productService.findAllProdCategory());
    	mv.addObject("prodSubCategory", new ProductSubCategory());
    	return mv;
    }
    
    @RemoteMethod
    public Map<String,Object> saveSubCategory(String subCategoryName,String desc,Long categoryId,boolean isForEdit) {
    	return productService.saveSubCategory(subCategoryName,desc,categoryId,isForEdit);
    }
    
    @GetMapping("/products/showAllProductCategories")
    public ModelAndView showAllProdCategory(@RequestParam(required =false) String redirectMsg) {
    	ModelAndView mv = new ModelAndView("allCategories");
    	mv.addObject("redirectMsg", redirectMsg);
    	mv.addObject("prodCategoryList", productService.findAllProdCategory());
    	return mv;
    }
    
    @GetMapping("/products/editProdCategoryInput/{categoryId}")
    public ModelAndView getEditProdCategoryInput(@PathVariable Long categoryId) {
    	ModelAndView mv = new ModelAndView("addCategory");
    	ProductCategory category = productService.getProdCategoryById(categoryId);
    	if (category != null) {
    		mv.addObject("prodCategory", category);
        } else {
        	mv.addObject("error", "Product Category not found.");
            return new ModelAndView("error"); // Return error page if no products are found
        }
    	return mv;
    }
    
    @RemoteMethod
    public Map<String,Object> deleteProdCategory(@PathVariable Long categoryId) {
    	return productService.deleteProdCategory(categoryId);
    }
    
    @GetMapping("/products/showAllProductSubCategories")
    public ModelAndView showAllSubCategory(@RequestParam(required =false) String redirectMsg) {
    	ModelAndView mv = new ModelAndView("allSubCategories");
    	mv.addObject("redirectMsg", redirectMsg);
    	mv.addObject("prodSubCategoryList", productService.findAllProdSubCategory());
    	return mv;
    }
    
    @GetMapping("/products/editProdSubCategoryInput/{subCategoryId}")
    public ModelAndView getEditProdSubCategoryInput(@PathVariable Long subCategoryId) {
    	ModelAndView mv = new ModelAndView("subCategory");
    	ProductSubCategory subCategory = productService.getProdSubCategoryById(subCategoryId);
    	if (subCategory != null) {
    		mv.addObject("prodCategory", productService.findAllProdCategory());
    		mv.addObject("prodSubCategory", subCategory);
        } else {
        	mv.addObject("error", "Product Sub Category not found.");
            return new ModelAndView("error"); // Return error page if no products are found
        }
    	return mv;
    }
    
    @RemoteMethod
    public Map<String,Object> deleteProdSubCategory(@PathVariable Long subCategoryId) {
    	return productService.deleteProdSubCategory(subCategoryId);
    }
    
    @GetMapping("/products/showAllBrands")
    public ModelAndView showAllBrands(@RequestParam(required =false) String redirectMsg) {
    	ModelAndView mv = new ModelAndView("allBrands");
    	mv.addObject("redirectMsg", redirectMsg);
    	mv.addObject("prodBrandList", productService.findAllBrands());
    	return mv;
    }
    
    @GetMapping("/products/editBrandInput/{brandId}")
    public ModelAndView getEditBrandInput(@PathVariable Long brandId) {
    	ModelAndView mv = new ModelAndView("addBrand");
    	Brand existbrand = productService.findBrandById(brandId);
    	if (existbrand != null) {
    		mv.addObject("prodSubCategory", productService.findAllProdSubCategory());
    		mv.addObject("brand", existbrand);
        } else {
        	mv.addObject("error", "Product Sub Category not found.");
            return new ModelAndView("error"); // Return error page if no products are found
        }
    	return mv;
    }
    
    @RemoteMethod
    public Map<String,Object> deleteBrand(@PathVariable Long brandId) {
    	return productService.deleteBrand(brandId);
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
