package com.hualala.cache.redis;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hualala.cache.service.UserService;

/**
 * 事件监听
 * @author lin.cheng
 */
@Service
@Slf4j
public class Receiver {

	@Autowired
	private UserService userService;

	public void receiveSetList(String redisType) {
		log.info("异步处理： <" + redisType + ">类型的全局数据。");
		if (redisType.equals("user")) {
			userService.updateAll();
		}
	}

}
