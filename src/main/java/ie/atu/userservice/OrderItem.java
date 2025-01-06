package ie.atu.userservice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItem {

    @NotBlank(message = "Item name is required.")
    private String name;

    @Positive(message = "Price must be greater than zero.")
    private double price;
}
