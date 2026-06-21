package com.github.elja9y.todo.service.impl;

import com.github.elja9y.todo.dto.auth.LoginRequest;
import com.github.elja9y.todo.dto.auth.RegisterRequest;
import com.github.elja9y.todo.entity.Role;
import com.github.elja9y.todo.entity.User;
import com.github.elja9y.todo.exception.RoleException;
import com.github.elja9y.todo.exception.UserException;
import com.github.elja9y.todo.mapper.UserStructMapper;
import com.github.elja9y.todo.repository.RoleRepository;
import com.github.elja9y.todo.repository.UserRepository;
import com.github.elja9y.todo.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    private UserStructMapper userMapper;

    private AuthenticationManager authenticationManager;

//    public AuthServiceImpl(
//                UserRepository userRepository,
//                RoleRepository roleRepository,
//                PasswordEncoder passwordEncoder,
//                UserStructMapper userMapper,
//                AuthenticationManager authenticationManager) {
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.userMapper = userMapper;
//        this.authenticationManager = authenticationManager;
//    }

    @Override
    public String register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.username()))
            throw UserException.duplicatedUsername();

        if(userRepository.existsByEmail(request.email()))
            throw UserException.duplicatedEmail();

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName("ROLE_USER");
        if(userRole == null)
            throw RoleException.roleNotFound();

        roles.add(userRole);

        user.setRoles(roles);

        userRepository.save(user);

        return "User registered successfully";
    }

    @Override
    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.usernameOrEmail(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "Logged in successfully";
    }
}
