package com.intercop.inventario.product.service;
import com.intercop.inventario.notification.model.Notification;
import com.intercop.inventario.notification.service.NotificationService;
import com.intercop.inventario.product.model.Product;
import com.intercop.inventario.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    private final KafkaTemplate<String, Notification> kafkaTemplate;

    public ProductService(ProductRepository productRepository, KafkaTemplate<String, Notification> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    private NotificationService notificationService;


    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Mono<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Mono<Product> createProduct(Product product) {
        Notification notification = notificationService.createNotification(product.getId(), "New product" + product.getName() + " has been updated");
        return productRepository.save(product)
                .doOnSuccess(savedProduct -> kafkaTemplate.send("product-events", notification));
    }

    public Mono<Product> updateProduct(String id, Product product) {
        Notification notification = notificationService.createNotification(product.getId(), "Product" + product.getName() + " has been updated");
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setQuantity(product.getQuantity());
                    return productRepository.save(existingProduct);
                })
                .doOnSuccess(updatedProduct -> kafkaTemplate.send("product-events", notification));
    }

    public Mono<Void> deleteProduct(String id, Product product) {
        Notification notification = notificationService.createNotification(product.getId(), "Product" + product.getName() + " has been deleted");
        return productRepository.deleteById(id)
                .doOnSuccess(v -> kafkaTemplate.send("product-events", notification));
    }
}
