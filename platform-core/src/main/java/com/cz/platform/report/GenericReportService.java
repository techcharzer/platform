package com.cz.platform.report;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.cz.platform.dto.Range;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericReportService {

	protected static final Map<String, CustomReportFetcher> MAP_OF_DATA_FETCHERS = new HashMap<>();

	@FunctionalInterface
	public interface CustomReportFetcher {
		List<ReportSingleRowDTO> fetchData(IFetchReportRequest request);
	}

	public Page<ReportSingleRowDTO> getDashBoardCardDTO(IFetchReportRequest request, Pageable page) {
		if (ObjectUtils.isEmpty(request)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid request");
		}
		if (ObjectUtils.isEmpty(request.getKey())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid key");
		}
		if (!MAP_OF_DATA_FETCHERS.containsKey(request.getKey())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid report type");
		}
		List<ReportSingleRowDTO> rows = new ArrayList<>();
		try {
			if (page.getPageNumber() == 0) {
				rows = MAP_OF_DATA_FETCHERS.get(request.getKey()).fetchData(request);
			}
			return new PageImpl<>(rows, page, 1);
		} catch (Exception e) {
			log.error("error occured while fetching the report: {}", request, e);
			throw new ApplicationException(PlatformExceptionCodes.INVALID_DATA.getCode(), e.getMessage());
		}
	}

	public static enum StepSizeType {
		MONTHLY, DAILY
	}

	public static interface IFetchReportRequest {
		String getKey();

		Range<Instant> getRange();

		StepSizeType getStepSize();
	}

	public static interface ReportSingleRowDTO {

	}

}
