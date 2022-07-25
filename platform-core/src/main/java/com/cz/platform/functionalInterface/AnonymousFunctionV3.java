package com.cz.platform.functionalInterface;

@FunctionalInterface
public interface AnonymousFunctionV3<A, B, C, R> {
	R execute(A a, B b, C c);
}
