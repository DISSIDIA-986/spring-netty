package com.dissidia986.model;

import java.util.Date;

public class ChatLogin {
	private int id;
	private String loginid;
	private String username;
	private String address;
	private Date createtime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public ChatLogin(String loginid, String username, String address, Date createtime) {
		super();
		this.loginid = loginid;
		this.username = username;
		this.address = address;
		this.createtime = createtime;
	}

	public ChatLogin() {
	}

}
