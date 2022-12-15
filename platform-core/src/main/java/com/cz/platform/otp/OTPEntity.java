package com.cz.platform.otp;

import com.cz.platform.whitelabel.WhiteLabelAppTypeEnum;

import lombok.Data;

@Data
public class OTPEntity {
	private String mobileNumber;
	private String otp;
	private String verificationFor;
	private WhiteLabelAppTypeEnum appSource;
}
