package com.acme.crm.security;

import com.acme.crm.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository users;

  public UserDetails loadUserByUsername(String email) {
    return users
        .findByEmailIgnoreCase(email)
        .map(UserPrincipal::from)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
  }
}
