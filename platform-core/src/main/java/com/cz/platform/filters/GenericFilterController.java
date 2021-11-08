package com.cz.platform.filters;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class GenericFilterController {

	private FilterService filterService;

	@GetMapping("/secure/filter/{key}")
	public List<FilterDTO<Object>> getCZOInventoryFilter(@PathVariable("key") String key) {
		return filterService.getFiltersViaConfigKey(key);
	}

}
