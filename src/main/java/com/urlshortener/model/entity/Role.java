package com.urlshortener.model.entity;

import com.urlshortener.model.SuperClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * Created by Pavel on 25.12.2016.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends SuperClass implements GrantedAuthority {

    @Column(unique = true, nullable = false)
    private String authority;
}
