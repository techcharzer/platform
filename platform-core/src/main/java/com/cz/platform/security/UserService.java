package com.cz.platform.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {

	public UserDTO findByUsername(String username) {
		// TODO Auto-generated method stub
		UserDTO dto = new UserDTO();
		dto.setPassword("adjsfk");
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("ROLE_GAME"));
		dto.setRoles(roles);
		dto.setUserName("username");
		return dto;
	}

}