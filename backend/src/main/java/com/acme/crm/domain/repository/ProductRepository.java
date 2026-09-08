package com.acme.crm.domain.repository;

import com.acme.crm.domain.model.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;

public interface ProductRepository
    extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {}
