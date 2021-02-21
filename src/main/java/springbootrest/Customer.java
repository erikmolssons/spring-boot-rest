package springbootrest;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@Document("customer")
public class Customer {
    @Id
    private String id;
    @Pattern(regexp = "[a-zA-Z]+")
    private String firstName;
    @Pattern(regexp = "[a-zA-Z]+")
    private String lastName;
    @Email
    private String email;
    @URL
    private String avatar;

    public Customer(@Pattern(regexp = "[a-zA-Z]+") String firstName, @Pattern(regexp = "[a-zA-Z]+") String lastName, @Email String email, @URL String avatar) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatar = avatar;
    }
}
