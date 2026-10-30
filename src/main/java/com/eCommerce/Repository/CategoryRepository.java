package com.eCommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Entity.ProductCategory;

@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory, Long>{

	ProductCategory findByName(String name);
}
