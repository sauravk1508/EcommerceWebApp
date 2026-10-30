package com.eCommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Entity.ImageDetails;

@Repository
public interface ImageRepository extends JpaRepository<ImageDetails, Long>{

}
