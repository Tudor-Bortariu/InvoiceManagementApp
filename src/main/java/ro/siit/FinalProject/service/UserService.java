package ro.siit.FinalProject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ro.siit.FinalProject.mapstruct.dto.user.CreateUserDto;
import ro.siit.FinalProject.mapstruct.mapper.user.UserMapper;
import ro.siit.FinalProject.mapstruct.response.user.SimpleUserResponse;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final UserMapper mapper;
    private final BCryptPasswordEncoder encoder;

    public SimpleUserResponse saveUser(CreateUserDto createUserDto){
        if(userRepository.findByUsername(createUserDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is not available. Please insert a different Username.");
        } else {
            User user = mapper.createUserDtoToUser(createUserDto);
            user.setPassword(encoder.encode(createUserDto.getPassword()));
            userRepository.saveAndFlush(user);
            return mapper.userToSimpleUserResponse(user);
        }
    }

    public SimpleUserResponse findAuthenticatedUser(){
        if(securityService.isAuthenticated()) {
            return mapper.userToSimpleUserResponse(securityService.getAuthenticatedUser());
        }
        return null;
    }
}
