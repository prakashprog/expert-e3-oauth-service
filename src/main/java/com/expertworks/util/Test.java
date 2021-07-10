package com.expertworks.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {
	
	public static void main(String[] args) {
		String encoded=new BCryptPasswordEncoder().encode("passs");
		System.out.println(encoded);
	}

}
