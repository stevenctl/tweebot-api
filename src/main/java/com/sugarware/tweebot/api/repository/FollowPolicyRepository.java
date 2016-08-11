package com.sugarware.tweebot.api.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sugarware.tweebot.api.entity.FollowPolicy;

@Repository
public interface FollowPolicyRepository extends CrudRepository<FollowPolicy, Serializable> {

	public Iterable<FollowPolicy> findByUserId(@Param("userId") long userId);

	public FollowPolicy findByPolicyId(@Param("policyId") int policyId);
}
