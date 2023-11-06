package ro.siit.FinalProject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final UserRepository userRepository;

    public User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByUsername(username);
        if(isAuthenticated()) {
            return user.get();
        }
        return null;
    }

    public boolean isAuthenticated(){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }
}
