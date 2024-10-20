package com.eCommerce.Configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private UserDetailsService userDetails;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/register", "/login", "/perform_login").permitAll() // Allow access to the registration endpoint
						.anyRequest().authenticated())
				.formLogin(form -> form
	                    .loginPage("/login") // Specify the custom login page
	                    .loginProcessingUrl("/perform_login") // Specify the login processing URL
	                    .defaultSuccessUrl("/products", true) // Redirect to /home on success
	                    .failureUrl("/login?error=true") // Redirect to login page on failure
	                    .permitAll()) // Allow all to access the login page
				.logout(logout -> logout
		                .permitAll()
		                .invalidateHttpSession(true)
		                .deleteCookies("JSESSIONID"))  // Ensure session and cookies are cleaned up
				.httpBasic(Customizer.withDefaults())
				.sessionManagement(session -> 
					session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				 .exceptionHandling(exceptionHandling -> exceptionHandling
		                    .accessDeniedPage("/error")) // Redirect to /error when access is denied
				.build();
		

//		http.formLogin(Customizer.withDefaults());

		/*
		 * Customizer<CsrfConfigurer<HttpSecurity>> custCsrf = new
		 * Customizer<CsrfConfigurer<HttpSecurity>>() {
		 * 
		 * @Override public void customize(CsrfConfigurer<HttpSecurity> customizer) { //
		 * TODO Auto-generated method stub customizer.disable(); } };
		 */

	}

	// we can't use this because it takes the hard coded values
//    @Bean
//    public UserDetailsService userDetailsService() {
//    	UserDetails user1 = User
//    			.withDefaultPasswordEncoder()
//    			.username("rahul")
//    			.password("12345")
//    			.roles("ADMIN")
//    			.build();   //build returns the object of userdetails
//    	
//    	UserDetails user2 = User
//    			.withDefaultPasswordEncoder()
//    			.username("Anuj")
//    			.password("12345")
//    			.roles("USER")
//    			.build();
//    	
//    	
//    	return new InMemoryUserDetailsManager(user1,user2);
//    }

	/*
	 * for working with database when we are login we give the username and password
	 * that username password goes to authentication provider in the form of
	 * authentication object so before going to authentication provider that
	 * username and password are un-authorised, after going to authentication
	 * provider that user password will become authorised spring usses that
	 * authentication provider is by default for changing that authentication
	 * provider we are doing the below code , we want to use own authentication
	 * provider that's why we are using this.
	 */

//    we are not using object of AuthenticationProvider becos it is interface so that
//		we are using DaoAuthenticationProvider it is class which entends
//		AbstractUserDetailsAuthenticationProvider and AbstractUserDetailsAuthenticationProvider
//		implements AuthenticationProvider , so indirectly we are using AuthenticationProvider
	
	
	@Bean
    public AuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    	provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
//    	provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
    	provider.setUserDetailsService(userDetails);
    	return provider;
    }
	

	
		@Bean
	    public ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine templateEngine) {
	        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
	        resolver.setTemplateEngine(templateEngine);
	        resolver.setOrder(1); // Set the order for resolving views
	        resolver.setCharacterEncoding("UTF-8");
	        resolver.setContentType("text/html");
	        return resolver;
	    }

}
