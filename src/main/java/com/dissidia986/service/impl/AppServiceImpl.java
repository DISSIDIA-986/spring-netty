package com.dissidia986.service.impl;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.velocity.app.VelocityEngine;
import org.nerdronix.springnetty.websocketx.server.ext.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dissidia986.dao.AppDao;
import com.dissidia986.model.ChatLogin;
import com.dissidia986.service.AppService;
import com.dissidia986.util.WtUtils;

@Service("appService")
public class AppServiceImpl implements AppService {
	@Autowired
	private AppDao appDao;
	@Resource(name = "velocityEngine")
	private VelocityEngine velocityEngine;

	@Override
	@Transactional
	public void chatLogin(String address, LoginUser user) throws Exception {
		appDao.insertChatLogin(new ChatLogin(user.getLoginid(), user.getUsername(), address, new Date()));
	}

	@Override
	public String tranlateHTML(Map<String, Object> model,String path) {
		return WtUtils.mergeTemplate(velocityEngine, path, model);
	}
}
