package com.sugarware.tweebot.api.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sugarware.tweebot.api.entity.Subscription;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Serializable> {

	public Subscription findByUserId(@Param("userId") long userId);

	public Iterable<Subscription> findBySubscription(@Param("subscription") int subscription);
}
