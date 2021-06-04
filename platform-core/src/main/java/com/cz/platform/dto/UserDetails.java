package com.cz.platform.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.cz.platform.security.ProtectedChargerNetworkUserMapping;

import lombok.Data;

@Data
public class UserDetails {
	private String userId;
	private String mobileNumber;
	private String email;
	private List<String> roles;
	private ProtectedChargerNetworkUserMapping society;
	private LocalDateTime ceatedAt;
	private LocalDateTime updatedAt;
}
