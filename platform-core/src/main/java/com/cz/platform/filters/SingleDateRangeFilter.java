package com.cz.platform.filters;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

import com.cz.platform.dto.Range;

public class SingleDateRangeFilter extends AbstractFilter {

	private Range<Instant> range;

	public SingleDateRangeFilter() {
		super(null, FilterOperationsType.SINGLE_DATE_RANGE);
	}

	public SingleDateRangeFilter(String field, Range<Instant> value) {
		super(field, FilterOperationsType.SINGLE_DATE_RANGE);
		range = value;
	}

	public SingleDateRangeFilter(String field, Instant start, Instant end) {
		super(field, FilterOperationsType.SINGLE_DATE_RANGE);
		range = new Range<Instant>(start, end);
	}

	public Range<Instant> getRange() {
		return range;
	}

	public void setRanges(Range<Instant> ranges) {
		this.range = ranges;
	}

	public String getValue() {
		return MessageFormat.format("{0}-{1}", String.valueOf(range.getFrom()), String.valueOf(range.getTo()));
	}

	@Override
	public String toString() {
		return "SingleDateRangeFilter [ranges=" + range + ", filterType=" + filterType + ", field=" + field + "]";
	}

	@Override
	public Criteria getCriteria() {
		Criteria criteria = new Criteria();
		List<Criteria> listOfCriterias = new ArrayList<>();
		listOfCriterias.add(Criteria.where(field).gte(range.getFrom()).lt(range.getTo()));
		Criteria[] array = listOfCriterias.toArray(new Criteria[listOfCriterias.size()]);
		criteria.orOperator(array);
		return criteria;
	}

}
