package io.muzoo.ssc.backend.whoami;

import io.muzoo.ssc.backend.User;
import io.muzoo.ssc.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WhoAmIController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/whoAmI")
    public WhoAmIDTO whoAmI() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
                User u = userRepository.findByUsername(user.getUsername());
                return WhoAmIDTO.builder()
                        .loggedIn(true)
                        .name(u.getUsername()) // we don't have a name field so i use username but you can add it yourself.
                        .role(u.getGroup())
                        .username(u.getUsername())
                        .build();
            }
        } catch (Exception e) {
        }
        return WhoAmIDTO.builder()
                .loggedIn(false)
                .build();
    }
}
