package com.acme.crm.security;

import com.acme.crm.domain.model.User;
import java.util.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record UserPrincipal(
    UUID id,
    String name,
    String username,
    String password,
    boolean enabled,
    Collection<? extends GrantedAuthority> authorities)
    implements UserDetails {
  public static UserPrincipal from(User u) {
    var auth = new HashSet<GrantedAuthority>();
    u.getRoles()
        .forEach(
            r -> {
              auth.add(new SimpleGrantedAuthority("ROLE_" + r.getName()));
              r.getPermissions().forEach(p -> auth.add(new SimpleGrantedAuthority(p.getName())));
            });
    return new UserPrincipal(
        u.getId(), u.getName(), u.getEmail(), u.getPasswordHash(), u.isEnabled(), auth);
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
}
