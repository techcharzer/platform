package com.cz.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProfitShareConfiguration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -21090774840692732L;
	private Double charzerShare;
	private UserShare dealerShare;
	private UserShare premiseOwnerShare;
}
