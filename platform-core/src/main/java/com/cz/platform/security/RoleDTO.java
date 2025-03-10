package com.cz.platform.security;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RoleDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 176382974834L;
	private String roleId;
	private List<String> permissions;
}
