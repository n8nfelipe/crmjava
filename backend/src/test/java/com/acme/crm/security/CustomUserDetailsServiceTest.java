package com.acme.crm.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
  @Mock private UserRepository users;
  @InjectMocks private CustomUserDetailsService service;

  @Test
  void loadsUserAndMapsRolesAndPermissionsToAuthorities() {
    var permission = new Permission();
    permission.setName("customer:read");
    var role = new Role();
    role.setName(RoleName.SALES);
    role.setPermissions(Set.of(permission));
    var user = new User();
    user.setId(UUID.randomUUID());
    user.setName("Sales User");
    user.setEmail("sales@example.com");
    user.setPasswordHash("encoded");
    user.setRoles(Set.of(role));
    when(users.findByEmailIgnoreCase("sales@example.com")).thenReturn(Optional.of(user));

    var principal = service.loadUserByUsername("sales@example.com");

    assertThat(principal.getUsername()).isEqualTo("sales@example.com");
    assertThat(principal.getAuthorities())
        .extracting(authority -> authority.getAuthority())
        .containsExactlyInAnyOrder("ROLE_SALES", "customer:read");
  }
}
