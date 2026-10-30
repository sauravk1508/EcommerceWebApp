package com.eCommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Entity.ProductSubCategory;
@Repository
public interface SubCategoryRepository extends JpaRepository<ProductSubCategory, Long>{
	ProductSubCategory findByName(String name);
}

