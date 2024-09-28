package com.intercop.inventario.notification.service;
import com.intercop.inventario.notification.model.Notification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final List<Notification> notifications = new ArrayList<>();

    @KafkaListener(topics = "product-events", groupId = "notification-group")
    public void listenProductEvents(String message) {
        Notification notification = new Notification(
                UUID.randomUUID().toString(),
                message,
                LocalDateTime.now().toString()
        );
        notifications.add(notification);
    }

    public Notification createNotification(String productId, String action) {
        String message = "Product " + action + ": " + productId;
        Notification notification = new Notification(
                UUID.randomUUID().toString(),
                productId,
                message,
                LocalDateTime.now().toString()
        );
        notifications.add(notification);
        return notification;
    }

    public List<Notification> getAllNotifications() {
        return notifications;
    }

    public Notification getNotificationById(String id) {
        return notifications.stream()
                .filter(notification -> notification.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}