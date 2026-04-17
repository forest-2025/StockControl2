package com.example.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
/**
 * 例外発生時にログを出力するアスペクト.
 */
public class ErrorAspect {

	@AfterThrowing(value = "execution(* com.example..*(..))", throwing = "ex")
	public void logAfterThrowing(Exception ex) {
		
		log.error("予期せぬエラーの発生: {}", ex.getClass().getSimpleName(), ex);
	}
}
