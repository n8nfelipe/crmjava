package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.crm.application.dto.ProductDtos.Request;
import com.acme.crm.domain.model.Product;
import com.acme.crm.domain.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
  @Mock private ProductRepository repo;
  @Mock private AuditService audit;
  @InjectMocks private ProductService service;

  @Test
  void createsProductAndRecordsAudit() {
    var id = UUID.randomUUID();
    when(repo.save(any(Product.class)))
        .thenAnswer(
            invocation -> {
              var product = invocation.getArgument(0, Product.class);
              product.setId(id);
              return product;
            });

    var response =
        service.create(new Request("Plano Pro", "Assinatura", new BigDecimal("99.90"), true));

    assertThat(response.id()).isEqualTo(id);
    assertThat(response.price()).isEqualByComparingTo("99.90");
    assertThat(response.active()).isTrue();
    verify(audit).record("Product", id, "CREATE", "{}");
  }
}
