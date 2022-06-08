package com.cz.platform.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cz.platform.dto.SuccessDTO;
import com.cz.platform.security.SecurityUtils;
import com.cz.platform.security.UserDTO;

@RestController
public class DashboardController {

	@Lazy
	@Autowired
	private GenericDashBoardService filterService;

	@GetMapping("/secure/dasboard/app/{key}")
	public SuccessDTO getDashBoardCardDTOForAPP(@PathVariable("key") String key) {
		UserDTO user = SecurityUtils.getLoggedInUser();
		filterService.getDashBoardCardDTO(key, user.getUserId());
		return SuccessDTO.of();
	}
	
	@GetMapping("/secure/dasboard/czo/{key}")
	@Secured("ROLE_EXECUTE_CRON")
	public SuccessDTO getDashBoardCardDTOForCZO(@PathVariable("key") String key) {
		UserDTO user = SecurityUtils.getLoggedInUser();
		filterService.getDashBoardCardDTO(key, user.getUserId());
		return SuccessDTO.of();
	}

}
