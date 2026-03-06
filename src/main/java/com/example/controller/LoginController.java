package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ログインのコントローラクラス.
 * 
 * */
@Controller
public class LoginController {

	/**
	 * ログイン画面に遷移する.
	 * 
	 * @return ログイン画面のビュー名.
	 */
	@GetMapping("/login")
	public String getLogin() {
		
		return "login/login";
	}
}
