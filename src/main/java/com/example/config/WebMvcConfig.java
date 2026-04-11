package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC（画面制御）に関するカスタマイズ設定を行うクラス.
 * 
 * 外部フォルダ（ローカルファイルシステム）に保存された画像を,
 * ブラウザからURL経由で参照可能にするために定義する.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Value("${file.upload-dir}")
	private String uploadDir;

	/**
     * 静的リソース（画像・CSS・JSなど）の場所を定義するメソッド.
     * 
     * @param registry リソースの「URLパス」と「実際の場所」を登録するための台帳オブジェクト.
     */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/upload/**").addResourceLocations("file:" + uploadDir);
	}

}