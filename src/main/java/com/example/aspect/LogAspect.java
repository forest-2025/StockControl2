package com.example.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LogAspect {
	
	@Around("@within(org.springframework.stereotype.Controller)")
	public Object startLog(ProceedingJoinPoint jp) throws Throwable {
		
		// 開始ログ出力
		log.info("メソッド開始:" + jp.getSignature());
		
		try {
			// メソッド実行
			Object result = jp.proceed();
			
			// 終了ログ出力
			log.info("メソッド終了:" + jp.getSignature());
			
			// 実行結果を呼び出し元に返却
			return result;

		} catch (Exception ex) {
			// エラーログ出力
			log.error("メソッド異常終了:" + jp.getSignature());
			
			// エラーの再スロー
			throw ex;
		}
	}


}
