package com.cz.platform.dto;

import lombok.Data;

@Data
public class FailedResponseData implements IActionResponseData {
	private String reason;
}
