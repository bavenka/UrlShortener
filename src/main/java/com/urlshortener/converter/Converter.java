package com.urlshortener.converter;

import com.urlshortener.model.dto.UserDto;
import com.urlshortener.model.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created by Pavel on 20.01.2017.
 */
public class Converter {

    public static User toUserEntity(UserDto userDto) {

        User user = new User();
        user.setUsername(userDto.getUsername());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        return user;

    }
}
