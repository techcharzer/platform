package com.cz.platform.logging;

import java.util.LinkedHashMap;
import java.util.Map;

import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.contrib.json.JsonLayoutBase;

public class CustomJsonLayout extends JsonLayoutBase<ILoggingEvent> {

	public static final String TIMESTAMP_ATTR_NAME = "timestamp";
	public static final String LEVEL_ATTR_NAME = "level";
	public static final String THREAD_ATTR_NAME = "thread";
	public static final String MDC_ATTR_NAME = "mdc";
	public static final String LOGGER_ATTR_NAME = "logger";
	public static final String FORMATTED_MESSAGE_ATTR_NAME = "message";
	public static final String MESSAGE_ATTR_NAME = "raw-message";
	public static final String EXCEPTION_ATTR_NAME = "exception";
	public static final String CONTEXT_ATTR_NAME = "context";

	protected boolean includeLevel;
	protected boolean includeThreadName;
	protected boolean includeMDC;
	protected boolean includeLoggerName;
	protected boolean includeFormattedMessage;
	protected boolean includeMessage;
	protected boolean includeException;
	protected boolean includeContextName;

	private ThrowableHandlingConverter throwableProxyConverter;

	public CustomJsonLayout() {
		super();
		this.includeLevel = true;
		this.includeThreadName = true;
		this.includeMDC = true;
		this.includeLoggerName = true;
		this.includeFormattedMessage = true;
		this.includeException = true;
		this.includeContextName = true;
		this.throwableProxyConverter = new ThrowableProxyConverter();
	}

	@Override
	public void start() {
		this.throwableProxyConverter.start();
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		this.throwableProxyConverter.stop();
	}

	@Override
	protected Map toJsonMap(ILoggingEvent event) {

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		addTimestamp(TIMESTAMP_ATTR_NAME, this.includeTimestamp, event.getTimeStamp(), map);
		add(LEVEL_ATTR_NAME, this.includeLevel, String.valueOf(event.getLevel()), map);
		add(THREAD_ATTR_NAME, this.includeThreadName, event.getThreadName(), map);
		addMap(MDC_ATTR_NAME, this.includeMDC, event.getMDCPropertyMap(), map);
		add(LOGGER_ATTR_NAME, this.includeLoggerName, event.getLoggerName(), map);
		add(FORMATTED_MESSAGE_ATTR_NAME, this.includeFormattedMessage, event.getFormattedMessage(), map);
		add(MESSAGE_ATTR_NAME, this.includeMessage, event.getMessage(), map);
		add(CONTEXT_ATTR_NAME, this.includeContextName, event.getLoggerContextVO().getName(), map);
		addThrowableInfo(EXCEPTION_ATTR_NAME, this.includeException, event, map);

		addCustomDataToJsonMap(map, event);
		return map;
	}

	protected void addThrowableInfo(String fieldName, boolean field, ILoggingEvent value, Map<String, Object> map) {
		if (field && value != null) {
			IThrowableProxy throwableProxy = value.getThrowableProxy();
			if (throwableProxy != null) {
				String ex = throwableProxyConverter.convert(value);
				if (ex != null && !ex.equals("")) {
					map.put(fieldName, ex);
				}
			}
		}
	}

	/**
	 * Override to add custom data to the produced JSON from the logging event.
	 * Useful if you e.g. want to include the parameter array as a separate json
	 * attribute.
	 *
	 * @param map   the map for JSON serialization, populated with data
	 *              corresponding to the configured attributes. Add new entries from
	 *              the event to this map to have them included in the produced
	 *              JSON.
	 * @param event the logging event to extract data from.
	 */
	protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent le) {
		StringBuffer message = new StringBuffer();
		String loggerName = le.getLoggerName().replaceFirst("com.cars24.sell24.", "");
		message.append(loggerName);
		StackTraceElement[] cda = le.getCallerData();
		if (cda != null && cda.length > 0) {
			message.append(".");
			message.append(cda[0].getMethodName());
			message.append("(");
			message.append(Integer.toString(cda[0].getLineNumber()));
			message.append(")");
			message.append(": ");
		}
		message.append(le.getFormattedMessage());
		add(FORMATTED_MESSAGE_ATTR_NAME, this.includeFormattedMessage, message.toString(), map);
	}

	public boolean isIncludeLevel() {
		return includeLevel;
	}

	public void setIncludeLevel(boolean includeLevel) {
		this.includeLevel = includeLevel;
	}

	public boolean isIncludeLoggerName() {
		return includeLoggerName;
	}

	public void setIncludeLoggerName(boolean includeLoggerName) {
		this.includeLoggerName = includeLoggerName;
	}

	public boolean isIncludeFormattedMessage() {
		return includeFormattedMessage;
	}

	public void setIncludeFormattedMessage(boolean includeFormattedMessage) {
		this.includeFormattedMessage = includeFormattedMessage;
	}

	public boolean isIncludeMessage() {
		return includeMessage;
	}

	public void setIncludeMessage(boolean includeMessage) {
		this.includeMessage = includeMessage;
	}

	public boolean isIncludeMDC() {
		return includeMDC;
	}

	public void setIncludeMDC(boolean includeMDC) {
		this.includeMDC = includeMDC;
	}

	public boolean isIncludeThreadName() {
		return includeThreadName;
	}

	public void setIncludeThreadName(boolean includeThreadName) {
		this.includeThreadName = includeThreadName;
	}

	public boolean isIncludeException() {
		return includeException;
	}

	public void setIncludeException(boolean includeException) {
		this.includeException = includeException;
	}

	public boolean isIncludeContextName() {
		return includeContextName;
	}

	public void setIncludeContextName(boolean includeContextName) {
		this.includeContextName = includeContextName;
	}

	public ThrowableHandlingConverter getThrowableProxyConverter() {
		return throwableProxyConverter;
	}

	public void setThrowableProxyConverter(ThrowableHandlingConverter throwableProxyConverter) {
		this.throwableProxyConverter = throwableProxyConverter;
	}
}
