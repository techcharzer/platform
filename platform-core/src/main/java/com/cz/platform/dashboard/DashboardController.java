package com.cz.platform.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cz.platform.security.SecurityUtils;
import com.cz.platform.security.UserDTO;

@RestController
public class DashboardController {

	@Lazy
	@Autowired
	private GenericDashBoardService filterService;

	@GetMapping("/secure/dashboard/app/{key}")
	public DashboardCardDTO getDashBoardCardDTOForAPP(@PathVariable("key") String key) {
		UserDTO user = SecurityUtils.getLoggedInUser();
		return filterService.getDashBoardCardDTO(key, user.getUserId());
	}
	
	@GetMapping("/secure/dashboard/czo/{key}")
	@Secured("ROLE_EXECUTE_CRON")
	public DashboardCardDTO getDashBoardCardDTOForCZO(@PathVariable("key") String key) {
		UserDTO user = SecurityUtils.getLoggedInUser();
		return filterService.getDashBoardCardDTO(key, user.getUserId());
	}

}
