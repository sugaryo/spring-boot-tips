package sugaryo.springboot.tips.common.constant;


/**
 * {@code REST API} を使用する際に HttpClient で使用する {@link org.springframework.http.HttpMethod HTTPメソッド} を定義します。
 * 
 * @author sugaryo
 */
public enum ApiMethod {
	
	GET,
	POST_JSON,
	POST_X_FORM_URLENCODED,
	PATCH,
	PUT,
	DELETE,
	
	BINARY_GET,
}
