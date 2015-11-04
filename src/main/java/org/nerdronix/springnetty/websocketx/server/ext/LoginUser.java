package org.nerdronix.springnetty.websocketx.server.ext;

public class LoginUser {
	private String loginid;
	private String username;


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

	public LoginUser() {
		super();
	}

	public LoginUser(String loginid, String username) {
		super();
		this.loginid = loginid;
		this.username = username;
	}

}
