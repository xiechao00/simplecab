package com.dr.test.api;

import com.dr.test.util.Constant;
import com.dr.test.util.Util;
import com.dr.test.dao.DAO;
import com.dr.test.entity.CabRequest;
import com.dr.test.entity.LRUCache;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimpleCab {
	private LRUCache<String, Integer> cache;
	private DAO dao;

	public SimpleCab(DAO dao, int cacheSize) {
		this.dao = dao;
		cache = new LRUCache(cacheSize);
	}

	//mode 1 : cache: fetch from cache, if not hit, then load from db and update cache
	//mode 2 : direct: direct load, and update cache anyway
	public List<Integer> query(List<CabRequest> requests, boolean useCache) throws SQLException {
		List<Integer> res = new ArrayList();

		for (CabRequest request : requests) {
			String key = key(request.getId(), request.getDate());
			Integer val;
			if (useCache) {
				val = cache.get(key);
				if (val == null) {
					val = loadToCache(key, request);
				}
			} else {
				val = loadToCache(key, request);
			}
			res.add(val);
		}

		return res;
	}

	public void clearCache() {
		cache.clear();
	}

	private Integer loadToCache(String key, CabRequest request) throws SQLException {
		Integer val = dao.getTripCountsByIdAndDate(request.getId(), request.getDate());
		cache.put(key, val);
		return val;
	}

	private String key(String id, Date date) {
		return id + File.separator + Util.dateToString(date, Constant.DATE_FORMAT);
	}
}
