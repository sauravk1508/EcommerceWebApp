package com.eCommerce.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eCommerce.Entity.Users;
@Repository
public interface UserRepo extends JpaRepository<Users, Long>{
	 // Case-sensitive username query
    @Query("SELECT u FROM Users u WHERE u.username = :username")
    Users findByUsername(@Param("username") String username);
    
    // Check if username exists (case-sensitive)
    @Query("SELECT COUNT(u) > 0 FROM Users u WHERE u.username = :username")
    boolean existsByUsernameExact(@Param("username") String username);
}
