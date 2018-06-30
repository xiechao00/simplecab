package com.dr.test;

import com.dr.test.entity.LRUCache;
import org.junit.Assert;
import org.junit.Test;

public class LRUCacheTest {
	class Operation {
		private String oper; //get or put or contains
		private String key;
		private Integer expectedValue; //get - expected value, put - not used, contains - 1 means key existing, 0 means key N/A

		public Operation(String oper, String key, Integer expectedValue) {
			this.oper = oper;
			this.key = key;
			this.expectedValue = expectedValue;
		}
	}
	private LRUCache<String, Integer> cache;

	@Test
	public void test001MinorSizeCap() {
		cache = new LRUCache<>(-3);
		assertExpected(new Operation[]{
				new Operation("put", "1", 1),
				new Operation("get", "2", null),
				new Operation("contains", "2", 0),
				new Operation("get", "1", null),
				new Operation("contains", "1", 0),
		});
	}
	@Test
	public void test002ZeroCap() {
		cache = new LRUCache<>(0);

		assertExpected(new Operation[]{
				new Operation("get", "1", null),
				new Operation("put", "1", 5),
				new Operation("get", "1", null)
		});

	}

	@Test
	public void test003PutNullValue() {
		cache = new LRUCache<>(3);

		assertExpected(new Operation[]{
				new Operation("put", "1", 1),
				new Operation("put", "2", 2),
				new Operation("put", "1", null),
				new Operation("put", "3", 3),
				new Operation("put", "1", 4),
				new Operation("get", "2", 2),
				new Operation("put", "1", null),
				new Operation("get", "1", null),
				new Operation("contains", "1", 1),
				new Operation("put", "4", 4),
				new Operation("put", "5", 5),
				new Operation("put", "6", null),
				new Operation("get", "1", null),//evicted
				new Operation("contains", "1", 0),
		});
	}

	@Test
	public void test004PutNullKey() {
		cache = new LRUCache<>(3);

		assertExpected(new Operation[]{
				new Operation("put", null, 1),
				new Operation("get", null, 1),
				new Operation("put", null, 2),
				new Operation("get", null, 2)
		});
	}

	@Test
	public void test005NullKeyNullValue() {
		cache = new LRUCache<>(3);
		assertExpected(new Operation[] {
				new Operation("put", null, 1),
				new Operation("put", null, 2),
				new Operation("get", null, 2),
				new Operation("put", null, null),
				new Operation("get", null, null),
				new Operation("contains", null, 1),
		});
	}

	@Test
	public void test006Comprehensive() {
		cache = new LRUCache<>(2);

		assertExpected(new Operation[]{
				new Operation("get", "1", null),
				new Operation("put", "1", 1),
				new Operation("put", "2", 2),
				new Operation("get", "1", 1),
				new Operation("put", "3", 3),
				new Operation("get", "2", null),
				new Operation("put", "4", 4),
				new Operation("get", "1", null),
				new Operation("get", "3", 3),
				new Operation("get", "4", 4)
		});
	}

	@Test
	public void test007Comprehensive() {
		cache = new LRUCache<>(10);
		assertExpected(new Operation[] {
				new Operation("put", "1", 1),
				new Operation("put", "2", 2),
				new Operation("put", "3", 3),
				new Operation("put", "4", 4),
				new Operation("put", "5", 5),
				new Operation("put", "6", 6),
				new Operation("put", "7", 7),
				new Operation("put", "8", 8),
				new Operation("put", "9", 9),
				new Operation("put", "10", 10),
				new Operation("get", "1", 1),
				new Operation("put", "10", 20),
				new Operation("get", "2", 2),
				new Operation("put", "11", 11),
				new Operation("get", "3", null), //evicted
				new Operation("contains", "3", 0),
				new Operation("put", "6", 16),
				new Operation("get", "6", 16),
				new Operation("get", "4", 4),
				new Operation("put", "12", 12),
				new Operation("get", "5", null), //evicted
				new Operation("contains", "5", 0),
				new Operation("put", "8", null),
				new Operation("get", "8", null),
				new Operation("contains", "8", 1),
				new Operation("put", null, 21),
				new Operation("get", "7", null), //evicted
				new Operation("contains", "7", 0),
				new Operation("put", "7", 17),
				new Operation("get", "9", null), //evicted
				new Operation("contains", "9", 0),
				new Operation("get", "1", 1),
				new Operation("get", "10", 20),
				new Operation("get", "2", 2),
				new Operation("get", "11", 11),
				new Operation("get", "6", 16),
				new Operation("get", "4", 4),
				new Operation("get", "12", 12),
				new Operation("get", "8", null), //value null
				new Operation("contains", "8", 1)
		});
	}

	private void assertExpected(Operation[] operations) {
		for (Operation o : operations) {
			String oper = o.oper;
			if ("get".equalsIgnoreCase(oper)) {
				Assert.assertEquals(o.expectedValue, cache.get(o.key));
			} else if ("put".equalsIgnoreCase(o.oper)){
				cache.put(o.key, o.expectedValue);
			} else if ("contains".equalsIgnoreCase(o.oper)) {
				Integer actual = cache.containsKey(o.key) ? 1 : 0;
				Assert.assertEquals(o.expectedValue, actual);
			}
		}
	}
}
