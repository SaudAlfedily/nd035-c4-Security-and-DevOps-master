package com.example.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Cart;
import com.example.demo.model.User;
import com.example.demo.repositories.CartRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.requests.CreateUserRequest;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/user")
@Transactional
@RequiredArgsConstructor
public class UserController {
	
	private final UserRepository userRepository;
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);


	private final CartRepository cartRepository;


	private final  BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User newuser = new User();
		newuser.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		newuser.setCart(cart);
		//for security password is null or 8 or smaller
		if(createUserRequest.getPassword() == null || createUserRequest.getPassword().length() <= 8){
			logger.error("can not register user{}",createUserRequest.getUsername());
		}
		//encoding and set encoded password
		String encodedPassword =bCryptPasswordEncoder.encode(createUserRequest.getPassword());
		newuser.setPassword(encodedPassword);
		userRepository.save(newuser);
		logger.info("User registered");
		return ResponseEntity.ok(newuser);
	}
	
}
