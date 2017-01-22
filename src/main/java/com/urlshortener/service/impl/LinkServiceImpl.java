package com.urlshortener.service.impl;

import com.sun.deploy.util.StringUtils;
import com.urlshortener.constant.Constant;
import com.urlshortener.converter.Converter;
import com.urlshortener.model.dto.EditedLinkDto;
import com.urlshortener.model.dto.LinkDto;
import com.urlshortener.model.dto.LinkWithUserDto;
import com.urlshortener.model.dto.RegisteredLinkDto;
import com.urlshortener.model.entity.Link;
import com.urlshortener.model.entity.User;
import com.urlshortener.repository.LinkRepository;
import com.urlshortener.repository.UserRepository;
import com.urlshortener.service.LinkService;
import org.apache.commons.lang3.RandomStringUtils;
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
        UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
        if (urlValidator.isValid(url)) {
            return true;
        }
        return false;
    }

    private boolean userHasSoLink(User user, String originalUrl) {
        if (user != null && !user.getLinks().isEmpty()) {
            Set<Link> links = user.getLinks();
            for (Link link : links) {
                if (link.getUrl().equals(originalUrl)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String generateToken(String url) throws Exception {
        if (!urlValid(url)) {
            throw new Exception(Constant.MESSAGE_NOT_VALID_URL);
        }
        return RandomStringUtils.randomAlphanumeric(6);
    }

    @Override
    public LinkDto saveLink(String username, RegisteredLinkDto registeredLinkDto) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new Exception(Constant.MESSAGE_NOT_FOUND_USER);
        }
        if (userHasSoLink(user, registeredLinkDto.getUrl())) {
            throw new Exception(Constant.MESSAGE_LINK_EXIST);
        }
        Link link = Converter.toRegisterLink(registeredLinkDto);
        String token = generateToken(registeredLinkDto.getUrl());
        link.setToken(token);
        link.setCreationDate(new Date());
        link.setUser(user);
        linkRepository.save(link);
        return Converter.toLinkWithoutStatisticsDto(link);
    }

    @Override
    public LinkWithUserDto getLink(String token) throws Exception {
        Link link = linkRepository.findLinkByToken(token);
        if (link == null) {
            throw new Exception(Constant.MESSAGE_NOT_FOUND_URL);
        }
        return Converter.toLinkWithUserDto(linkRepository.findLinkByToken(token));
    }

    @Override
    public void incrementClickNumberOnShortLink(LinkWithUserDto linkWithUserDto) {
        int clickCount = linkWithUserDto.getLinkDto().getClickCount();
        linkWithUserDto.getLinkDto().setClickCount(clickCount + 1);
        Link link = Converter.toLinkWithUser(linkWithUserDto);
        linkRepository.save(link);
    }

    @Override
    public LinkDto updateLink(String username, long id, EditedLinkDto editedLinkDto) throws Exception {
        Link link = linkRepository.findOne(id);
        if (link != null && link.getUser().getUsername().equals(username)) {
            link.setDescription(editedLinkDto.getDescription());
            link.setTags(StringUtils.join(editedLinkDto.getTags(), ", "));
            linkRepository.save(link);
            return Converter.toLinkWithoutStatisticsDto(link);
        }
        throw new Exception(Constant.MESSAGE_NOT_FOUND_URL);
    }

    @Override
    public Set<LinkDto> getUserLinksByUsername(String username) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new Exception(Constant.MESSAGE_NOT_FOUND_USER);
        }
        Set<LinkDto> linkDtos = new HashSet<>();
        if (!user.getLinks().isEmpty()) {
            for (Link link : user.getLinks()) {
                linkDtos.add(Converter.toLinkWithoutStatisticsDto(link));
            }
        }
        return linkDtos;
    }

    @Override
    public Set<LinkDto> getLinksByHashTag(String hashTag) {
        Set<Link> links = linkRepository.findByTagsIgnoreCaseContaining(hashTag);
        Set<LinkDto> linkDtos = new HashSet<>();
        if (links == null) {
            return linkDtos;
        }
        for (Link link : links) {
            linkDtos.add(Converter.toLinkWithoutStatisticsDto(link));
        }
        return linkDtos;
    }

    @Override
    public LinkDto getUserLinkWithStatistics(String username, long id) throws Exception {
        Link link = linkRepository.findOne(id);
        if (link == null || !link.getUser().getUsername().equals(username)) {
            throw new Exception(Constant.MESSAGE_NOT_FOUND_URL);
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
