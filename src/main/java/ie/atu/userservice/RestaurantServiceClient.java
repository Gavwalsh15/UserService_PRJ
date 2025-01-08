package ie.atu.userservice;

import org.aspectj.weaver.ast.Or;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Component
@FeignClient(name = "restaurant-service", url = "http://localhost:8081/api/restaurant/")
public interface RestaurantServiceClient {
    @PostMapping("addNewOrder")
    ResponseEntity<Order> addNewOrder(Order order);

    @GetMapping("get-orders-customer/{username}")
    ResponseEntity<List<Order>> getMyOrders(@PathVariable String username);
}
