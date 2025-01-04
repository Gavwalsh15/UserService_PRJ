package ie.atu.userservice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        Optional<Customer> customer = customerRepository.findById(Long.valueOf(id));
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> loginCustomer(@RequestBody LoginRequest loginRequest) {
        Optional<Customer> customer;
        HashMap<String, Object> response = new HashMap<>();

        if(loginRequest.getEmail() != null && loginRequest.getPassword() != null) {
            customer = customerRepository.findByEmail(loginRequest.getEmail());
        }else if(loginRequest.getUsername() != null && loginRequest.getPassword() != null) {
            customer = customerRepository.findByUsername(loginRequest.getUsername());
        }else{
            response.put("message", "Username or email is not found");
            return ResponseEntity.badRequest().body(response);
        }

        Customer foundCustomer = customer.get();

        if(loginRequest.getPassword().equals(foundCustomer.getPassword())){
            response.put("username", foundCustomer.getUsername());
            return ResponseEntity.ok(response);
        }else {
            response.put("message", "Incorrect password");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        if (customerRepository.existsById(Long.valueOf(id))) {
            customerRepository.deleteById(Long.valueOf(id));
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer) {}
}
