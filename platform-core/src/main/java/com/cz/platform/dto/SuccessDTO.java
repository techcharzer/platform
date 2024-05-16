package com.cz.platform.dto;

import com.cz.platform.PlatformConstants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessDTO {
	private String message;

	private static final SuccessDTO SUCCESS = new SuccessDTO(PlatformConstants.SUCCESS);
	private static final SuccessDTO REQUEST_SUBMITTED = new SuccessDTO(PlatformConstants.REQUEST_SUBMITTED);

	public static SuccessDTO of() {
		return SUCCESS;
	}

	public static SuccessDTO requestSubmitted() {
		return REQUEST_SUBMITTED;
	}

}
