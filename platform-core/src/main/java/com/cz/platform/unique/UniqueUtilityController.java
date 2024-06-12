package com.cz.platform.unique;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cz.platform.dto.SuccessDTO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
class UniqueUtilityController {

	private UniqueUtilityService utility;

	@PutMapping("/secure/unique-utility/new-logic")
	public SuccessDTO enableNewLogic() {
		utility.enableNewLogic();
		return SuccessDTO.of();
	}

}
