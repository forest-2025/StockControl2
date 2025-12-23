package com.example.component;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class FullNameUser extends User{
	
	private final String familyName;
	private final String firstName;

	public FullNameUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
			String familyName, String firstName) {
		super(username, password, authorities);
		this.familyName = familyName;
		this.firstName = firstName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public String getFirstName() {
		return firstName;
	}


}
