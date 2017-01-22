package com.urlshortener.repository;

import com.urlshortener.model.entity.Link;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by Pavel on 20.01.2017.
 */
@Repository
public interface LinkRepository extends CrudRepository<Link, Long> {

    Link findLinkByToken(String token);

    Set<Link> findByTagsIgnoreCaseContaining(String hashTag);

}
