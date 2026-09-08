package com.acme.crm.application.service;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.UserDtos.*;
import com.acme.crm.domain.exception.*;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository users;
  private final RoleRepository roles;
  private final PasswordEncoder encoder;
  private final AuditService audit;

  @Transactional(readOnly = true)
  public PageResponse<View> list(Pageable p) {
    return PageResponse.from(users.findAll(p).map(this::view));
  }

  @Transactional
  public View create(Create r) {
    if (users.existsByEmailIgnoreCase(r.email()))
      throw new BusinessException("E-mail já cadastrado");
    var u = new User();
    u.setName(r.name());
    u.setEmail(r.email());
    u.setPasswordHash(encoder.encode(r.password()));
    u.setRoles(resolve(r.roles()));
    users.save(u);
    audit.record("User", u.getId(), "CREATE", "{}");
    return view(u);
  }

  @Transactional
  public View update(UUID id, Update r) {
    var u = users.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    u.setName(r.name());
    u.setRoles(resolve(r.roles()));
    u.setEnabled(r.enabled());
    audit.record("User", id, "UPDATE", "{}");
    return view(u);
  }

  private Set<Role> resolve(Set<RoleName> names) {
    return names.stream().map(n -> roles.findByName(n).orElseThrow()).collect(Collectors.toSet());
  }

  private View view(User u) {
    return new View(
        u.getId(),
        u.getName(),
        u.getEmail(),
        u.isEnabled(),
        u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
  }
}
