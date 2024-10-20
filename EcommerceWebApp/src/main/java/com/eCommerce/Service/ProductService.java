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
	
	
	public Optional<Product> getProductById(Integer id ){
		return repo.findById(id);
	}
	
	public Product updateProduct(Integer id , Product product) {
		return repo.save(product);
	}
	
	public void deleteProduct(Integer id) {
		repo.deleteById(id);
	}
}
