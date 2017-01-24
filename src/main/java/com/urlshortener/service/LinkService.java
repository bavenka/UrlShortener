package com.urlshortener.service;

import com.urlshortener.model.dto.EditingLinkDto;
import com.urlshortener.model.dto.LinkWithUserDto;
import com.urlshortener.model.dto.RegistrationLinkDto;
import com.urlshortener.model.dto.LinkDto;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Pavel on 20.01.2017.
 */
@Service
public interface LinkService {

    void saveLink(String username, RegistrationLinkDto registrationLinkDto) throws Exception;

    LinkWithUserDto getLinkByToken(String linkToken);

    void incrementClickNumberOnShortLink(LinkWithUserDto linkWithUserDto);

    void updateLink(String username, long linkId, EditingLinkDto editingLinkDto) throws Exception;

    Set<LinkDto> getUserLinksByUsername(String username);

    Set<LinkDto> getLinksByHashTag(String hashTag);

    LinkDto getUserLinkWithStatistics(String username, long id);

    void deleteLink(String username, long id) throws Exception;
}
