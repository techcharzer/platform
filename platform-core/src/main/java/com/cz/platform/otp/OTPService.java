package com.cz.platform.otp;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.cz.platform.config.OTPConfig;
import com.cz.platform.exception.LoggerType;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;
import com.cz.platform.notifications.GenericNotificationService;
import com.cz.platform.utility.PlatformCommonService;
import com.cz.platform.whitelabel.WhiteLabelAppTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OTPService {

	@Autowired
	@Qualifier("redisOtpKey")
	private RedisTemplate<String, OTPEntity> redisTemplate;

	@Autowired
	private GenericNotificationService globalNotificationService;

	@Autowired
	private OTPConfig config;

	@Autowired
	private PlatformCommonService platformCommonService;

	private static final String OTP_BASE_KEY = "otpRequestIds:{0}";

	private static final Random random = new Random();

	public static final String REDIS_TEMPLATE_AUTH_KEY = "redisAuthKeyHash";

	public OTPSentResponseDTO sendOTP(OTPRequest request) {
		log.debug("send otp request : {}", request);
		validateRequest(request);
		// generate OTP
		String otpStr = config.getDefaultOtp();
		String mobileNumber = request.getMobileNumber();
		String otpRedisLockKey = MessageFormat.format("OTP_SEND_MESSAGE_{0}", mobileNumber);
		platformCommonService.takeLock(otpRedisLockKey, 5, "Please wait for 5 seconds to try again.", LoggerType.ERROR);

		boolean useRandomOTP = config.getUseRandomOtp();
		if (!ObjectUtils.isEmpty(config.getDefaultMobileNumberSet())
				&& config.getDefaultMobileNumberSet().contains(mobileNumber)) {
			useRandomOTP = false;
		}

		if (useRandomOTP) {
			int otp = random.nextInt(8999) + 1000;
			otpStr = String.format("%04d", otp);
		}

		// generate unique key
		String uniqueKey = UUID.randomUUID().toString();
		String key = MessageFormat.format(OTP_BASE_KEY, uniqueKey);

		// save in redis
		OTPEntity entity = new OTPEntity();
		entity.setMobileNumber(mobileNumber);
		entity.setOtp(otpStr);
		entity.setVerificationFor(request.getVerificationFor());
		WhiteLabelAppTypeEnum appSource = request.getAppSource();
		if (ObjectUtils.isEmpty(appSource)) {
			appSource = WhiteLabelAppTypeEnum.CHARZER_APP;
		}
		entity.setAppSource(appSource);
		redisTemplate.opsForValue().set(key, entity, 5, TimeUnit.MINUTES);

		log.debug("useRandomOTP: {} otprequestId : {}, otpEntity {}", useRandomOTP, uniqueKey, entity);
		// send message only if random otp is being sent
		if (useRandomOTP) {
			Map<String, String> data = new HashMap<>();
			data.put("otp", otpStr);
			globalNotificationService.sendSMS(mobileNumber, data, "OTP_TEMPLATE", request.getAppSource());
		}
		OTPSentResponseDTO otpResponseDTO = new OTPSentResponseDTO();
		otpResponseDTO.setOtpRequestId(uniqueKey);
		return otpResponseDTO;
	}

	private void validateRequest(OTPRequest request) {
		if (ObjectUtils.isEmpty(request)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid request");
		}
		if (ObjectUtils.isEmpty(request.getMobileNumber())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid mobileNumber");
		}
		if (ObjectUtils.isEmpty(request.getVerificationFor())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid verificationFor");
		}
		if (!StringUtils.isNumeric(request.getMobileNumber())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Mobile number must have only digits.");
		}
		if (request.getMobileNumber().length() != 10) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Mobile number must have 10 digits.");
		}

	}

	public void verifyOtp(VerifyOTPRequest verifyRequest) {
		log.debug("verify otp request : {}", verifyRequest);
		validateRequest(verifyRequest);
		String key = MessageFormat.format(OTP_BASE_KEY, verifyRequest.getOtpRequestId());
		OTPEntity otpEntity = redisTemplate.opsForValue().get(key);
		if (ObjectUtils.isEmpty(otpEntity)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid OTP");
		}
		if (!StringUtils.equalsIgnoreCase(verifyRequest.getOtp(), otpEntity.getOtp())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid OTP");
		}
		if (!StringUtils.equalsIgnoreCase(verifyRequest.getVerificationFor(), otpEntity.getVerificationFor())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid OTP");
		}
	}

	private void validateRequest(VerifyOTPRequest request) {
		if (ObjectUtils.isEmpty(request)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid request");
		}
		if (ObjectUtils.isEmpty(request.getOtpRequestId())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid OTP requestId");
		}
		if (ObjectUtils.isEmpty(request.getOtp())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid OTP");
		}
		if (ObjectUtils.isEmpty(request.getVerificationFor())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid verificationFor");
		}
	}

}
