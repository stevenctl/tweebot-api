package com.sugarware.tweebot.api.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AccessToken {

	@Id
	private long userId;
	private String oauth_token;
	private String oauth_token_secret;
	
}
