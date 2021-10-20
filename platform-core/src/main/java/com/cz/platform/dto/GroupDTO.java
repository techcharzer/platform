package com.cz.platform.dto;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
public class GroupDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 80585674933958546L;
	private String id;
	private String name;
	private GroupType groupType;
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "groupType")
	@JsonSubTypes({ @Type(value = Society.class, name = "SOCIETY"), @Type(value = Fleet.class, name = "FLEET") })
	private GroupConfiguration details;

	private Boolean isActive;
	private String createdBy;
	private Instant createdAt;
	private Instant updatedAt;
}
