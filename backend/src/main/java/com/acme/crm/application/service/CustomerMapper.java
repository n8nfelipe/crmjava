package com.acme.crm.application.service;

import com.acme.crm.application.dto.CustomerDtos;
import com.acme.crm.domain.model.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
  @Mapping(target = "ownerId", source = "owner.id")
  @Mapping(target = "ownerName", source = "owner.name")
  CustomerDtos.Response toResponse(Customer c);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "owner", ignore = true)
  Customer toEntity(CustomerDtos.Request r);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "owner", ignore = true)
  void update(CustomerDtos.Request r, @MappingTarget Customer c);
}
