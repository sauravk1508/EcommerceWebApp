package com.eCommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>{
	
	Brand findByName(String name);
}
