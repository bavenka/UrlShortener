package com.urlshortener.repository;

import com.urlshortener.model.entity.Link;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Created by Pavel on 20.01.2017.
 */
@Repository
public interface LinkRepository extends CrudRepository<Link, Long> {

    Link findLinkByToken(String token);

    @Query(value = "SELECT * FROM LINKS L LEFT JOIN USERS U ON L.USER_ID = U.ID WHERE U.USERNAME=?1",
            nativeQuery = true)
    Set<Link> findLinksByUsername(String username);

    @Query(value = "SELECT * FROM LINKS L LEFT JOIN USERS U ON L.USER_ID = U.ID WHERE L.URL=?1 and U.USERNAME=?2",
            nativeQuery = true)
    Link findLinkByUrlAndUsername(String url, String username);

    Set<Link> findByTagsIgnoreCaseContaining(String hashTag);

}
