package org.sixpang.userservice;

import org.sixpang.commonserver.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@Import(JpaConfig.class)
@SpringBootApplication(scanBasePackages = "org.sixpang")
@EntityScan(basePackages = "org.sixpang")
@EnableJpaRepositories(basePackages = "org.sixpang")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}