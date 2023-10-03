package com.myproject.app.customer;


import com.myproject.app.amqp.RabbitMQMessageProducer;
import com.myproject.app.clients.fraud.FraudCheckResponse;
import com.myproject.app.clients.fraud.FraudClient;
import com.myproject.app.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        // todo: check if email valid
        //  check if email already exists
        //  check fraudster
        customerRepository.saveAndFlush(customer);
//        FraudCheckResponse fraudCheckResponse =  restTemplate.getForObject(
//                "http://FRAUD/api/v1/fraud-check/{customerId}",
//                FraudCheckResponse.class,
//                customer.getId()
//        );

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse==null && fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }
        // todo: send notification

        NotificationRequest notificationRequest = new NotificationRequest(
            customer.getId(),
            customer.getEmail(),
            String.format("Hi %s, welcome to my project", customer.getFirstName()));

        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing.key"
        );
    }
}
