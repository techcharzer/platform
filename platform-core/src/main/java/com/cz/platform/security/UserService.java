package com.cz.platform.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {

	public UserLoggedInDTO findByUsername(String id) {
		// TODO Auto-generated method stub
		UserLoggedInDTO dto = new UserLoggedInDTO();
		List<RoleDTO> roles = new ArrayList<>();
		dto.setRoles(roles);
		dto.setUserId(id);

		return dto;
	}

}
