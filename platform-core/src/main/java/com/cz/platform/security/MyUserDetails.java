package com.cz.platform.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetails implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		final UserLoggedInDTO user = new UserLoggedInDTO();
		List<RoleDTO> roles = new ArrayList<>();
		user.setRoles(roles);
		user.setUserId(id);

		if (user == null) {
			throw new UsernameNotFoundException("User '" + id + "' not found");
		}
		Set<Permission> permissions = Utility.getPermissions(user.getRoles());

		return org.springframework.security.core.userdetails.User//
				.withUsername(id)//
				.password("")//
				.authorities(permissions)//
				.accountExpired(false)//
				.accountLocked(false)//
				.credentialsExpired(false)//
				.disabled(false)//
				.build();
	}

}