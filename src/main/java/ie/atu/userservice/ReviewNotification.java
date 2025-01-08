package ie.atu.userservice;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ReviewNotification {
    @RabbitListener(queues = "review_notification_queue")
    public void handleNewReviewNotification(String message) {
        System.out.println("Received notification: " + message);
    }
}
