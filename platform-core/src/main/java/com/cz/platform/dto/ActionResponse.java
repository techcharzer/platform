package com.cz.platform.dto;

import lombok.Data;

@Data
public class ActionResponse {
	private boolean success;
	private IActionResponseData data;
}