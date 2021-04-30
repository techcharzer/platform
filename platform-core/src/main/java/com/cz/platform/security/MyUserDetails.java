package com.cz.platform.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class MyUserDetails implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final UserDTO user = userService.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("User '" + username + "' not found");
		}
		Set<Permission> permissions = Utility.getPermissions(user);

		return org.springframework.security.core.userdetails.User//
				.withUsername(username)//
				.password("")//
				.authorities(permissions)//
				.accountExpired(false)//
				.accountLocked(false)//
				.credentialsExpired(false)//
				.disabled(false)//
				.build();
	}

}