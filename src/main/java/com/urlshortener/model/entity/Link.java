package com.urlshortener.model.entity;

import com.urlshortener.model.SuperClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Pavel on 20.01.2017.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "links")
public class Link extends SuperClass {

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    private String url;
    private String token;
    private String description;
    private int clickCount;
    private String tags;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
