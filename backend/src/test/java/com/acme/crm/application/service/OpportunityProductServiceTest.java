package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.crm.application.dto.OpportunityProductDtos.Request;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpportunityProductServiceTest {
  @Mock private OpportunityProductRepository lines;
  @Mock private OpportunityRepository opportunities;
  @Mock private ProductRepository products;
  @Mock private AuditService audit;
  @InjectMocks private OpportunityProductService service;

  @Test
  void addsProductAndCalculatesLineTotal() {
    var opportunityId = UUID.randomUUID();
    var productId = UUID.randomUUID();
    var lineId = UUID.randomUUID();
    var opportunity = new Opportunity();
    opportunity.setId(opportunityId);
    var product = new Product();
    product.setId(productId);
    product.setName("Licença");
    when(opportunities.findById(opportunityId)).thenReturn(Optional.of(opportunity));
    when(products.findById(productId)).thenReturn(Optional.of(product));
    when(lines.findByOpportunityIdAndProductId(opportunityId, productId))
        .thenReturn(Optional.empty());
    when(lines.save(any(OpportunityProduct.class)))
        .thenAnswer(
            invocation -> {
              var line = invocation.getArgument(0, OpportunityProduct.class);
              line.setId(lineId);
              return line;
            });

    var response =
        service.upsert(opportunityId, new Request(productId, 3, new BigDecimal("25.50")));

    assertThat(response.id()).isEqualTo(lineId);
    assertThat(response.total()).isEqualByComparingTo("76.50");
    verify(audit)
        .record(
            "Opportunity",
            opportunityId,
            "PRODUCT_UPSERT",
            "{\"productId\":\"" + productId + "\"}");
  }
}
