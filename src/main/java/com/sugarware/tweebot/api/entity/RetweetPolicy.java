package com.sugarware.tweebot.api.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RetweetPolicy {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int policyId;
	private long userId;
	private String hashtag;
	private int retweets;

}
