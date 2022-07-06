package com.cz.platform.filters;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

import com.cz.platform.dto.Range;

public class DateRangeFilter extends AbstractFilter {

	private List<Range<Instant>> ranges;

	public DateRangeFilter() {
		super(null, FilterOperationsType.RANGE);
	}

	public DateRangeFilter(String field, List<Range<Instant>> value) {
		super(field, FilterOperationsType.RANGE);
		this.ranges = value;
	}

	public DateRangeFilter(String field, Range<Instant> value) {
		super(field, FilterOperationsType.RANGE);
		ranges = new ArrayList<Range<Instant>>();
		ranges.add(value);
	}

	public DateRangeFilter(String field, Instant start, Instant end) {
		super(field, FilterOperationsType.RANGE);
		ranges = new ArrayList<>();
		ranges.add(new Range<Instant>(start, end));
	}

	public List<Range<Instant>> getRanges() {
		return ranges;
	}

	public void setRanges(List<Range<Instant>> ranges) {
		this.ranges = ranges;
	}

	public List<String> getValue() {
		List<String> values = new ArrayList<>();
		for (Range<Instant> range : ranges) {
			values.add(MessageFormat.format("{0}-{1}", String.valueOf(range.getFrom()), String.valueOf(range.getTo())));
		}
		return values;
	}

	@Override
	public String toString() {
		return "RangeElasticFilter [ranges=" + ranges + ", filterType=" + filterType + ", field=" + field + "]";
	}

	@Override
	public Criteria getCriteria() {
		Criteria criteria = new Criteria();
		List<Range<Instant>> list = this.ranges;
		List<Criteria> listOfCriterias = new ArrayList<>();
		for (Range<Instant> val : list) {
			listOfCriterias.add(Criteria.where(field).gte(val.getFrom()).lt(val.getTo()));
		}
		Criteria[] array = listOfCriterias.toArray(new Criteria[listOfCriterias.size()]);
		criteria.orOperator(array);
		return criteria;
	}

}
