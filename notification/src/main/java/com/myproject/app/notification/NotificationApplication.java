package com.myproject.app.notification;

import com.myproject.app.amqp.RabbitMQMessageProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication(
        scanBasePackages = {
                "com.myproject.app.notification",
                "com.myproject.app.amqp",
        }
)
public class NotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(RabbitMQMessageProducer producer,
                                        NotificationConfig notificationConfig){
        return args -> producer.publish(new Person("Ali",30),
                notificationConfig.getInternalExchange(),
                notificationConfig.getInternalNotificationRoutingKey());
    }
    record Person(String name, int age) {
    }
}
