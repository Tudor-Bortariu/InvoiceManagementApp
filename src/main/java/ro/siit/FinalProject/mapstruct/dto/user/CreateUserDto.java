package ro.siit.FinalProject.mapstruct.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateUserDto extends BaseUserDto{
    private String username;
    private String password;
    private String email;
}
