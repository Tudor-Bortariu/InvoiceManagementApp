package ro.siit.FinalProject.mapstruct.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.stereotype.Service;
import ro.siit.FinalProject.mapstruct.dto.user.CreateUserDto;
import ro.siit.FinalProject.mapstruct.response.user.SimpleUserResponse;
import ro.siit.FinalProject.model.User;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = "spring")
@Service
public interface UserMapper {
    User createUserDtoToUser(CreateUserDto createUserDto);
    SimpleUserResponse userToSimpleUserResponse(User user);
}
