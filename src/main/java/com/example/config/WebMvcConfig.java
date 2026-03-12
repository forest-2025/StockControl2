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

/* 商品画像は修正が可能な動的な画像のため,staticではなくサーバーのローカルファイルシステムに保存する.
 * WebMvcConfigurerインターフェースを実装することで通常ならstaticをみにいくだけのところを,サーバーのローカルファイルも参照してくれるように追加できるようになる.
 * WebMvcConfigurer インターフェースはほぼすべてのメソッドがdefaultメソッドで本来は中身が空の状態だが,
 * 設定したいメソッドのみを@Overrideして実装することでその部分だけをカスタマイズして設定できる(すべてのメソッドを@Overrideしなくていい).
 * 
 * addResourceHandlers は Spring MVC に自動で探してもらえるためリソース（画像やCSSなど）の置き場所を追加する設定ができるメソッドで,
 * 実際にリソースを探したいときのリクエストを受け付けられるURLの登録やリソースの置き場所の登録ができる.
 * 
 * ResourceHandlerRegistry の addResourceHandler でリソースにアクセスできるURLの設定を持つ ResourceHandlerRegistration オブジェクトが生成される.
 * そのオブジェクトに,そのURLにアクセスがあったときに探しに行くファイル置き場の設定を addResourceLocations で追加する.
 * これにより受付できるURLとファイル置き場が紐づいたResourceHandlerRegistration オブジェクトが完成し,
 * それをResourceHandlerRegistryオブジェクトが保持することで独自のリソース置き場にアクセスできるようになる.
 * 
 * "/image/**" は http://localhost:8080/image/~ というURLのアクセスを受け付けるという意味になる(image以下すべての階層を指し,*なら直下のファイルだけ).
 * "file:" はアプリ(jarファイル(読み取り専用,書き込めないため動的な画像の保存はできない))の外側,外部フォルダ(ローカルのファイルシステム)であることを表すプレフィックス.
 */
