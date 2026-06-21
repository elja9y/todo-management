package com.github.elja9y.todo.security;

import com.github.elja9y.todo.entity.User;
import com.github.elja9y.todo.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /*
     UserDetailsService is Spring Security's interface for "how do I find a user".
     It has one method: loadUserByUsername(String username) —
     Spring Security calls it internally during authentication.
     You don't call it yourself. When a login request comes in, Spring Security does:
     - Takes the username from the request
     - Calls your loadUserByUsername to fetch the user from DB
     - Compares the returned UserDetails password with the request password
     - Grants or denies access
     So you override it to tell Spring Security where your users live — in your DB,
     via your UserRepository. Without it Spring has no idea how to find users.*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList()
        );
    }
}
