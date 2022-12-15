package com.cz.platform.otp;

import com.cz.platform.whitelabel.WhiteLabelAppTypeEnum;

import lombok.Data;

@Data
public class OTPRequest {
	private String mobileNumber;
	private String verificationFor;
	private WhiteLabelAppTypeEnum appSource;
}
