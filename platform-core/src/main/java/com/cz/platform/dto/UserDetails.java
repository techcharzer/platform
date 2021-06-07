package com.cz.platform.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class UserDetails {
	private String userId;
	private String mobileNumber;
	private List<String> protectedChargerNetworkId;
	private LocalDateTime ceatedAt;
	private LocalDateTime updatedAt;
}
