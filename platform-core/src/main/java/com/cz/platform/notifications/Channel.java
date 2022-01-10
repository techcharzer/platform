package com.cz.platform.notifications;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum Channel {
	PUSH_NOTIFICATION, SMS, WHATSAPP(new Channel[] { Channel.SMS });

	Channel[] fallbackChannel;
}
