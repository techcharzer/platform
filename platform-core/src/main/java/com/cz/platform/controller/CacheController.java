package com.cz.platform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cz.platform.cache.ICacheCommonService;
import com.cz.platform.dto.SuccessDTO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CacheController {

	private ICacheCommonService service;

	@PutMapping("/secure/refreshCache")
	public SuccessDTO refreshCache(@RequestBody List<String> listOfCache) {
		service.refreshCache(listOfCache);
		return SuccessDTO.of();
	}
}
