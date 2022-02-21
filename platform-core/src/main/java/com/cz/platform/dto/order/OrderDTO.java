package com.cz.platform.dto.order;

import java.time.Instant;
import java.util.List;

import com.cz.platform.dto.MarketingMaterial;
import com.cz.platform.dto.OrderChargerDetail;
import com.cz.platform.dto.UserDetails;
import com.cz.platform.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderDTO {
	private String orderId;
	private UserDetails contactDetails;
	private OrderData orderData;
	private List<OrderChargerDetail> chargerDetails;
	private MarketingMaterial marketingMaterial;
	private OrderStatus status;
	private Instant createdAt;
}
