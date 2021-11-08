package com.cz.platform.filters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

import com.cz.platform.dto.Range;

public class RangeFilter<T extends Comparable<T>> extends AbstractFilter {

	private List<Range<T>> ranges;

	public RangeFilter() {
		super(null, FilterOperationsType.RANGE);
	}

	public RangeFilter(String field, List<Range<T>> value) {
		super(field, FilterOperationsType.RANGE);
		this.ranges = value;
	}

	public RangeFilter(String field, Range<T> value) {
		super(field, FilterOperationsType.RANGE);
		ranges = new ArrayList<Range<T>>();
		ranges.add(value);
	}

	public RangeFilter(String field, T start, T end) {
		super(field, FilterOperationsType.RANGE);
		ranges = new ArrayList<>();
		ranges.add(new Range<T>(start, end));
	}

	public List<Range<T>> getRanges() {
		return ranges;
	}

	public void setRanges(List<Range<T>> ranges) {
		this.ranges = ranges;
	}

	public List<String> getValue() {
		List<String> values = new ArrayList<>();
		for (Range<T> range : ranges) {
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
		List<Range<T>> list = this.ranges;
		List<Criteria> listOfCriterias = new ArrayList<>();
		for (Range<T> val : list) {
			listOfCriterias.add(Criteria.where(field).gte(val.getFrom()).lte(val.getTo()));
		}
		Criteria[] array = listOfCriterias.toArray(new Criteria[listOfCriterias.size()]);
		criteria.orOperator(array);
		return criteria;
	}

}
