package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Cart;
import com.example.demo.model.Item;
import com.example.demo.model.User;
import com.example.demo.repositories.CartRepository;
import com.example.demo.repositories.ItemRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
	
	private final UserRepository userRepository;
	
	private final CartRepository cartRepository;
	

	private final ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());

		if(user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();

		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());

		if(user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		}

		Optional<Item> item = itemRepository.findById(request.getItemId());

		if(!item.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));

		cartRepository.save(cart);
		return ResponseEntity.ok(cart);
	}
		
}
