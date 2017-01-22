package com.urlshortener.controller;

import com.urlshortener.model.dto.EditedLinkDto;
import com.urlshortener.model.dto.LinkWithUserDto;
import com.urlshortener.model.dto.RegisteredLinkDto;
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
    public ResponseEntity<?> createLink(@Valid @RequestBody RegisteredLinkDto registeredLinkDto) throws Exception {
        LinkDto linkDto;
        try {
            linkDto = linkService.saveLink(SecurityContextHolder.getContext().getAuthentication().getName(),
                    registeredLinkDto);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(linkDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.GET)
    public void redirect(@PathVariable(name = "token") String token,
                         HttpServletResponse response) throws Exception {
        LinkWithUserDto linkWithUserDto = linkService.getLink(token);
        if (linkWithUserDto == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            linkService.incrementClickNumberOnShortLink(linkWithUserDto);
            response.sendRedirect(linkWithUserDto.getLinkDto().getUrl());
        }
    }

    @RequestMapping(value = "/users/{username}/links/", method = RequestMethod.GET)
    public ResponseEntity<?> getUserLinks(@PathVariable String username) throws Exception {
        Set<LinkDto> linkDtos;
        try {
            linkDtos = linkService.getUserLinksByUsername(username);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(linkDtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/links/", method = RequestMethod.GET)
    public ResponseEntity<?> getLinksByHashTag(@RequestParam(name = "hashtag") String hashTag) throws Exception {
        return new ResponseEntity<>(linkService.getLinksByHashTag(hashTag), HttpStatus.OK);
    }

    @PreAuthorize("#username == authentication.name")
    @RequestMapping(value = "/users/{username}/links/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateLink(@PathVariable String username, @PathVariable(name = "id") long userId,
                                        @Valid @RequestBody EditedLinkDto editedLinkDto) throws Exception {
        LinkDto linkDto;
        try {
            linkDto = linkService.updateLink(SecurityContextHolder.getContext().getAuthentication().getName(),
                    userId, editedLinkDto);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(linkDto, HttpStatus.OK);
    }

    @PreAuthorize("#username == authentication.name")
    @RequestMapping(value = "/users/{username}/links/{id}/statistics/", method = RequestMethod.GET)
    public ResponseEntity<?> getLinkStatistics(@PathVariable String username,
                                               @PathVariable long id) throws Exception {
        LinkDto linkDto;
        try {
            linkDto = linkService.getUserLinkWithStatistics(SecurityContextHolder.getContext().getAuthentication().getName(),
                    id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(linkDto, HttpStatus.OK);
    }

    @PreAuthorize("#username == authentication.name")
    @RequestMapping(value = "/users/{username}/links/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteLink(@PathVariable String username,
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
