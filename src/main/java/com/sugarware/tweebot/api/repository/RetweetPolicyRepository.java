package com.sugarware.tweebot.api.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sugarware.tweebot.api.entity.RetweetPolicy;

@Repository
public interface RetweetPolicyRepository extends CrudRepository<RetweetPolicy, Serializable> {

	public Iterable<RetweetPolicy> findByUserId(@Param("userId") long userId);

	public RetweetPolicy findByPolicyId(@Param("policyId") int policyId);
}
