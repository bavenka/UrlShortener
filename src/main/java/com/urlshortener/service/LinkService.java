package com.urlshortener.service;

import com.urlshortener.model.dto.EditedLinkDto;
import com.urlshortener.model.dto.LinkWithUserDto;
import com.urlshortener.model.dto.RegisteredLinkDto;
import com.urlshortener.model.dto.LinkDto;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Pavel on 20.01.2017.
 */
@Service
public interface LinkService {

    LinkDto saveLink(String username, RegisteredLinkDto registeredLinkDto) throws Exception;

    LinkWithUserDto getLink(String linkToken) throws Exception;

    void incrementClickNumberOnShortLink(LinkWithUserDto linkWithUserDto);

    LinkDto updateLink(String username, long linkId, EditedLinkDto editedLinkDto) throws Exception;

    Set<LinkDto> getUserLinksByUsername(String username) throws Exception;

    Set<LinkDto> getLinksByHashTag(String hashTag);

    LinkDto getUserLinkWithStatistics(String username, long id) throws Exception;

    void deleteLink (String username, long id) throws Exception;
}
