package springbootrest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerEndpointTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate http;

    private static String testId;

    private final Customer testObject = new Customer("jesus", "christ", "jesus.christ@heaven.com", "http://localhost:8080/");
    private String url = "http://localhost:";

    @BeforeEach
    void before() {
        this.url = url.concat(String.valueOf(port));
    }

    @Test
    @Order(1000)
    void shouldInsertNewCustomer() {
        var customer = http.postForEntity(url, testObject, Customer.class);
        assertEquals(HttpStatus.CREATED, customer.getStatusCode());
        testId = customer.getBody().getId();
    }

    @Test
    @Order(2000)
    void shouldFindOneCustomer() {
        this.url = url.concat("/").concat(String.valueOf(testId));
        var customer = http.getForEntity(url, Customer.class);
        assertEquals(HttpStatus.OK, customer.getStatusCode());
        assertEquals(testId, customer.getBody().getId());
    }

    @Test
    @Order(3000)
    void shouldUpdateOneCustomer() {
        this.url = url.concat("/").concat(String.valueOf(testId));
        testObject.setFirstName("lucifer");
        testObject.setLastName("beelzebub");
        testObject.setEmail("lucifer.beelzebub@hell.com");
        http.put(url, testObject);

        var customer = http.getForEntity(url, Customer.class);
        assertEquals(HttpStatus.OK, customer.getStatusCode());
        assertEquals(testId, customer.getBody().getId());
        assertEquals("lucifer", customer.getBody().getFirstName());
        assertEquals("beelzebub", customer.getBody().getLastName());
        assertEquals("lucifer.beelzebub@hell.com", customer.getBody().getEmail());
    }

    @Test
    @Order(4000)
    void shouldDeleteOneCustomer() {
        this.url = url.concat("/").concat(String.valueOf(testId));
        http.delete(url);
        var customer = http.getForEntity(url, Customer.class);
        assertEquals(HttpStatus.NOT_FOUND, customer.getStatusCode());
    }

    @Test
    @Order(5000)
    void shouldReturnListOfCustomers() {
        var list = http.getForEntity(url, List.class);
        assertEquals(HttpStatus.OK, list.getStatusCode());
        assertEquals(1000, list.getBody().size());
    }

    @Test
    @Order(6000)
    void shouldFailValidation() {
        testObject.setFirstName("444");
        testObject.setLastName("åäö");
        testObject.setEmail("hello@");
        testObject.setAvatar("not an URL");
        var s = http.postForEntity(url, testObject, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, s.getStatusCode());
    }
}
