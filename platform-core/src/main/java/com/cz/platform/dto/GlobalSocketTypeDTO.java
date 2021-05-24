package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class GlobalSocketTypeDTO implements Serializable {

	private static final long serialVersionUID = 4659175221798020044L;
	private String code;
	private String displayText;
	private String imageRelativePath;
	private Boolean isActive;
}
