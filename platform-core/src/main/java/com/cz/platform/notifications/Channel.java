package com.cz.platform.notifications;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum Channel {
	PUSH_NOTIFICATION, DATA_PUSH, SMS, WHATSAPP(new Channel[] { Channel.SMS });

	Channel[] fallbackChannel;
}
