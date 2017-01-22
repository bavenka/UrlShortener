package com.urlshortener.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Pavel on 22.01.2017.
 */
@Getter
@Setter
@NoArgsConstructor
public class LinkWithUserDto {
    private LinkDto linkDto;
    private UserDto userDto;
}
