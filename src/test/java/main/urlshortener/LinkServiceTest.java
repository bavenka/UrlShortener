package main.urlshortener;

import com.urlshortener.Application;
import com.urlshortener.constant.Constant;
import com.urlshortener.model.dto.*;
import com.urlshortener.service.LinkService;
import com.urlshortener.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Pavel on 23.01.2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
@Rollback
public class LinkServiceTest {

    @Autowired
    private LinkService linkService;

    @Autowired
    private UserService userService;

    private RegistrationLinkDto registrationLinkDto;
    private RegistrationLinkDto additionalLinkDto;
    private EditingLinkDto editingLinkDto;
    private RegistrationUserDto registrationUserDto;

    private String getRandomToken() {
        return RandomStringUtils.randomAlphanumeric(6);
    }

    @Before
    public void initializeLink() {
        RegistrationLinkDto linkDto = new RegistrationLinkDto();
        linkDto.setUrl("https://emcd.grsu.by/login/index.php");
        linkDto.setToken(getRandomToken());
        linkDto.setDescription("Grsu portal");
        HashSet<String> tags = new HashSet<>();
        tags.add("#grsu");
        tags.add("#portal");
        tags.add("#university");
        tags.add("students");
        linkDto.setTags(tags);
        this.registrationLinkDto = linkDto;
    }

    @Before
    public void initializeAdditionalLink() {
        RegistrationLinkDto linkDto = new RegistrationLinkDto();
        linkDto.setUrl("http://people.onliner.by/2017/01/23/abc-2");
        linkDto.setToken(getRandomToken());
        linkDto.setDescription("ABC advertising");
        HashSet<String> tags = new HashSet<>();
        tags.add("#ABC");
        tags.add("#meal");
        tags.add("#portal");
        linkDto.setTags(tags);
        this.additionalLinkDto = linkDto;
    }

    @Before
    public void initializeEditingLink() {
        EditingLinkDto editingLinkDto = new EditingLinkDto();
        editingLinkDto.setDescription("Student Portal");
        HashSet<String> tags = new HashSet<>();
        tags.add("#book");
        tags.add("#mobile");
        editingLinkDto.setTags(tags);
        this.editingLinkDto = editingLinkDto;
    }

    @Before
    public void initializeUser() {
        String username = getRandomToken();
        String password = getRandomToken();
        String email = getRandomToken() + "@gmail.com";
        RegistrationUserDto userDto = new RegistrationUserDto();
        userDto = new RegistrationUserDto();
        userDto.setUsername(username);
        userDto.setPassword(password);
        userDto.setEmail(email);
        this.registrationUserDto = userDto;
    }

    @Test
    public void saveLinkSuccessfully() throws Exception {
        userService.registerUser(registrationUserDto);
        linkService.saveLink(registrationUserDto.getUsername(),
                registrationLinkDto);
        LinkWithUserDto linkWithUserDto = linkService.getLinkByToken(registrationLinkDto.getToken());
        assertNotNull(linkWithUserDto);
        assertEquals(registrationLinkDto.getToken(), linkWithUserDto.getLinkDto().getToken());
    }

    @Test
    public void saveExistingLinkFromCurrentUser() throws Exception {
        String exceptionName = "";
        userService.registerUser(registrationUserDto);
        linkService.saveLink(registrationUserDto.getUsername(), registrationLinkDto);
        try {
            linkService.saveLink(registrationUserDto.getUsername(), registrationLinkDto);
        } catch (Exception e) {
            exceptionName = e.getMessage();
        }
        assertEquals(Constant.MESSAGE_LINK_EXIST, exceptionName);
    }

    @Test
    public void incrementClickNumberOnShortLink() throws Exception {
        userService.registerUser(registrationUserDto);
        linkService.saveLink(registrationUserDto.getUsername(),
                registrationLinkDto);
        LinkWithUserDto linkWithUserDto = linkService.getLinkByToken(registrationLinkDto.getToken());
        linkService.incrementClickNumberOnShortLink(linkWithUserDto);
        int clickCountBeforeIncrement = linkWithUserDto.getLinkDto().getClickCount() - 1;
        assertEquals(0, clickCountBeforeIncrement);
    }

    @Test
    public void updateLink() throws Exception {
        userService.registerUser(registrationUserDto);
        linkService.saveLink(registrationUserDto.getUsername(), registrationLinkDto);
        LinkWithUserDto linkWithUserDto = linkService.getLinkByToken(registrationLinkDto.getToken());
        linkService.updateLink(registrationUserDto.getUsername(),
                linkWithUserDto.getLinkDto().getId(), editingLinkDto);
        LinkWithUserDto updatedLink = linkService.getLinkByToken(registrationLinkDto.getToken());
        assertNotNull(updatedLink);
        assertNotEquals(registrationLinkDto.getDescription(), updatedLink.getLinkDto().getDescription());
        assertNotEquals(registrationLinkDto.getTags(), updatedLink.getLinkDto().getTags());
    }

    @Test
    public void getUserLinksByUsername() throws Exception {
        userService.registerUser(registrationUserDto);
        linkService.saveLink(registrationUserDto.getUsername(), registrationLinkDto);
        linkService.saveLink(registrationUserDto.getUsername(), additionalLinkDto);
        Set<LinkDto> foundLinks = linkService.getUserLinksByUsername(registrationUserDto.getUsername());
        assertNotNull(foundLinks);
        assertEquals(2, foundLinks.size());
    }

    @Test
    public void getLinksByHashTag() throws Exception {
        String hashTag = "#portal";
        userService.registerUser(registrationUserDto);
        linkService.saveLink(registrationUserDto.getUsername(), registrationLinkDto);
        linkService.saveLink(registrationUserDto.getUsername(), additionalLinkDto);
        Set<LinkDto> foundLinks = linkService.getLinksByHashTag(hashTag);
        assertEquals(2, foundLinks.size());
        for (LinkDto linkDto : foundLinks) {
            assertTrue(linkDto.getTags().toString().contains(hashTag));
        }
    }

    @Test
    public void getUserLinkWithStatistics() throws Exception {
        userService.registerUser(registrationUserDto);
        linkService.saveLink(registrationUserDto.getUsername(), registrationLinkDto);
        LinkWithUserDto linkWithUserDto = linkService.getLinkByToken(registrationLinkDto.getToken());
        LinkDto foundLink = linkService.getUserLinkWithStatistics(registrationUserDto.getUsername(),
                linkWithUserDto.getLinkDto().getId());
        assertNotNull(foundLink);
        assertEquals(linkWithUserDto.getLinkDto().getId(), foundLink.getId());
    }

    @Test
    public void deleteLink() throws Exception {
        userService.registerUser(registrationUserDto);
        linkService.saveLink(registrationUserDto.getUsername(), registrationLinkDto);
        LinkWithUserDto linkWithUserDto = linkService.getLinkByToken(registrationLinkDto.getToken());
        linkService.deleteLink(registrationUserDto.getUsername(), linkWithUserDto.getLinkDto().getId());
        LinkWithUserDto deletedLink = linkService.getLinkByToken(registrationLinkDto.getToken());
        assertNull(deletedLink);
    }

}
