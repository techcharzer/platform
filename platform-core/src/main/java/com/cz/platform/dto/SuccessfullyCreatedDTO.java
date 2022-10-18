package com.cz.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessfullyCreatedDTO {
	private String id;

	public static SuccessfullyCreatedDTO of(String id) {
		return new SuccessfullyCreatedDTO(id);
	}

}
