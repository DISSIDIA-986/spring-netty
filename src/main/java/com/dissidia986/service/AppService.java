package com.dissidia986.service;

import java.util.Map;

import org.nerdronix.springnetty.websocketx.server.ext.LoginUser;

public interface AppService {
	public void chatLogin(String address,LoginUser user) throws Exception;
	
	public String tranlateHTML(Map<String, Object> model,String path);
}
