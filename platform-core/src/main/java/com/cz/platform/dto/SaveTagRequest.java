package com.cz.platform.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class SaveTagRequest {
	private String entityId;
	private Set<String> tags = new HashSet<>();
}