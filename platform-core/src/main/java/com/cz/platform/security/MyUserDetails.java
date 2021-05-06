package com.cz.platform.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetails implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		final UserDTO user = userService.findByUsername(id);

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