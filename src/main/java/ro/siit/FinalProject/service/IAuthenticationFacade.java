package ro.siit.FinalProject.service;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
}
