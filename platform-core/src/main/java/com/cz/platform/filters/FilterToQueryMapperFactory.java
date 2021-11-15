package com.cz.platform.filters;

import org.springframework.stereotype.Component;

@Component
public abstract class FilterToQueryMapperFactory {

	public abstract GenericFilterToQueryMapper getService(Class a);

}
