package demo.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PartitioningProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartitioningProducerApplication.class, args);
    }
}