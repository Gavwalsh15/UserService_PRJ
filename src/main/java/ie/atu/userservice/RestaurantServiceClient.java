package ie.atu.userservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@FeignClient(name = "restaurant-service", url = "http://localhost:8081")
public interface RestaurantServiceClient {
    @PostMapping("/api/restaurant/addNewOrder")
    ResponseEntity<Order> addNewOrder(Order order);
}
