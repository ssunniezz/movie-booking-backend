package io.muzoo.ssc.backend.auth;

import io.muzoo.ssc.backend.Auditorium;
import io.muzoo.ssc.backend.Movie;
import io.muzoo.ssc.backend.User;
import io.muzoo.ssc.backend.repository.UserRepository;
import io.muzoo.ssc.backend.util.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Optional;

@RestController
public class AuthenticationController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/api/getUser")
    public Iterator<User> getUser() {
        return userRepository.findAll().iterator();
    }

    @PostMapping("/api/getUserByUsername")
    public User getUserByUsername(HttpServletRequest request) {
        User user = userRepository.findByUsername(request.getParameter("username"));

        if (user == null) {
            return null;
        }
        return user;
    }

    @PostMapping("/api/deleteUser")
    public ResponseDTO deleteUserByUsername(HttpServletRequest request) {
        try {
            String username = request.getParameter("username");
            User deleteUser = userRepository.findByUsername(username);
            if (deleteUser == null) {
                return ResponseDTO.builder()
                        .success(false)
                        .error("Invalid username")
                        .build();
            }
            userRepository.deleteById(deleteUser.getId());
            return ResponseDTO.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/api/setRole")
    public ResponseDTO setRole(HttpServletRequest request) {
        try {
            String username = request.getParameter("username");
            String group = request.getParameter("group");

            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseDTO.builder()
                        .success(false)
                        .error("Invalid username")
                        .build();
            }
            user.setGroup(group);
            userRepository.save(user);
            return ResponseDTO.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }
    @PostMapping("/api/password")
    public ResponseDTO changePassword(HttpServletRequest request) {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseDTO.builder()
                        .success(false)
                        .error("Invalid username")
                        .build();
            }
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return ResponseDTO.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/api/login")
    public ResponseDTO login(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                request.logout();
            }
            request.login(username, password);
            return ResponseDTO
                    .builder()
                    .success(true)
                    .build();
        } catch (ServletException e) {
            return ResponseDTO
                    .builder()
                    .success(false)
                    .error("Incorrect username of password.")
                    .build();
        }

    }

    @GetMapping("/api/logout")
    public ResponseDTO logout(HttpServletRequest request) {
        try {
            request.logout();
            return ResponseDTO
                    .builder()
                    .success(true)
                    .build();
        } catch (ServletException e) {
            return ResponseDTO
                    .builder()
                    .success(false)
                    .error("Fail to logout")
                    .build();
        }
    }

    @PostMapping("/api/signup")
    public ResponseDTO signup(HttpServletRequest request) {
        try {
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            User newUser = userRepository.findByUsername(username);
            if (newUser != null) {
                return ResponseDTO.builder()
                        .success(false)
                        .error(String.format("Username %s has already been taken.", username))
                        .build();
            } else {
                newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(passwordEncoder.encode(password));
                newUser.setEmail(email);
                newUser.setGroup("USER");
            }
            userRepository.save(newUser);
            return ResponseDTO.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }
}
