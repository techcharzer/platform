package com.cz.platform.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cz.platform.utility.CommonUtility;

@Service
public class MyUserDetails implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		final LoggedInUser user = new LoggedInUser();
		List<RoleDTO> roles = new ArrayList<>();
		user.setRoles(roles);
		user.setUserId(id);

		Set<Permission> permissions = CommonUtility.getPermissions(user.getRoles());

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