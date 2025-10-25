package com.cakeshop.controller;

import com.cakeshop.dto.CartItemRequest;
import com.cakeshop.model.CartItem;
import com.cakeshop.model.Product;
import com.cakeshop.model.User;
import com.cakeshop.repository.CartItemRepository;
import com.cakeshop.repository.ProductRepository;
import com.cakeshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return ResponseEntity.ok(cartItemRepository.findByUser(user));
    }

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody CartItemRequest request, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        Product product = productRepository.findById(request.getProductId())
                .orElse(null);

        if (product == null) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        CartItem cartItem = cartItemRepository.findByUserAndProductId(user, request.getProductId())
                .orElse(new CartItem());

        if (cartItem.getId() == null) {
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        }

        cartItemRepository.save(cartItem);
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long id,
                                           @RequestBody CartItemRequest request,
                                           Authentication authentication) {
        User user = getUserFromAuth(authentication);
        CartItem cartItem = cartItemRepository.findById(id)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (cartItem == null) {
            return ResponseEntity.notFound().build();
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long id, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        CartItem cartItem = cartItemRepository.findById(id)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (cartItem == null) {
            return ResponseEntity.notFound().build();
        }

        cartItemRepository.delete(cartItem);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        cartItemRepository.deleteByUser(user);
        return ResponseEntity.ok().build();
    }

    private User getUserFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
