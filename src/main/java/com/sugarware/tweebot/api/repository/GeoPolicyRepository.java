package com.sugarware.tweebot.api.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sugarware.tweebot.api.entity.GeoPolicy;

@Repository
public interface GeoPolicyRepository extends CrudRepository<GeoPolicy, Serializable> {

	public GeoPolicy findByUserId(@Param("userId") long userId);

}
