package com.acme.crm;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.crm.domain.model.Customer;
import com.acme.crm.domain.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class CrmApplicationTest {
  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired CustomerRepository customers;

  @Test
  void migratesSchemaAndPersistsCustomer() {
    var customer = new Customer();
    customer.setType(Customer.Type.COMPANY);
    customer.setName("Acme Test");
    customers.saveAndFlush(customer);
    assertThat(customers.findById(customer.getId())).isPresent();
  }
}
