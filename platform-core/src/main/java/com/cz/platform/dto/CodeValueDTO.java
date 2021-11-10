package com.cz.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodeValueDTO<T, U> {
	private T code;
	private U value;
}