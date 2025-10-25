package com.cakeshop.controller;

import com.cakeshop.dto.PaymentRequest;
import com.cakeshop.model.*;
import com.cakeshop.repository.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequest request,
                                                 Authentication authentication) {
        try {
            User user = getUserFromAuth(authentication);
            List<CartItem> cartItems = cartItemRepository.findByUser(user);

            if (cartItems.isEmpty()) {
                return ResponseEntity.badRequest().body("Cart is empty");
            }

            double total = cartItems.stream()
                    .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                    .sum();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long)(total * 100))
                    .setCurrency("usd")
                    .putMetadata("userId", user.getId().toString())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", intent.getClientSecret());
            response.put("amount", total);

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Payment error: " + e.getMessage());
        }
    }

    @PostMapping("/confirm")
    @Transactional
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> request,
                                           Authentication authentication) {
        try {
            User user = getUserFromAuth(authentication);
            String paymentIntentId = request.get("paymentIntentId");

            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            if ("succeeded".equals(intent.getStatus())) {
                List<CartItem> cartItems = cartItemRepository.findByUser(user);

                Order order = new Order();
                order.setUser(user);
                order.setStatus("PAID");
                order.setPaymentIntentId(paymentIntentId);

                double total = 0;
                for (CartItem cartItem : cartItems) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getProduct().getPrice());
                    order.getItems().add(orderItem);
                    total += orderItem.getPrice() * orderItem.getQuantity();
                }

                order.setTotalAmount(total);
                orderRepository.save(order);

                cartItemRepository.deleteByUser(user);

                return ResponseEntity.ok(order);
            }

            return ResponseEntity.badRequest().body("Payment not confirmed");
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Payment error: " + e.getMessage());
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return ResponseEntity.ok(orderRepository.findByUserOrderByCreatedAtDesc(user));
    }

    private User getUserFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
