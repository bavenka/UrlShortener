package com.urlshortener.service.impl;

import com.sun.deploy.util.StringUtils;
import com.urlshortener.constant.Constant;
import com.urlshortener.converter.Converter;
import com.urlshortener.model.dto.EditingLinkDto;
import com.urlshortener.model.dto.LinkDto;
import com.urlshortener.model.dto.LinkWithUserDto;
import com.urlshortener.model.dto.RegistrationLinkDto;
import com.urlshortener.model.entity.Link;
import com.urlshortener.model.entity.User;
import com.urlshortener.repository.LinkRepository;
import com.urlshortener.repository.UserRepository;
import com.urlshortener.service.LinkService;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Pavel on 20.01.2017.
 */
@Component
public class LinkServiceImpl implements LinkService {

    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private UserRepository userRepository;

    private boolean urlValid(String url) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
        if (urlValidator.isValid(url)) {
            return true;
        }
        return false;
    }

    private boolean userHasSoLink(String originalUrl, String username) {
        Link link = linkRepository.findLinkByUrlAndUsername(originalUrl, username);
        if (link == null) {
            return false;
        }
        return true;
    }

    @Override
    public void saveLink(String username, RegistrationLinkDto registrationLinkDto) throws Exception {
        if (!urlValid(registrationLinkDto.getUrl())) {
            throw new Exception(Constant.MESSAGE_NOT_VALID_URL);
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new Exception(Constant.MESSAGE_NOT_FOUND_USER);
        }
        if (userHasSoLink(registrationLinkDto.getUrl(), username)) {
            throw new Exception(Constant.MESSAGE_LINK_EXIST);
        }
        Link link = Converter.toRegisterLink(registrationLinkDto);
        link.setCreationDate(new Date());
        link.setUser(user);
        linkRepository.save(link);
    }

    @Override
    public LinkWithUserDto getLinkByToken(String token) {
        Link link = linkRepository.findLinkByToken(token);
        if (link == null) {
            return null;
        }
        return Converter.toLinkWithUserDto(link);
    }

    @Override
    public void incrementClickNumberOnShortLink(LinkWithUserDto linkWithUserDto) {
        int clickCount = linkWithUserDto.getLinkDto().getClickCount();
        linkWithUserDto.getLinkDto().setClickCount(clickCount + 1);
        Link link = Converter.toLinkWithUser(linkWithUserDto);
        linkRepository.save(link);
    }

    @Override
    public void updateLink(String username, long id, EditingLinkDto editingLinkDto) throws Exception {
        Link link = linkRepository.findOne(id);
        if (link != null && link.getUser().getUsername().equals(username)) {
            link.setDescription(editingLinkDto.getDescription());
            link.setTags(StringUtils.join(editingLinkDto.getTags(), ", "));
            linkRepository.save(link);
            return;
        }
        throw new Exception(Constant.MESSAGE_NOT_FOUND_URL);
    }

    @Override
    public Set<LinkDto> getUserLinksByUsername(String username) {
        Set<Link> links = linkRepository.findLinksByUsername(username);
        Set<LinkDto> linkDtos = null;
        if (!links.isEmpty()) {
            linkDtos = new HashSet<>();
            for (Link link : links) {
                linkDtos.add(Converter.toLinkWithoutStatisticsDto(link));
            }
        }
        return linkDtos;
    }

    @Override
    public Set<LinkDto> getLinksByHashTag(String hashTag) {
        Set<Link> links = linkRepository.findByTagsIgnoreCaseContaining(hashTag);
        Set<LinkDto> linkDtos = null;
        if (!links.isEmpty()) {
            linkDtos = new HashSet<>();
            for (Link link : links) {
                linkDtos.add(Converter.toLinkWithoutStatisticsDto(link));
            }
        }
        return linkDtos;
    }

    @Override
    public LinkDto getUserLinkWithStatistics(String username, long id) {
        Link link = linkRepository.findOne(id);
        if (link == null || !link.getUser().getUsername().equals(username)) {
            return null;
        }
        return Converter.toWorkLinkDto(link);
    }

    @Override
    public void deleteLink(String username, long id) throws Exception {
        Link link = linkRepository.findOne(id);
        if (link == null || !link.getUser().getUsername().equals(username)) {
            throw new Exception(Constant.MESSAGE_NOT_FOUND_URL);
        }
        linkRepository.delete(link.getId());
    }

}
