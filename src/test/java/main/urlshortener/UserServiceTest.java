package main.urlshortener;

import com.urlshortener.Application;
import com.urlshortener.constant.Constant;
import com.urlshortener.model.dto.RegistrationUserDto;
import com.urlshortener.repository.UserRepository;
import com.urlshortener.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

/**
 * Created by Pavel on 23.01.2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
@Rollback
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private RegistrationUserDto userDto;

    private String getRandomToken() {
        return RandomStringUtils.randomAlphanumeric(7);
    }

    @Before
    public void initializeUser() {
        String username = getRandomToken();
        String password = getRandomToken();
        String email = getRandomToken() + "@gmail.com";
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername(username);
        registrationUserDto.setPassword(password);
        registrationUserDto.setEmail(email);
        this.userDto = registrationUserDto;
    }

    @After
    public void clear() {
        this.userDto = null;
    }

    @Test
    public void registerUserSuccessfully() throws Exception {
        RegistrationUserDto registrationUserDto = userDto;
        userService.registerUser(registrationUserDto);
        Assert.assertEquals(registrationUserDto.getUsername(),
                userRepository.findByUsername(userDto.getUsername()).getUsername());
    }

    @Test
    public void registerUserWithExistingEmail() throws Exception {
        String exceptionName = "";
        RegistrationUserDto registrationUserDto = userDto;
        userService.registerUser(registrationUserDto);
        try {
            registrationUserDto.setUsername(getRandomToken());
            userService.registerUser(registrationUserDto);
        } catch (Exception e) {
            exceptionName = e.getMessage();
        }
        Assert.assertEquals(Constant.MESSAGE_EMAIL_EXIST, exceptionName);
    }

    @Test
    public void registerUserWithExistingUsername() throws Exception {
        String exceptionName = "";
        RegistrationUserDto registrationUserDto = userDto;
        userService.registerUser(registrationUserDto);
        try {
            registrationUserDto.setEmail(getRandomToken() + "@gmail.com");
            userService.registerUser(registrationUserDto);
        } catch (Exception e) {
            exceptionName = e.getMessage();
        }
        Assert.assertEquals(Constant.MESSAGE_USERNAME_EXIST, exceptionName);
    }
}
