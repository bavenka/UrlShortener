package com.urlshortener.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Created by Pavel on 21.01.2017.
 */
@Getter
@Setter
@NoArgsConstructor
public class EditingLinkDto {

    private String description;
    private Set<String> tags;
}
