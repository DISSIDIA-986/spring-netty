package com.dissidia986.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dissidia986.dao.AppDao;
import com.dissidia986.model.ChatLogin;

@Repository
public class AppDaoImpl implements AppDao {
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;


	
	// ChatLogin
	@Override
	public List<ChatLogin> findChatLoginListByParams(Map<String, Object> params) throws Exception{
		return sqlSessionTemplate.selectList("user.findChatLoginByParams", params);
	}

	@Override
	public ChatLogin findChatLoginByParams(Map<String, Object> params) throws Exception{
		return sqlSessionTemplate.selectOne("user.findChatLoginByParams", params);
	}

	@Override
	public int updateChatLogin(Map<String, Object> params) throws Exception {
		return sqlSessionTemplate.update("user.updateChatLogin", params);
	}

	@Override
	public int insertChatLogin(ChatLogin pojo) throws Exception{
		return sqlSessionTemplate.insert("user.insertChatLogin", pojo);
	}
	
}
