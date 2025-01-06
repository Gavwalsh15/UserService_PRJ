package ie.atu.userservice;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @NotBlank(message = "Restaurant ID is required.")
    private String restaurantId;

    @NotEmpty(message = "Order must contain at least one menu item.")
    private List<OrderItem> foodSelected;

    @NotBlank(message = "Customer name is required.")
    private String username;

    private String customerAddress;

    private OrderStatus status = OrderStatus.PENDING;

    @Positive(message = "Price must be greater than zero.")
    private double totalPrice;

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        DELIVERED,
        CANCELLED
    }
}


