package com.example.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * オブジェクト同士のデータ変換を簡単に行う ModelMapper を Bean として定義するクラス.
 *
 */
@Configuration
public class MapperConfig {
	
	/**
	 * オブジェクト同士のデータ変換を簡単に行う ModelMapper を Bean として定義する.
	 *
	 * @return データ変換用の ModelMapper オブジェクト.
	 */
	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
