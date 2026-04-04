package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

	@GetMapping("/error/403")
	public String get403Error(Model model) {

		model.addAttribute("error", 403);

		// 表示するHTMLテンプレート名
		return "error";
	}

}
