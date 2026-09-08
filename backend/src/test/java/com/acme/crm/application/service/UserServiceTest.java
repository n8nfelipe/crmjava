package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.crm.application.dto.UserDtos.Create;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserRepository users;
  @Mock private RoleRepository roles;
  @Mock private PasswordEncoder encoder;
  @Mock private AuditService audit;
  @InjectMocks private UserService service;

  @Test
  void createsUserWithEncodedPasswordAndResolvedRole() {
    var id = UUID.randomUUID();
    var role = new Role();
    role.setName(RoleName.SALES);
    when(users.existsByEmailIgnoreCase("sales@example.com")).thenReturn(false);
    when(roles.findByName(RoleName.SALES)).thenReturn(Optional.of(role));
    when(encoder.encode("strong-password")).thenReturn("encoded-password");
    when(users.save(any(User.class)))
        .thenAnswer(
            invocation -> {
              var user = invocation.getArgument(0, User.class);
              user.setId(id);
              return user;
            });

    var response =
        service.create(
            new Create(
                "Sales User", "sales@example.com", "strong-password", Set.of(RoleName.SALES)));

    assertThat(response.id()).isEqualTo(id);
    assertThat(response.roles()).containsExactly(RoleName.SALES);
    var captor = ArgumentCaptor.forClass(User.class);
    verify(users).save(captor.capture());
    assertThat(captor.getValue().getPasswordHash()).isEqualTo("encoded-password");
    verify(audit).record("User", id, "CREATE", "{}");
  }
}
