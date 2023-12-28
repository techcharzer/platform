package com.cz.platform.filters;

import java.util.List;

import lombok.Data;

@Data
public class CustomPageResponse<T> {
	private List<T> list;
	private long totalCount;
	private long offset;
	private int limit;
}
