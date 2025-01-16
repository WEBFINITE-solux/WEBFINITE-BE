package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.UserRole;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginUserId) throws UsernameNotFoundException {
        Optional<User> _user = this.userRepository.findByLoginUserId(loginUserId);
        if (_user.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        User user = _user.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(loginUserId)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getLoginUserId(), user.getPassword(), authorities);
    }
}
