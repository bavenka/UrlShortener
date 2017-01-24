package com.urlshortener.service.impl;

import com.urlshortener.constant.Constant;
import com.urlshortener.converter.Converter;
import com.urlshortener.model.dto.RegistrationUserDto;
import com.urlshortener.model.entity.User;
import com.urlshortener.repository.UserRepository;
import com.urlshortener.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * Created by Pavel on 05.01.2017.
 */
@Component
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private boolean emailExist(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

    private boolean usernameExist(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return true;
        }
        return false;
    }


    @Override
    public void registerUser(RegistrationUserDto registrationUserDto) throws Exception {
        if (emailExist(registrationUserDto.getEmail())) {
            throw new Exception(Constant.MESSAGE_EMAIL_EXIST);
        }
        if (usernameExist(registrationUserDto.getUsername())) {
            throw new Exception(Constant.MESSAGE_USERNAME_EXIST);
        }
        User user = Converter.toRegisteredUser(registrationUserDto);
        userRepository.save(user);
    }
}
