package com.cz.platform.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cz.platform.security.ChargerNetworkUserMapping;

import lombok.Data;

@Data
public class UserDetails {
	private String userId;
	private String mobileNumber;
	private String email;
	private List<String> roles = new ArrayList<>();
	private ChargerNetworkUserMapping society;
	private LocalDateTime ceatedAt;
	private LocalDateTime updatedAt;
}
