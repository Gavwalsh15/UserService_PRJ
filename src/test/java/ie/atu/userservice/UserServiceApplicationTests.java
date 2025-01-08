package ie.atu.userservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@ExtendWith(MockitoExtension.class)
public class UserServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private RestaurantServiceClient restaurantServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer validCustomer;
    private LoginRequest validLoginRequest;
    private Order validOrder;

    @BeforeEach
    void setUp() {
        validCustomer = new Customer(1L, "John", "Doe", "john.doe@email.com", "1234 Elm St.", "john_doe", "Password1");
        validLoginRequest = new LoginRequest("john_doe", "john.doe@email.com", "Password1");

        OrderItem orderItem = new OrderItem("Pizza", 12.50);
        validOrder = new Order("1", List.of(orderItem), "john_doe", "1234 Elm St.", Order.OrderStatus.PENDING, 12.50);
    }

    @Test
    void testCreateCustomer_Valid() throws Exception {
        when(customerRepository.save(validCustomer)).thenReturn(validCustomer);

        mockMvc.perform(post("/api/customer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void testLoginCustomer_Valid() throws Exception {
        when(customerRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(validCustomer));

        mockMvc.perform(post("/api/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void testLoginCustomer_InvalidEmail() throws Exception {
        LoginRequest invalidLoginRequest = new LoginRequest("wrong_user", "wrong@email.com", "Password1");

        mockMvc.perform(post("/api/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username or email is not found"));
    }

    @Test
    void testLoginCustomer_IncorrectPassword() throws Exception {
        LoginRequest invalidLoginRequest = new LoginRequest("john_doe", "john.doe@email.com", "WrongPassword");

        Customer mockedCustomer = new Customer(1L, "John", "Doe", "john.doe@email.com", "1234 Elm St.", "john_doe", "Password1");
        when(customerRepository.findByEmail("john.doe@email.com")).thenReturn(Optional.of(mockedCustomer));

        mockMvc.perform(post("/api/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Incorrect password"));
    }

    @Test
    void testAddOrder_Invalid() throws Exception {
        Order invalidOrder = new Order("1", List.of(), "john_doe", "1234 Elm St.", Order.OrderStatus.PENDING, 0);

        mockMvc.perform(post("/api/customer/add-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrder)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.foodSelected").value("Order must contain at least one menu item."))
                .andExpect(jsonPath("$.errors.totalPrice").value("Price must be greater than zero."));
    }

    @Test
    void testGetOrders() throws Exception {
        when(restaurantServiceClient.getMyOrders("john_doe")).thenReturn(ResponseEntity.ok(List.of(validOrder)));

        mockMvc.perform(get("/api/customer/get-orders/john_doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].restaurantId").value("1"))
                .andExpect(jsonPath("$[0].username").value("john_doe"));
    }
}
