//package com.example.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Controller
//@Slf4j
//public class ErrorController {
//
//	@GetMapping("/error/403")
//	public String get403Error(Model model,Exception ex) {
//
//		model.addAttribute("error", 403);
//		log.warn("例外発生: {}", ex.getClass().getSimpleName(), ex);
//
//		return "error";
//	}
//}
