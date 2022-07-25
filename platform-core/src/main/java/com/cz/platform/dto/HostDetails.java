package com.cz.platform.dto;

import lombok.Data;

@Data
public class HostDetails {
	private UserDetails userDetails;
	private HostConfiguration configuration;
}
