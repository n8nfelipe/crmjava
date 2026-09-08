package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.crm.application.dto.ContactDtos.Request;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {
  @Mock private ContactRepository repo;
  @Mock private CustomerRepository customers;
  @Mock private AuditService audit;
  @InjectMocks private ContactService service;

  @Test
  void createsContactForExistingCustomer() {
    var customerId = UUID.randomUUID();
    var contactId = UUID.randomUUID();
    var customer = new Customer();
    customer.setId(customerId);
    when(customers.findById(customerId)).thenReturn(Optional.of(customer));
    when(repo.save(any(Contact.class)))
        .thenAnswer(
            invocation -> {
              var contact = invocation.getArgument(0, Contact.class);
              contact.setId(contactId);
              return contact;
            });

    var response =
        service.create(
            customerId, new Request("Maria", "Compras", "11999999999", "maria@example.com", true));

    assertThat(response.id()).isEqualTo(contactId);
    assertThat(response.customerId()).isEqualTo(customerId);
    assertThat(response.primaryContact()).isTrue();
    verify(audit).record("Contact", contactId, "CREATE", "{}");
  }
}
