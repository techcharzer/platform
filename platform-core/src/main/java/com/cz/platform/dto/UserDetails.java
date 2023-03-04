package com.cz.platform.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class UserDetails implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8315971748398515838L;
	private String userId;
	private String mobileNumber;
	private String name;
	private String email;
	private List<String> chargePointOperators;
}
