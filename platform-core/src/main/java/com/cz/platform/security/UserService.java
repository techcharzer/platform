package com.cz.platform.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {

	public UserDTO findByUsername(String id) {
		// TODO Auto-generated method stub
		UserDTO dto = new UserDTO();
		List<RoleDTO> roles = new ArrayList<>();
		dto.setRoles(roles);
		dto.setUserId(id);

		return dto;
	}

}
