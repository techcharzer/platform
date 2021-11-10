package com.cz.platform.unique;

import java.time.Instant;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
class UniqueUtilityEntity {
	@Id
	private String id;
	private Long value;
	private Instant updatedAt;
}
