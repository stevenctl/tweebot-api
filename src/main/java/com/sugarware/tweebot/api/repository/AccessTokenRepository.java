package com.sugarware.tweebot.api.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sugarware.tweebot.api.entity.AccessToken;

@Repository
public interface AccessTokenRepository extends CrudRepository<AccessToken, Serializable> {

	public AccessToken findByUserId(@Param("userId") long userId);

	@Query("select t from AccessToken t where oauth_token = :oauth_token")
	public AccessToken findByOauthToken(@Param("oauth_token") String oauth_token);

}
