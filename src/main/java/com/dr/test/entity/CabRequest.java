package com.dr.test.entity;

import java.util.Date;

public class CabRequest {
	private String id;
	private Date date;

	public String getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public CabRequest(String id, Date date) {
		this.id = id;
		this.date = date;
	}
}
