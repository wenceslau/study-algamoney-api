package com.exemplo.algamoney.api.security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeradorSenha {

	public static void main(String[] args) {
		BCryptPasswordEncoder econder = new BCryptPasswordEncoder();
		System.out.println(econder.encode("admin"));
	}
	
}
