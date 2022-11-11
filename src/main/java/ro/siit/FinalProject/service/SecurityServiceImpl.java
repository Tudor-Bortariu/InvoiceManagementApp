package ro.siit.FinalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ro.siit.FinalProject.model.CustomUserDetails;
import ro.siit.FinalProject.model.User;

@Service
public class SecurityServiceImpl implements SecurityService{
    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @Override
    public User getUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }
}
