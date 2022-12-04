package io.muzoo.ssc.backend.auth;

import io.muzoo.ssc.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class OurUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        io.muzoo.ssc.backend.User u = userRepository.findByUsername(username);
        if (u != null){
            return User.withUsername(u.getUsername())
                    .password(u.getPassword()).roles(u.getGroup()).build();
        } else {
            throw new UsernameNotFoundException("Invalid username or password!!!!!");
        }
    }
}
