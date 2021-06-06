package com.cz.platform.dto;

import java.time.LocalDateTime;

import com.cz.platform.security.ChargerNetworkUserMapping;

import lombok.Data;

@Data
public class UserDetails {
	private String userId;
	private String mobileNumber;
	private ChargerNetworkUserMapping society;
	private LocalDateTime ceatedAt;
	private LocalDateTime updatedAt;
}
