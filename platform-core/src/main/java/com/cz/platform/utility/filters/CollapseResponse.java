package com.cz.platform.utility.filters;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollapseResponse implements Serializable {

	private static final long serialVersionUID = 134345554656L;
	boolean isCollapsed;
	boolean collapsible;

}
