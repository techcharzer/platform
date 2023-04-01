package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
@Deprecated
public class UserShare implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7588296092632312704L;
	private String userId;
	private String mobileNumber;
	private Double share;
}
