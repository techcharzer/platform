package com.cz.platform.whitelabel;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReferralConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8057640553967405531L;
	private Long referrerCreditUnits;
	private Long refereeCreditUnits;
	private Long firstBookingReferrerCreditUnits;
	private String referralMessageTemplate;
}
