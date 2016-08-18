package com.sugarware.tweebot.api.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class GeoPolicy {

	@Id
	private long userId;
	private int zipCode;
	private int radius;
	private boolean active;
	
}
