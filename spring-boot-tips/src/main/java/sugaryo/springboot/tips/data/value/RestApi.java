package sugaryo.springboot.tips.data.value;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import sugaryo.springboot.tips.common.util.JsonMapper;


/**
 * REST API の HTTP要求/応答 セット.
 *
 * @author sugaryo
 * 
 * @see RestApi.RestRequest
 * @see RestApi.RestResponse
 * @see RestApi.Converter
 */
public class RestApi {

	/**
	 * HTTP要求
	 *
	 * @author sugaryo
	 */
	public static class RestRequest {
		/** エンドポイントURL */
		public final String url;
		/** 要求の {@link org.springframework.http.HttpMethod HTTPメソッド} */
		public final String method;
		/** APIへのHTTP要求ヘッダ */
		public final String headers;
		/** APIへのHTTP送信データ */
		public final String body;
		
		public RestRequest( String url, String method, String headers, String body ) {
			this.url = url;
			this.method = method;
			this.headers = headers;
			this.body = body;
		}
	}
	
	/**
	 * HTTP応答
	 *
	 * @author sugaryo
	 * @see org.springframework.http.HttpStatus
	 */
	public static class RestResponse {
		/** {@link org.springframework.http.HttpStatus}ステータスコード */
		public final int status;
		/** {@link org.springframework.http.HttpStatus}ステータス名称 */
		public final String text;
		/** APIのHTTP応答ヘッダ */
		public final String headers;
		/** APIのHTTP応答データ */
		public final String body;

		public RestResponse( int status, String text, String headers, String body ) {
			this.status = status;
			this.text = text;
			this.headers = headers;
			this.body = body;
		}
	}
	
	/**
	 * HTTP応答（バイナリデータ用拡張）
	 * 
	 * @author sugaryo
	 */
	public static class BinaryResponse extends RestResponse {
		/** レスポンスのバイナリデータ */
		public final byte[] bin;
		
		public BinaryResponse( int status, String text, String headers, byte[] bin ) {
			super( status, text, headers, "binary-data[" + bin.length + "]" );
			this.bin = bin;
		}
	}
	
	
	/** HTTP要求 */
	public final RestRequest request;
	/** HTTP応答 */
	public final RestResponse response;
	
	/**
	 * コンストラクタ.
	 * 
	 * @param request  {@link #request}
	 * @param response {@link #response}
	 */
	public RestApi(
			RestRequest request, 
			RestResponse response ) {
		
		this.request = request;
		this.response = response;
	}
	
	
	/**
	 * {@link RestApi} 用の HTTP要求/応答 コンバータ.
	 * 
	 * @author sugaryo
	 * @see org.springframework.http.RequestEntity
	 * @see org.springframework.http.ResponseEntity
	 */
	public static class Converter {

		/**
		 * {@code SpringBoot系} の {@link RequestEntity} を {@link RestApi.RestRequest} に変換します。
		 */
		public static class Request {
			
			public static final RestRequest fromVoid(RequestEntity<Void> entity) {
				String url = entity.getUrl().getPath();
				String method = entity.getMethod().name();
				String headers = JsonMapper.stringify( entity.getHeaders() );
				String body = null;
				
				return new RestRequest( url, method, headers, body );
			}
			
			public static final RestRequest fromString(RequestEntity<String> entity) {
				String url = entity.getUrl().getPath();
				String method = entity.getMethod().name();
				String headers = JsonMapper.stringify( entity.getHeaders() );
				String body = entity.getBody();
				
				return new RestRequest( url, method, headers, body );
			}
			
			public static RestRequest fromPatchEntity(String url, HttpEntity<String> entity) {
				String method = HttpMethod.PATCH.name();
				String headers = JsonMapper.stringify( entity.getHeaders() );
				String body = entity.getBody();
				
				return new RestRequest( url, method, headers, body );
			}
		}
		
		/**
		 * {@code SpringBoot系} の {@link ResponseEntity} を {@link RestApi.RestResponse} に変換します。
		 */
		public static class Response {
			
			public static final RestResponse fromEntity(ResponseEntity<String> entity) {
				
				int status = entity.getStatusCode().value();
				String text = entity.getStatusCode().name();
				String body = entity.getBody();
				String headers = JsonMapper.stringify( entity.getHeaders() );
				
				return new RestResponse( status, text, headers, body );
			}

			public static final RestResponse fromBinary( ResponseEntity<byte[]> entity ) {

				int status = entity.getStatusCode().value();
				String text = entity.getStatusCode().name();
				byte[] bin = entity.getBody();
				String headers = JsonMapper.stringify( entity.getHeaders() );
				
				return new BinaryResponse( status, text, headers, bin );
			}
			
			public static final RestResponse fromClientError(RestClientResponseException ex) {
				// RestTemplateのDefaultErrorHandler では、
				// HTTPの 2xx系 以外は例外として飛ばして来るので、
				// HTTP系エラーの場合はトラップして正常ルートで返す。
				// HTTP系のエラーは以下の３パターン。
				// ■4xx - RestClientResponseException -> HttpStatusCodeException -> HttpClientErrorException
				// ■5xx - RestClientResponseException -> HttpStatusCodeException -> HttpServerErrorException
				// ■xxx - RestClientResponseException -> UnknownHttpStatusCodeException
				// ※ただし、
				// 現在の RestClient 共通部品ではデフォルトエラーハンドラを使用せず、
				// 正常ルートで処理しているため 4xx/5xx 系は例外にはならない。
				int status = ex.getRawStatusCode();
				String text = ex.getStatusText();
				String body = ex.getMessage();
				String headers = JsonMapper.stringify( ex.getResponseHeaders() );
				
				return new RestResponse( status, text, headers, body );
			}
			
			public static final RestResponse fromException(Exception ex) {
				int status = -1;
				String text = ex.getMessage();
				String body = "";
				String headers = "";
				
				return new RestResponse( status, text, headers, body );
			}
		}
	
	}
}
