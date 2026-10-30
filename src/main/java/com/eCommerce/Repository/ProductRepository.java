package com.eCommerce.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eCommerce.Entity.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	@Query("SELECT p FROM Product p " +
		       "JOIN p.subCategory psc " +
		       "JOIN psc.productCategory pc " +
		       "WHERE pc.id = :category_id AND p.id = :id")
		Product findByCategoryAndId(@Param("category_id") Long categoryId, @Param("id") Long id);

	
	@Query("SELECT p FROM Product p " +
		       "JOIN p.brand b " +
		       "JOIN p.subCategory psc " +
		       "JOIN psc.productCategory pc " +
		       "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		             "LOWER(pc.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		             "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		             "LOWER(psc.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		             "LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
		List<Product> searchProducts(@Param("keyword") String keyword);

	
	@Query("SELECT p FROM Product p " +
		       "JOIN p.subCategory psc " +
		       "JOIN psc.productCategory pc " +
		       "WHERE pc.id = :category_id")
		List<Product> findByCategory(@Param("category_id") Long categoryId);
	
}
