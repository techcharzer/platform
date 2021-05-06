package com.cz.platform.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.ObjectUtils;

public class Utility {

	public static Set<Permission> getPermissions(List<RoleDTO> roles) {
		Set<Permission> permissions = new HashSet<>();
		if (!ObjectUtils.isEmpty(roles)) {
			for (RoleDTO role : roles) {
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
