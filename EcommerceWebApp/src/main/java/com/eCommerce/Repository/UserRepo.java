package com.eCommerce.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Entity.Users;
@Repository
public interface UserRepo extends JpaRepository<Users, Integer>{
	Users findByUsername(String username);
}

