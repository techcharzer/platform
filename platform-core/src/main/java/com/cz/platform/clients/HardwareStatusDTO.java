package com.cz.platform.clients;

import java.time.Instant;

import lombok.Data;

@Data
public class HardwareStatusDTO {
	private Boolean isOnline = false;
	private Instant lastSeen;

}
