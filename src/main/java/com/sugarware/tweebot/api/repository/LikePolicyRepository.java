package com.sugarware.tweebot.api.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sugarware.tweebot.api.entity.LikePolicy;

@Repository
public interface LikePolicyRepository extends CrudRepository<LikePolicy, Serializable> {

	public Iterable<LikePolicy> findByUserId(@Param("userId") long userId);
	public LikePolicy findByPolicyId(@Param("policyId")int policyId);
}
