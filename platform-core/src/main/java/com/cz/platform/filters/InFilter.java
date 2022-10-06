package com.cz.platform.filters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

public class InFilter<T> extends AbstractFilter {
	private List<T> value;

	public InFilter() {
		super(null, FilterOperationsType.IN);
	}

	public InFilter(String field, List<T> value) {
		super(field, FilterOperationsType.IN);
		this.value = value;
	}

	public InFilter(String field, T value) {
		super(field, FilterOperationsType.IN);
		List<T> list = new ArrayList<>(1);
		list.add(value);
		this.value = list;
	}

	public List<T> getValue() {
		return value;
	}

	public void setValue(List<T> value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "InElasticFilter [value=" + value + ", filterType=" + filterType + ", field=" + field + "]";
	}

	@Override
	public Criteria getCriteria() {
		return Criteria.where(field).in(this.value);
	}

}
