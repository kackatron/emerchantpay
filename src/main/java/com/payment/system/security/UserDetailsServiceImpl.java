package com.payment.system.security;

import com.payment.system.dao.models.User;
import com.payment.system.dao.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service that provides UserDetails for the needs of authentication.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepository
                .findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("There is no user with name: %s", name)));
        return UserDetailsImpl.getUserDetailsImpl(user);
    }
}
