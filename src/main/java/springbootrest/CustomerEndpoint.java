package springbootrest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CustomerEndpoint {

    private final CustomerRepository repository;

    @Autowired
    public CustomerEndpoint(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Customer>> buildResponseFindAll() {
        return ResponseEntity.ok(this.repository.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Customer> buildResponseFindOne(@PathVariable("id") String id) {
        var customer = this.repository.findById(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customer> buildResponseInsert(@Valid @RequestBody Customer customer) {
        var createdCustomer = this.repository.insert(customer);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCustomer.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdCustomer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> buildResponseUpdate(@PathVariable("id") String id, @Valid @RequestBody Customer customer) {
        var found = this.repository.findById(id);
        if (found.isPresent()) {
            customer.setId(found.get().getId());
            return ResponseEntity.ok(this.repository.save(customer));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> buildResponseDelete(@PathVariable String id) {
        var found = this.repository.findById(id);
        if (found.isPresent()) {
            this.repository.delete(found.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> buildResponseValidationFailed(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> buildResponseInternalServerError(Throwable e) {
        var msg = e.getMessage();
        return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
