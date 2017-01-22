package com.urlshortener.service;

import com.urlshortener.model.dto.RegisteredUserDto;
import org.springframework.stereotype.Service;

/**
 * Created by Pavel on 05.01.2017.
 */
@Service
public interface UserService {

    void registerUser(RegisteredUserDto registeredUserDto) throws Exception;
}
