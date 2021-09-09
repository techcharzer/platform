package com.cz.platform;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import com.cz.platform.enums.ChargerType;
import com.cz.platform.enums.ConnectivityType;
import com.cz.platform.exception.PlatformExceptionCodes;

public class PlatformConstants {
	private PlatformConstants() {

	}

	public static final String CURRENT_TIME_ZONE = "Asia/Kolkata";
	public static final ZoneId CURRENT_ZONE_ID = ZoneId.of(CURRENT_TIME_ZONE);
	public static final String EXTERNAL_CLIENT = "externalClient";
	public static final String EXTERNAL_SLOW_CLIENT = "externalSlowClient";
	public static final String SUCCESS = "success";
	public static final String REQUEST_SUBMITTED = "request submitted";
	public static final String X_TRACE_ID = "x-trace-id";
	public static final String PARENT_PACKAGE = "com.cz";
	public static final String DEV_PROFILE = "dev";
	public static final String QA_PROFILE = "qa";
	public static final String PROD_PROFILE = "prod";
	public static final String APP_CONFIG_PREFIX = "app.config.";
	public static final String CUSTOM_EVENT_CONFIG_PREFIX = APP_CONFIG_PREFIX + "events";
	public static final String CORS_CONFIG_PREFIX = APP_CONFIG_PREFIX + "cors";
	public static final String SECURITY_CONFIG_PREFIX = APP_CONFIG_PREFIX + "security";
	public static final String URL_CONFIG_PREFIX = APP_CONFIG_PREFIX + "url";
	public static final String SSO_TOKEN_HEADER = "x-sso-token";
	public static final String MAP_MY_INDIA_SERVICE_REV_GEOCODE_SERVICE = "mapMyIndiaRevGeoCodeService";
	public static final String DEFAULT_ROLE_ID = "DEFAULT_ROLE";
	public static final String CODE_404 = PlatformExceptionCodes.NOT_FOUND.getCode();
	public static final String REDIS_TEMPLATE_FOR_UNIQUE_NUMBERS = "redisUniqueTemplate";
	public static final Map<ChargerType, ConnectivityType> CHARGER_TYPE_TO_CONNECTIVITY = new HashMap<>();
}
