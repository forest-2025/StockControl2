package com.example.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ErrorController {

	@GetMapping("/error/403")
	public String get403Error(HttpServletRequest request, Principal principal, Model model) {
		String username = (principal != null) ? principal.getName() : "anonymousUser";

		// 2. 元のURLを「型変換エラー」を回避して安全に取得
		// String.valueOfなら、もしnullでも "null" という文字列になり、キャスト失敗で落ちることがない
		String targetUrl = String.valueOf(request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI));

		model.addAttribute("error", 403);
		// ログ出力
		log.warn("アクセス拒否発生: ユーザー={}, アクセス試行先={}", username, targetUrl);

		// 表示するHTMLテンプレート名
		return "error";
	}

}
