package com.cz.platform.cron;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cz.platform.dto.SuccessDTO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class GenericCronController {

	private GenericCronService filterService;

	@GetMapping("/secure/cron/{key}")
	public SuccessDTO getCZOInventoryFilter(@PathVariable("key") String key) {
		filterService.executeCron(key);
		return SuccessDTO.of();
	}

}
