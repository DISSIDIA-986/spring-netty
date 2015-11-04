package com.dissidia986.dao;

import java.util.List;
import java.util.Map;

import com.dissidia986.model.ChatLogin;

public interface AppDao {
	
	// ChatLogin
	public List<ChatLogin> findChatLoginListByParams(Map<String, Object> params) throws Exception;

	public ChatLogin findChatLoginByParams(Map<String, Object> params) throws Exception;

	public int updateChatLogin(Map<String, Object> params) throws Exception;

	public int insertChatLogin(ChatLogin pojo) throws Exception;
	
}
