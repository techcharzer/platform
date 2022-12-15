package com.cz.platform.otp;

import lombok.Data;

@Data
public class VerifyOTPRequest {
	private String otpRequestId;
	private String otp;
	private String verificationFor;
}
