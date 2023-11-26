package ro.siit.FinalProject.mapstruct.response.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ro.siit.FinalProject.mapstruct.dto.user.BaseUserDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class SimpleUserResponse extends BaseUserDto {
}
