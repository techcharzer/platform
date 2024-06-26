package com.cz.platform.unique;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "auto_increment_entity")
class AutoIncrementEntity {
	@Id
	private String id;
	private Long value;
	private Instant updatedAt;
}
