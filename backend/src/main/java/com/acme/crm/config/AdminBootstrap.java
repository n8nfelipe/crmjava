package com.acme.crm.config;

import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrap implements ApplicationRunner {
  private final UserRepository users;
  private final RoleRepository roles;
  private final PasswordEncoder encoder;
  private final String name, email, password;

  public AdminBootstrap(
      UserRepository users,
      RoleRepository roles,
      PasswordEncoder encoder,
      @Value("${APP_ADMIN_NAME:}") String name,
      @Value("${APP_ADMIN_EMAIL:}") String email,
      @Value("${APP_ADMIN_PASSWORD:}") String password) {
    this.users = users;
    this.roles = roles;
    this.encoder = encoder;
    this.name = name;
    this.email = email;
    this.password = password;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (email.isBlank() || password.isBlank() || users.existsByEmailIgnoreCase(email)) return;
    if (password.length() < 12)
      throw new IllegalStateException("APP_ADMIN_PASSWORD deve ter ao menos 12 caracteres");
    var u = new User();
    u.setName(name.isBlank() ? "CRM Administrator" : name);
    u.setEmail(email);
    u.setPasswordHash(encoder.encode(password));
    u.setRoles(Set.of(roles.findByName(RoleName.ADMIN).orElseThrow()));
    users.save(u);
  }
}
