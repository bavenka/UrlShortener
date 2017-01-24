package com.urlshortener.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by Pavel on 22.01.2017.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDto extends RegistrationUserDto {

    long id;
    @NotNull
    @NotEmpty
    private boolean accountNonLocked;
    @NotNull
    @NotEmpty
    private boolean accountNonExpired;
    @NotNull
    @NotEmpty
    private boolean credentialsNonExpired;
    @NotNull
    @NotEmpty
    private boolean enabled;
}
