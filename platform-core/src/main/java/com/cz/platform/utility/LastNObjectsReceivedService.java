package com.cz.platform.utility;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LastNObjectsReceivedService {

	private RedissonClient redissonClient;

	public <T> LastNObjectDataStore<T> getDataStore(String listKey, int size) {
		if (size <= 0 || size > 10000) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid size for dataStore");
		}
		LastNObjectDataStore<T> store = new LastNObjectDataStore<>();
		store.list = redissonClient.getList(listKey);
		for (int i = 0; i < size; ++i) {
			store.list.add(null);
		}
		String countKey = MessageFormat.format("{0}_COUNT", listKey);
		store.count = redissonClient.getAtomicLong(countKey);
		store.size = size;
		return store;
	}

	public static class LastNObjectDataStore<T> {
		private RList<T> list;
		private int size;
		private RAtomicLong count;

		public void add(T t) {
			long val = count.addAndGet(1L);
			int index = (int) (val % size);
			list.set(index, t);
		}

		public List<T> getLastElements() {
			long val = count.get();
			List<T> response = new ArrayList<>();
			List<T> localList = list.readAll();
			for (int i = 0; i < size; ++i) {
				int index = (int) ((val - i) % size);
				T t = localList.get(index);
				if (!ObjectUtils.isEmpty(t)) {
					response.add(t);
				}
			}
			return response;
		}

	}
}
