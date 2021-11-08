package com.cz.platform.filters;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterDTO<T> implements Serializable {

	private static final long serialVersionUID = 1437584763973L;
	private T data;
	private ElementType type;
	private CollapseResponse collapseResponse;
	private String title;
	private String keyName;
	private String keySuffix;
	private Boolean isMultipleSelectionAllowed = Boolean.TRUE;
	private Boolean isMandatory = Boolean.FALSE;

}
