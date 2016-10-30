package com.hualala.cache.domain;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	public User(Long id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	private Long id;
	private String firstName;
	private String lastName;
	private String nickName = "aleenjava";
	private String job = "softEngineer";
	private Boolean married = true;

}
