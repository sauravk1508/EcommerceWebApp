package com.eCommerce.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.eCommerce.Entity.UserPrincipal;
import com.eCommerce.Entity.Users;
import com.eCommerce.Repository.UserRepo;

@Service
public class UserService implements UserDetailsService{
	@Autowired
	private UserRepo repo;
	
	private BCryptPasswordEncoder encoder=  new BCryptPasswordEncoder(12) ;
	
	public Users register(Users user) {
		user.setPassword(encoder.encode(user.getPassword()));
		return repo.save(user);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		Users user1 = repo.findByUsername(username);
		
		if(user1 ==null) {
			System.out.println("user not found");
			throw new UsernameNotFoundException("user not found");
		}
		return new UserPrincipal(user1);
	}
	
}
