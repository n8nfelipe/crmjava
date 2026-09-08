package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.crm.application.dto.CustomerDtos.*;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
  @Mock private CustomerRepository customers;
  @Mock private UserRepository users;
  @Mock private CustomerMapper mapper;
  @Mock private AuditService audit;
  @InjectMocks private CustomerService service;

  @Test
  void createsCustomerWithOwnerAndRecordsAudit() {
    var customerId = UUID.randomUUID();
    var ownerId = UUID.randomUUID();
    var request = new Request(Customer.Type.COMPANY, "Acme", null, null, null, null, null, ownerId);
    var owner = new User();
    owner.setId(ownerId);
    var customer = new Customer();
    customer.setId(customerId);
    customer.setName("Acme");
    var expected =
        new Response(
            customerId,
            Customer.Type.COMPANY,
            "Acme",
            null,
            null,
            null,
            null,
            null,
            ownerId,
            null,
            null,
            null,
            0);
    when(mapper.toEntity(request)).thenReturn(customer);
    when(users.findById(ownerId)).thenReturn(Optional.of(owner));
    when(mapper.toResponse(customer)).thenReturn(expected);

    var response = service.create(request);

    assertThat(response).isSameAs(expected);
    assertThat(customer.getOwner()).isSameAs(owner);
    verify(customers).save(customer);
    verify(audit).record("Customer", customerId, "CREATE", "{\"name\":\"Acme\"}");
  }
}
