package com.eCommerce.Service;

import com.eCommerce.Entity.Users;

public interface GenericService {

	  Users register(Users user);
	  boolean usernameExists(String username);
	
}
