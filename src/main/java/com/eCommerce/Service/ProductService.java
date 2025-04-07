package com.eCommerce.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eCommerce.Entity.Product;
import com.eCommerce.Repository.ProductRepository;

@Service
public class ProductService {
	@Autowired
	private ProductRepository repo;

	public List<Product> getAllProduct(){
		return repo.findAll();
		
	}
	
	public Product saveProduct(Product product) {
		return repo.save(product);
		
		
	}
	
	
	public Product getProductById(Integer productId) {
	    return repo.findById(productId).orElse(null); // Return the product or null if not found
	}

	public Product updateProduct(Integer id , Product product) {
		return repo.save(product);
	}
	
	public void deleteProduct(Integer id) {
		repo.deleteById(id);
	}
	
	public Product getProductByCategoryAndId(String category, Integer id) {
		return repo.findByCategoryAndId(category, id);
	}
	
	 public List<Product> getSearchedProduct(String keyword) {
	        List<Product> products = repo.searchProducts(keyword);
	        System.out.println("Found products: " + products.size());
	        return products;
	    }
	
	  // Method to get products by category
 public List<Product> getProductsByCategory(String category) {
     return repo.findByCategory(category); // Replace with actual repository method
 }




	
}
