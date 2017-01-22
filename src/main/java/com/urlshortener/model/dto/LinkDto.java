package com.urlshortener.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * Created by Pavel on 21.01.2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class LinkDto {

    private long id;
    @NotNull
    @NotEmpty
    private String url;
    @NotEmpty
    @NotNull
    private String token;
    @NotNull
    @NotEmpty
    private Date creationDate;
    private String description;
    @NotNull
    @NotEmpty
    private Integer clickCount;
    private Set<String> tags;
}
