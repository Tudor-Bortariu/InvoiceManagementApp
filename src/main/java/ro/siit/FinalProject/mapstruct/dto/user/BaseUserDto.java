package ro.siit.FinalProject.mapstruct.dto.user;

import lombok.Data;

@Data
public abstract class BaseUserDto {
    private String firstName;
    private String lastName;
}
