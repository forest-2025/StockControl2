package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ログアウトのコントローラクラス.
 * 
 * */
@Controller
public class LogoutController {

	/** 
	 * ログアウトした後にログアウトしたことを伝える画面に遷移するためのメソッド. 
	 * 
	 * @return ログアウト画面のビュー名.
	 */
	@GetMapping("/logout")
	public String getLogout() {
		
		return "/logout/logout";
	}
	
}
