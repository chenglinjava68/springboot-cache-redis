package com.hualala.cache.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hualala.cache.ApplicationBoot;
import com.hualala.cache.domain.Address;
import com.hualala.cache.domain.User;
import com.hualala.cache.service.AddressService;
import com.hualala.cache.service.UserService;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationBoot.class)
@Slf4j
public class ExampleBootTests {

	@Autowired
	UserService userService;

	@Autowired
	AddressService addressService;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	private final static Long ID = 1L;

	@Before
	public void setUp() {
		userService.deleteAll();
		addressService.deleteAll();
	}

	@Test
	public void testUser() throws Exception {
		User user = new User(ID, "lin", "cheng");
		userService.save(user);
		String mapString = stringRedisTemplate.opsForValue().get("user:" + ID);
		assertThat(mapString).isEqualTo("[\"com.hualala.cache.domain.User\",{\"id\":1,\"firstName\":\"lin\",\"lastName\":\"cheng\",\"nickName\":\"aleenjava\",\"job\":\"softEngineer\",\"married\":true}]");
		Optional<User> optional = userService.findById(ID);
		assertThat(optional.isPresent()).isTrue();
		Thread.sleep(2000);
		log.info("延迟两秒再查询列表1，保证异步更新完毕....");
		Set<User> results = userService.findAll();
		assertThat(results.size()).isEqualTo(1);
		assertThat(results.stream().filter(u -> u.getFirstName().equals("lin")).findFirst().isPresent())
				.isEqualTo(true);
		//updated
		User changeUser = optional.get();
		changeUser.setFirstName("aleen");
		userService.update(changeUser);
		mapString = stringRedisTemplate.opsForValue().get("user:" + ID);
		assertThat(mapString).isEqualTo(
				"[\"com.hualala.cache.domain.User\",{\"id\":1,\"firstName\":\"aleen\",\"lastName\":\"cheng\",\"nickName\":\"aleenjava\",\"job\":\"softEngineer\",\"married\":true}]");
		optional = userService.findById(ID);
		assertThat(optional.isPresent()).isTrue();
		assertThat(optional.get().getFirstName()).isEqualTo("aleen");
		results = userService.findAll();
		Thread.sleep(2000);
		log.info("延迟两秒再查询列表2，保证异步更新完毕....");
		assertThat(results.size()).isEqualTo(2);
	    assertThat(results.stream().filter(u -> u.getFirstName().equals("aleen")).findFirst().isPresent())
				.isEqualTo(true);
		//removed
		userService.delete(ID);
		userService.updateAll();
		mapString = stringRedisTemplate.opsForValue().get("user:" + ID);
		assertThat(mapString).isNullOrEmpty();
		Thread.sleep(2000);
		log.info("延迟两秒再查询列表3，保证异步更新完毕....");
		results = userService.findAll();
		assertThat(results.size()).isEqualTo(1);
	}
	@Test
	public void testUsers() throws Exception {
		User user = new User(ID, "lin", "cheng");
		User user1 = new User(2L,"lu","xx");
		userService.save(user);
		userService.save(user1);
		String mapString = stringRedisTemplate.opsForValue().get("user:" + ID);
		assertThat(mapString).isEqualTo("[\"com.hualala.cache.domain.User\",{\"id\":1,\"firstName\":\"lin\",\"lastName\":\"cheng\",\"nickName\":\"aleenjava\",\"job\":\"softEngineer\",\"married\":true}]");
		Optional<User> optional = userService.findById(ID);
		assertThat(optional.isPresent()).isTrue();
		Thread.sleep(2000);
		log.info("延迟两秒再查询列表1，保证异步更新完毕....");
		Set<User> results = userService.findAll();
		assertThat(results.size()).isEqualTo(2);
		assertThat(results.stream().filter(u -> u.getFirstName().equals("lin")).findFirst().isPresent())
				.isEqualTo(true);
		//updated
		User changeUser = optional.get();
		changeUser.setFirstName("aleen");
		userService.update(changeUser);
		mapString = stringRedisTemplate.opsForValue().get("user:" + ID);
		assertThat(mapString).isEqualTo(
				"[\"com.hualala.cache.domain.User\",{\"id\":1,\"firstName\":\"aleen\",\"lastName\":\"cheng\",\"nickName\":\"aleenjava\",\"job\":\"softEngineer\",\"married\":true}]");
		optional = userService.findById(ID);
		assertThat(optional.isPresent()).isTrue();
		assertThat(optional.get().getFirstName()).isEqualTo("aleen");
		results = userService.findAll();
		Thread.sleep(2000);
		log.info("延迟两秒再查询列表2，保证异步更新完毕....");
		assertThat(results.size()).isEqualTo(2);
	/*	assertThat(results.stream().filter(u -> u.getFirstName().equals("aleen")).findFirst().isPresent())
				.isEqualTo(true);*/
		//removed
		userService.delete(ID);
		userService.updateAll();
		mapString = stringRedisTemplate.opsForValue().get("user:" + ID);
		assertThat(mapString).isNullOrEmpty();
		Thread.sleep(2000);
		log.info("延迟两秒再查询列表3，保证异步更新完毕....");
		results = userService.findAll();
		assertThat(results.size()).isEqualTo(1);
	}
	@Test
	public void testAddress() throws Exception {
		//created
		Address address = new Address(ID, "beijing", "xizhimen");
		addressService.save(address);
		String mapString = stringRedisTemplate.opsForValue().get("address:" + ID);
		assertThat(mapString).isEqualTo(
				"[\"com.hualala.cache.domain.Address\",{\"id\":1,\"province\":\"beijing\",\"city\":\"xizhimen\",\"zipcode\":\"000000\"}]");
		Optional<Address> optional = addressService.findById(ID);
		assertThat(optional.isPresent()).isTrue();
//		Assert.assertEquals();
//		Assert.assertTrue(condition);
		//updated actual data but not remove cache if condition failed.
		address = new Address(ID, "beijing", "xizhimen");
		addressService.conditionUpdate(address);
		mapString = stringRedisTemplate.opsForValue().get("address:" + ID);
		assertThat(mapString).isEqualTo(
				"[\"com.hualala.cache.domain.Address\",{\"id\":1,\"province\":\"beijing\",\"city\":\"xizhimen\",\"zipcode\":\"111111\"}]");

		//updated actual data and remove cache if condition successed.
		address = new Address(ID, "zhejiang", "jiaxing");
		addressService.conditionUpdate(address);
		mapString = stringRedisTemplate.opsForValue().get("address:" + ID);
		assertThat(mapString).isNullOrEmpty();

		//common updated
		Address changeAddress = optional.get();
		changeAddress.setZipcode("417118");
		addressService.update(changeAddress);
		optional = addressService.findByProvince("beijing");
		assertThat(optional.isPresent()).isTrue();
		assertThat(optional.get().getZipcode()).isEqualTo("417118");
		assertThat(addressService.findAll().size()).isEqualTo(1);

		//removed
		addressService.delete(changeAddress);
		mapString = stringRedisTemplate.opsForValue().get("address:" + ID);
		assertThat(mapString).isNullOrEmpty();
		assertThat(userService.findAll().size()).isEqualTo(0);
	}

}
