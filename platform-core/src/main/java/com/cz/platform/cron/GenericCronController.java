package com.cz.platform.cron;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cz.platform.dto.SuccessDTO;

@RestController
public class GenericCronController {

	@Lazy
	@Autowired
	private GenericCronService filterService;

	@PutMapping("/secure/cron/{key}")
	@Secured("ROLE_EXECUTE_CRON")
	public SuccessDTO executeCron(@PathVariable("key") String key) {
		filterService.executeCron(key);
		return SuccessDTO.of();
	}

}
