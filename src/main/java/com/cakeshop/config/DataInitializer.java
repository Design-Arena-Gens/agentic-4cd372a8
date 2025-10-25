package com.cakeshop.config;

import com.cakeshop.model.Product;
import com.cakeshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            createProduct("Chocolate Cake", "Rich and moist chocolate cake with chocolate frosting", 29.99, "chocolate", "https://images.unsplash.com/photo-1578985545062-69928b1d9587", 50);
            createProduct("Vanilla Cake", "Classic vanilla cake with buttercream frosting", 24.99, "vanilla", "https://images.unsplash.com/photo-1588195538326-c5b1e5b8f8c5", 50);
            createProduct("Red Velvet Cake", "Smooth red velvet cake with cream cheese frosting", 32.99, "red-velvet", "https://images.unsplash.com/photo-1616690710400-a16d146927c5", 40);
            createProduct("Strawberry Cake", "Fresh strawberry cake with whipped cream", 27.99, "fruit", "https://images.unsplash.com/photo-1565958011703-44f9829ba187", 45);
            createProduct("Lemon Cake", "Zesty lemon cake with lemon glaze", 26.99, "fruit", "https://images.unsplash.com/photo-1519915212116-7cfef71f1d3e", 35);
            createProduct("Carrot Cake", "Spiced carrot cake with cream cheese frosting", 28.99, "specialty", "https://images.unsplash.com/photo-1621303837174-89787a7d4729", 30);
            createProduct("Cheesecake", "Creamy New York style cheesecake", 34.99, "specialty", "https://images.unsplash.com/photo-1533134486753-c833f0ed4866", 25);
            createProduct("Black Forest Cake", "Chocolate cake with cherries and whipped cream", 35.99, "chocolate", "https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62", 20);
            createProduct("Tiramisu Cake", "Italian coffee-flavored dessert cake", 33.99, "specialty", "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9", 30);
            createProduct("Rainbow Cake", "Colorful layered cake with vanilla frosting", 31.99, "specialty", "https://images.unsplash.com/photo-1535141192574-5d4897c12636", 40);
        }
    }

    private void createProduct(String name, String description, double price, String category, String image, int stock) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setImage(image);
        product.setStock(stock);
        productRepository.save(product);
    }
}
