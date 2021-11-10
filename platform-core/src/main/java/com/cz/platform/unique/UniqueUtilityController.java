package com.cz.platform.unique;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cz.platform.dto.SuccessDTO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
class UniqueUtilityController {

	private UniqueUtilityService utility;

	@PutMapping("/secure/unique-utility/populate-cache")
	public SuccessDTO populateRedisCache() {
		utility.populateRedisCache();
		return SuccessDTO.of();
	}

	@GetMapping("/secure/unique-utility")
	public List<UniqueUtilityEntity> getListOfUniqueEntity() {
		return utility.getUniqueEntityList();
	}

}
