package com.cz.platform.cron;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cz.platform.dto.SuccessDTO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class GenericCronController {

	private GenericCronService filterService;

	@PutMapping("/secure/cron/{key}")
	@Secured("ROLE_EXECUTE_CRON")
	public SuccessDTO getCZOInventoryFilter(@PathVariable("key") String key) {
		filterService.executeCron(key);
		return SuccessDTO.of();
	}

}
