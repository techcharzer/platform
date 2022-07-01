package com.cz.platform.whitelabel;

import lombok.Data;

@Data
public class ReferralConfig {
	private Long referrerCreditUnits;
	private Long refereeCreditUnits;
	private Long firstBookingReferrerCreditUnits;
	private Long walletInitializationCredits;
	private String referralMessageTemplate;
}
