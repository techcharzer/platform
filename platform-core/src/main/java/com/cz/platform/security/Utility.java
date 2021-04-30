package com.cz.platform.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.util.ObjectUtils;

public class Utility {

	public static Set<Permission> getPermissions(UserDTO user) {
		Set<Permission> permissions = new HashSet<>();
		if (!ObjectUtils.isEmpty(user.getRoles())) {
			for (RoleDTO role : user.getRoles()) {
				if (!ObjectUtils.isEmpty(role.getPermissions())) {
					for (String permission : role.getPermissions()) {
						permissions.add(new Permission(permission));
					}
				}
			}
		}
		return permissions;
	}

}
