package com.cz.platform.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodeValueDTO<T, U> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2603846881117825438L;
	private T code;
	private U value;
}