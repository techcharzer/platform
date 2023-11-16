package com.cz.platform.report;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cz.platform.dto.Range;
import com.cz.platform.report.GenericReportService.IFetchReportRequest;
import com.cz.platform.report.GenericReportService.ReportCardDTO;
import com.cz.platform.report.GenericReportService.StepSizeType;
import com.cz.platform.security.SecurityUtils;
import com.cz.platform.security.UserDTO;

import lombok.Data;

@RestController
@RequestMapping("/secure/report")
public class ReportController {

	@Lazy
	@Autowired
	private GenericReportService filterService;

	@GetMapping("/app/{key}")
	public Page<ReportCardDTO> getReportCardDTOForAPP(@PathVariable("key") String key,
			@RequestParam("from") long fromEpochSecond, @RequestParam("to") long toEpochSecond,
			@RequestParam(name = "stepSize", defaultValue = "DAILY") StepSizeType stepSize, Pageable page) {
		UserDTO user = SecurityUtils.getLoggedInUser();
		Instant from = Instant.ofEpochSecond(fromEpochSecond);
		Instant to = Instant.ofEpochSecond(toEpochSecond);
		Range<Instant> range = new Range<Instant>(from, to);
		DHAppReportRequest request = new DHAppReportRequest();
		request.setRange(range);
		request.setStepSize(stepSize);
		request.setKey(key);
		request.setUserId(user.getUserId());
		return filterService.getDashBoardCardDTO(request, page);
	}

	@Data
	public static class DHAppReportRequest implements IFetchReportRequest {
		private String key;
		private String userId;
		private Range<Instant> range;
		private StepSizeType stepSize;
	}

	@GetMapping("/czo/{key}")
	public Page<ReportCardDTO> getDashBoardCardDTOForCZO(@PathVariable("key") String key,
			@RequestParam("from") long fromEpochSecond, @RequestParam("to") long toEpochSecond,
			@RequestParam(name = "stepSize", defaultValue = "DAILY") StepSizeType stepSize, Pageable page) {
		Instant from = Instant.ofEpochSecond(fromEpochSecond);
		Instant to = Instant.ofEpochSecond(toEpochSecond);
		Range<Instant> range = new Range<Instant>(from, to);
		CZOReportRequest request = new CZOReportRequest();
		request.setRange(range);
		request.setStepSize(stepSize);
		request.setKey(key);
		return filterService.getDashBoardCardDTO(request, page);
	}

	@Data
	public static class CZOReportRequest implements IFetchReportRequest {
		private String key;
		private Range<Instant> range;
		private StepSizeType stepSize;
	}

	@GetMapping("/cms/{key}")
	public Page<ReportCardDTO> getDashBoardCardDTOForCMS(@PathVariable("key") String key,
			@RequestParam("from") long fromEpochSecond, @RequestParam("to") long toEpochSecond,
			@RequestParam(name = "stepSize", defaultValue = "DAILY") StepSizeType stepSize, Pageable page) {
		Instant from = Instant.ofEpochSecond(fromEpochSecond);
		Instant to = Instant.ofEpochSecond(toEpochSecond);
		Range<Instant> range = new Range<Instant>(from, to);
		CMSReportRequest request = new CMSReportRequest();
		request.setRange(range);
		request.setStepSize(stepSize);
		UserDTO user = SecurityUtils.getLoggedInUser();
		request.setKey(key);
		request.setChargePointOperatorId(user.getChargePointOperatorId());
		return filterService.getDashBoardCardDTO(request, page);
	}

	@Data
	public static class CMSReportRequest implements IFetchReportRequest {
		private String key;
		private String chargePointOperatorId;
		private Range<Instant> range;
		private StepSizeType stepSize;
	}

	@GetMapping("/group/{groupId}/{key}")
	public Page<ReportCardDTO> getDashBoardCardDTOForUserGroup(@PathVariable("key") String key,
			@PathVariable("groupId") String groupId, @RequestParam("from") long fromEpochSecond,
			@RequestParam("to") long toEpochSecond,
			@RequestParam(name = "stepSize", defaultValue = "DAILY") StepSizeType stepSize, Pageable page) {
		Instant from = Instant.ofEpochSecond(fromEpochSecond);
		Instant to = Instant.ofEpochSecond(toEpochSecond);
		Range<Instant> range = new Range<Instant>(from, to);
		UserGroupReportRequest request = new UserGroupReportRequest();
		request.setRange(range);
		request.setStepSize(stepSize);
		request.setKey(key);
		request.setGroupId(groupId);
		return filterService.getDashBoardCardDTO(request, page);
	}

	@Data
	public static class UserGroupReportRequest implements IFetchReportRequest {
		private String key;
		private String groupId;
		private Range<Instant> range;
		private StepSizeType stepSize;
	}

	@GetMapping("/fleet/{fleetId}/{key}")
	public Page<ReportCardDTO> getDashBoardCardDTOForFleet(@PathVariable("key") String key,
			@PathVariable("fleetId") String fleetId, @RequestParam("from") long fromEpochSecond,
			@RequestParam("to") long toEpochSecond,
			@RequestParam(name = "stepSize", defaultValue = "DAILY") StepSizeType stepSize, Pageable page) {
		Instant from = Instant.ofEpochSecond(fromEpochSecond);
		Instant to = Instant.ofEpochSecond(toEpochSecond);
		Range<Instant> range = new Range<Instant>(from, to);
		FleetReportRequest request = new FleetReportRequest();
		request.setRange(range);
		request.setStepSize(stepSize);
		request.setKey(key);
		request.setFleetId(fleetId);
		return filterService.getDashBoardCardDTO(request, page);
	}

	@Data
	public static class FleetReportRequest implements IFetchReportRequest {
		private String key;
		private String fleetId;
		private Range<Instant> range;
		private StepSizeType stepSize;
	}

}
