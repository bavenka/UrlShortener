package com.urlshortener.controller;

import com.urlshortener.model.dto.EditingLinkDto;
import com.urlshortener.model.dto.LinkWithUserDto;
import com.urlshortener.model.dto.RegistrationLinkDto;
import com.urlshortener.model.dto.LinkDto;
import com.urlshortener.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Set;

/**
 * Created by Pavel on 20.01.2017.
 */
@RestController
public class LinkController {

    @Autowired
    private LinkService linkService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<String> createLink(@Valid @RequestBody RegistrationLinkDto registrationLinkDto) throws Exception {
        try {
            linkService.saveLink(SecurityContextHolder.getContext().getAuthentication().getName(),
                    registrationLinkDto);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.GET)
    public void redirect(@PathVariable(name = "token") String token,
                         HttpServletResponse response) throws Exception {
        LinkWithUserDto linkWithUserDto = linkService.getLinkByToken(token);
        if (linkWithUserDto == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            linkService.incrementClickNumberOnShortLink(linkWithUserDto);
            response.sendRedirect(linkWithUserDto.getLinkDto().getUrl());
        }
    }

    @RequestMapping(value = "/users/{username}/links/", method = RequestMethod.GET)
    public ResponseEntity<Set<LinkDto>> getUserLinks(@PathVariable String username) {
        Set<LinkDto> linkDtos = linkService.getUserLinksByUsername(username);
        if (linkDtos == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(linkService.getUserLinksByUsername(username), HttpStatus.OK);
    }

    @RequestMapping(value = "/links/", method = RequestMethod.GET)
    public ResponseEntity<Set<LinkDto>> getLinksByHashTag(@RequestParam(name = "hashtag") String hashTag) {
        Set<LinkDto> linkDtos = linkService.getLinksByHashTag(hashTag);
        if (linkDtos == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(linkService.getLinksByHashTag(hashTag), HttpStatus.OK);
    }

    @PreAuthorize("#username == authentication.name")
    @RequestMapping(value = "/users/{username}/links/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateLink(@PathVariable String username, @PathVariable(name = "id") long linkId,
                                             @Valid @RequestBody EditingLinkDto editingLinkDto) throws Exception {
        try {
            linkService.updateLink(SecurityContextHolder.getContext().getAuthentication().getName(),
                    linkId, editingLinkDto);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("#username == authentication.name")
    @RequestMapping(value = "/users/{username}/links/{id}/statistics/", method = RequestMethod.GET)
    public ResponseEntity<LinkDto> getLinkStatistics(@PathVariable String username,
                                               @PathVariable long id) {
        LinkDto linkDto = linkService.getUserLinkWithStatistics(SecurityContextHolder.getContext().getAuthentication().getName(),
                id);
        if (linkDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(linkDto, HttpStatus.OK);
    }

    @PreAuthorize("#username == authentication.name")
    @RequestMapping(value = "/users/{username}/links/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteLink(@PathVariable String username,
                                             @PathVariable long id) throws Exception {
        try {
            linkService.deleteLink(SecurityContextHolder.getContext().getAuthentication().getName(),
                    id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
