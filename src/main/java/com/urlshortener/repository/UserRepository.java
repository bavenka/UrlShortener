package com.urlshortener.repository;

import com.urlshortener.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Pavel on 26.12.2016.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);
}
