package com.dr.test;

import com.dr.test.api.SimpleCab;
import com.dr.test.dao.DAO;
import com.dr.test.dao.MySQLLocalDemoDaoImpl;
import com.dr.test.entity.CabRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class SimpleCabTest {
	private static final String MODIFY_DB_SQL = "src/main/resources/modifydb.sql";
	private static final String RESTORE_DB_SQL = "src/main/resources/restoredb.sql";
	private SimpleCab cab;
	private Calendar calendar;
	private DAO dao;
	private List<CabRequest> requestList;

	@Before
	public void setup() throws Exception {
		calendar = Calendar.getInstance();
		dao = new MySQLLocalDemoDaoImpl();
		requestList = new ArrayList<CabRequest>() {
			{
				calendar.set(2013, Calendar.DECEMBER, 30);
				add(new CabRequest("A18CC3E9191D21F604DFC2423916E6A2", calendar.getTime()));
				calendar.set(2013, Calendar.DECEMBER, 1);
				add(new CabRequest("D7D598CD99978BD012A87A76A7C891B7", calendar.getTime()));
				calendar.set(2013, Calendar.DECEMBER, 31);
				add(new CabRequest("801C69A08B51470871A8110F8B0505EE", calendar.getTime()));
				calendar.set(2013, Calendar.DECEMBER, 15);
				add(new CabRequest("00000000000000000000000000000001", calendar.getTime()));

			}
		};
	}

	@Test
	public void test001Cache() throws Exception {
		cab = new SimpleCab(new MySQLLocalDemoDaoImpl(), 100);
		//always restore db first
		dao.runSqlScript(RESTORE_DB_SQL);

		//query cache - 1
		List<Integer> actual = cab.query(requestList, true);
		List<Integer> expected = new ArrayList<Integer>() {
			{
				add(25);
				add(3);
				add(1);
				add(0);
			}
		};
		assertResult(expected, actual);
		//modify db - 2
		dao.runSqlScript(MODIFY_DB_SQL);
		//query cache - 1
		actual = cab.query(requestList, true);
		assertResult(expected, actual);
		//restore db - 1
		dao.runSqlScript(RESTORE_DB_SQL);
	}

	@Test
	public void test002Direct() throws Exception {
		cab = new SimpleCab(new MySQLLocalDemoDaoImpl(), 100);
		//always restore db first
		dao.runSqlScript(RESTORE_DB_SQL);

		//query direct - 1
		List<Integer> actual = cab.query(requestList, false);
		List<Integer> expected1 = new ArrayList<Integer>() {
			{
				add(25);
				add(3);
				add(1);
				add(0);
			}
		};
		assertResult(expected1, actual);
		//modify db - 2
		dao.runSqlScript(MODIFY_DB_SQL);
		//query direct - 2
		actual = cab.query(requestList, false);
		List<Integer> expected2 = new ArrayList<Integer>() {
			{
				add(27);
				add(5);
				add(4);
				add(3);
			}
		};
		assertResult(expected2, actual);
		//restore db - 1
		dao.runSqlScript(RESTORE_DB_SQL);
		//query direct - 1
		actual = cab.query(requestList, false);
		assertResult(expected1, actual);
	}

	@Test
	public void test003CacheVsDirect() throws Exception {
		cab = new SimpleCab(new MySQLLocalDemoDaoImpl(), 100);
		//always restore db first
		dao.runSqlScript(RESTORE_DB_SQL);

		//query cache - 1
		List<Integer> actual = cab.query(requestList, true);
		List<Integer> expected1 = new ArrayList<Integer>() {
			{
				add(25);
				add(3);
				add(1);
				add(0);
			}
		};
		assertResult(expected1, actual);
		//modify db - 2
		dao.runSqlScript(MODIFY_DB_SQL);
		//query direct - 2 - cache updated to 2
		actual = cab.query(requestList, false);
		List<Integer> expected2 = new ArrayList<Integer>() {
			{
				add(27);
				add(5);
				add(4);
				add(3);
			}
		};
		assertResult(expected2, actual);
		//query cache - 2
		actual = cab.query(requestList, true);
		assertResult(expected2, actual);
		//restore db - 1
		dao.runSqlScript(RESTORE_DB_SQL);
		//query cache - 2
		actual = cab.query(requestList, true);
		assertResult(expected2, actual);
		//query direct - 1 - cache updated to 1
		actual = cab.query(requestList, false);
		assertResult(expected1, actual);
		//query cache - 1
		actual = cab.query(requestList, true);
		assertResult(expected1, actual);
	}

	@Test
	public void test004ClearCache() throws Exception {
		cab = new SimpleCab(new MySQLLocalDemoDaoImpl(), 100);
		//always restore db first
		dao.runSqlScript(RESTORE_DB_SQL);

		//query cache - 1
		List<Integer> actual = cab.query(requestList, true);
		List<Integer> expected1 = new ArrayList<Integer>() {
			{
				add(25);
				add(3);
				add(1);
				add(0);
			}
		};
		assertResult(expected1, actual);
		//clear cache
		cab.clearCache();
		//query cache - 1
		actual = cab.query(requestList, true);
		assertResult(expected1, actual);
		//modify db - 2
		dao.runSqlScript(MODIFY_DB_SQL);
		//clear cache
		cab.clearCache();
		//query cache - 2
		actual = cab.query(requestList, true);
		List<Integer> expected2 = new ArrayList<Integer>() {
			{
				add(27);
				add(5);
				add(4);
				add(3);
			}
		};
		assertResult(expected2, actual);
		//restore db - 1
		dao.runSqlScript(RESTORE_DB_SQL);
	}

	@Test
	public void test004ClearCacheVsDirect() throws Exception {
		cab = new SimpleCab(new MySQLLocalDemoDaoImpl(), 100);
		//always restore db first
		dao.runSqlScript(RESTORE_DB_SQL);

		//query direct - 1 - cache updated to 1
		List<Integer> actual = cab.query(requestList, false);
		List<Integer> expected1 = new ArrayList<Integer>() {
			{
				add(25);
				add(3);
				add(1);
				add(0);
			}
		};
		assertResult(expected1, actual);
		//modify db - 2
		dao.runSqlScript(MODIFY_DB_SQL);
		//query cache - 1
		actual = cab.query(requestList, true);
		assertResult(expected1, actual);
		//clear cache
		cab.clearCache();
		//query cache - 2 - load updated data
		actual = cab.query(requestList, true);
		List<Integer> expected2 = new ArrayList<Integer>() {
			{
				add(27);
				add(5);
				add(4);
				add(3);
			}
		};
		assertResult(expected2, actual);
		//query direct - 2
		actual = cab.query(requestList, false);
		assertResult(expected2, actual);
		//restore db - 1
		dao.runSqlScript(RESTORE_DB_SQL);
		//query cache - 2
		actual = cab.query(requestList, true);
		assertResult(expected2, actual);
		//clear cache
		cab.clearCache();
		//query cache - 1
		actual = cab.query(requestList, true);
		assertResult(expected1, actual);
		//query direct - 1
		actual = cab.query(requestList, false);
		assertResult(expected1, actual);
	}

	private void assertResult(List<Integer> expected, List<Integer> actual) {
		Iterator<Integer> expIt = expected.iterator();
		Iterator<Integer> actIt = actual.iterator();
		while (expIt.hasNext() && actIt.hasNext()) {
			Assert.assertEquals(expIt.next(), actIt.next());
		}
		Assert.assertEquals(expIt.hasNext(), actIt.hasNext());
	}
}
