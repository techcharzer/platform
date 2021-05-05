package com.cz.platform.maps;

import org.springframework.stereotype.Service;

import com.cz.platform.exception.ApplicationException;

@Service
public interface RevGeoCodingService {

	public RevGeoCodeAddressDTO getAddress(Double lat, Double lon) throws ApplicationException;

}
