package sugaryo.springboot.tips.common.util;

import static sugaryo.springboot.tips.common.util.IRestClient.uri;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.HeadersBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import sugaryo.springboot.tips.common.constant.ApiMethod;
import sugaryo.springboot.tips.data.value.RestApi;



/**
 * REST API Client の標準実装.
 * 
 * @author sugaryo
 * @see org.springframework.web.client.RestTemplate
 * @see sugaryo.springboot.tips.common.util.IRestClient
 * @see sugaryo.springboot.tips.data.value.RestApi
 */
public class RestClient implements IRestClient {
	
	private static final Logger log = LoggerFactory.getLogger( RestClient.class );
	
	private static class QuietlyHandler extends DefaultResponseErrorHandler {
		
		/**
		 * @inherit
		 */
		@Override
		public void handleError( ClientHttpResponse response ) throws IOException {
			// ■何もしない■
			// デフォルト実装では HTTPステータスの 300/400系 で例外スローしているが、
			// NOPでオーバーライドすることで 300/400系 でも正常ルートに乗るようにする。
			// ・300系：HttpClientErrorException
			// ・400系：HttpServerErrorException
		}
	}
	
	/** HTTP Client */
	protected final RestTemplate rest;
	
	public RestClient() {
		this( 60_000 ); // デフォルト60秒設定
	}
	
	/**
	 * @param timeout タイムアウト値（デフォルト60秒）
	 */
	public RestClient( int timeout ) {
		this.rest = new RestTemplate();
		
		// 3xx/4xx で例外を飛ばさないように、独自のエラーハンドラを差し込む。
		this.rest.setErrorHandler( new QuietlyHandler() );
		
		// PATCHでリクエストするためにリクエストファクトリーを設定。
		// ★何故かSpring標準の RestTemplate.patch メソッドをしてもPATCHで通信が出来ないので注意★
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout( timeout );
		requestFactory.setReadTimeout( timeout );
		this.rest.setRequestFactory( requestFactory );
	}
	
	/** @inherit */
	@Override
	public RestApi exchange(
			String url,
			ApiMethod method,
			Map<String, String> headers,
			String data ) {
		
		switch ( method ) {
			
			case DELETE:
				return this.delete( url, headers );
			
			case GET:
				return this.get( url, headers );
			
			case POST_JSON:
				return this.post( url, headers, MediaType.APPLICATION_JSON, data );
			
			case POST_X_FORM_URLENCODED:
				return this.post( url, headers, MediaType.APPLICATION_FORM_URLENCODED, data );
			
			case PATCH:
				return this.patch( url, headers, MediaType.APPLICATION_JSON, data );
			
			case PUT:
				return this.put( url, headers, MediaType.APPLICATION_JSON, data );
			
			
			case BINARY_GET:
				return this.getBinary( url, headers );
			
			default:
				break;
		}
		
		return null;
	}
	
	
	/**
	 * HTTP DELETE 実装.
	 * 
	 * @param url     コールするAPIのURL
	 * @param headers HTTPヘッダ情報
	 * @return DELETEメソッドでのAPIコール結果
	 * 
	 * @see org.springframework.http.RequestEntity#delete(URI)
	 */
	protected RestApi delete( String url, Map<String, String> headers ) {
		
		RestApi status;
		
		RequestEntity<Void> request = buildDeleteRequest( url, headers );
		try {
			ResponseEntity<String> response = rest.exchange( request, String.class );
			
			status = new RestApi(
					RestApi.Converter.Request.fromVoid( request ),
					RestApi.Converter.Response.fromEntity( response ) );
		}
		// 不明なステータスコード例外の場合：
		catch ( UnknownHttpStatusCodeException ex ) {
			
			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromVoid( request ),
					RestApi.Converter.Response.fromClientError( ex ) );
		}
		// 予期せぬ例外：
		catch ( Exception ex ) {

			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromVoid( request ),
					RestApi.Converter.Response.fromException( ex ) );
		}
		
		return status;
	}
	
	private RequestEntity<Void> buildDeleteRequest(
			String url,
			Map<String, String> headers ) {
		
		HeadersBuilder<?> builder = RequestEntity.delete( uri( url ) );
		
		if ( null != headers ) {
			for ( String name : headers.keySet() ) {
				String header = headers.get( name );
				builder.header( name, header );
			}
		}
		
		return builder.build();
	}
	
	
	/**
	 * HTTP GET 実装.
	 * 
	 * @param url     コールするAPIのURL
	 * @param headers HTTPヘッダ情報
	 * @return GETメソッドでのAPIコール結果
	 * 
	 * @see org.springframework.http.RequestEntity#get(URI)
	 */
	protected RestApi get( String url, Map<String, String> headers ) {
		
		RestApi status;
		
		RequestEntity<Void> request = buildGetRequest( url, headers );
		try {
			ResponseEntity<String> response = rest.exchange( url, HttpMethod.GET, request, String.class );
			
			status = new RestApi(
					RestApi.Converter.Request.fromVoid( request ),
					RestApi.Converter.Response.fromEntity( response ) );
		}
		// 不明なステータスコード例外の場合：
		catch ( UnknownHttpStatusCodeException ex ) {

			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromVoid( request ),
					RestApi.Converter.Response.fromClientError( ex ) );
		}
		// 予期せぬ例外：
		catch ( Exception ex ) {

			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromVoid( request ),
					RestApi.Converter.Response.fromException( ex ) );
		}
		
		return status;
	}
	
	protected RestApi getBinary( String url, Map<String, String> headers ) {
		
		RestApi status;
		
		RequestEntity<Void> request = buildGetRequest( url, headers );
		try {
			ResponseEntity<byte[]> response = rest.exchange( url, HttpMethod.GET, request, byte[].class );
			
			status = new RestApi(
					RestApi.Converter.Request.fromVoid( request ),
					RestApi.Converter.Response.fromBinary( response ) );
		}
		// 不明なステータスコード例外の場合：
		catch ( UnknownHttpStatusCodeException ex ) {

			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromVoid( request ),
					RestApi.Converter.Response.fromClientError( ex ) );
		}
		// 予期せぬ例外：
		catch ( Exception ex ) {

			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromVoid( request ),
					RestApi.Converter.Response.fromException( ex ) );
		}
		
		return status;
	}
	
	private RequestEntity<Void> buildGetRequest(
			String url,
			Map<String, String> headers ) {
		
		HeadersBuilder<?> builder = RequestEntity.get( uri( url ) );
		
		if ( null != headers ) {
			for ( String name : headers.keySet() ) {
				String header = headers.get( name );
				builder.header( name, header );
			}
		}
		
		return builder.build();
	}
	
	
	/**
	 * HTTP POST 実装.
	 * 
	 * @param url     コールするAPIのURL
	 * @param headers HTTPヘッダ情報
	 * @param type    メディアタイプ
	 * @param data    送信するデータ
	 * @return POSTメソッドでのAPIコール結果
	 * 
	 * @see org.springframework.http.RequestEntity#post(URI)
	 */
	protected RestApi post( String url, Map<String, String> headers, MediaType type, String data ) {
		
		RestApi status;
		
		
		RequestEntity<String> request = buildPostRequest( url, type, data, headers );
		try {
			ResponseEntity<String> response = rest.exchange( request, String.class );
			
			status = new RestApi(
					RestApi.Converter.Request.fromString( request ),
					RestApi.Converter.Response.fromEntity( response ) );
		}
		// 不明なステータスコード例外の場合：
		catch ( UnknownHttpStatusCodeException ex ) {

			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromString( request ),
					RestApi.Converter.Response.fromClientError( ex ) );
		}
		// 予期せぬ例外：
		catch ( Exception ex ) {

			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromString( request ),
					RestApi.Converter.Response.fromException( ex ) );
		}
		
		return status;
	}
	
	private RequestEntity<String> buildPostRequest(
			String url,
			MediaType type,
			String data,
			Map<String, String> headers ) {
		
		RequestEntity.BodyBuilder builder = RequestEntity.post( uri( url ) );
		
		if ( null != headers ) {
			for ( String name : headers.keySet() ) {
				String header = headers.get( name );
				builder.header( name, header );
			}
		}
		
		RequestEntity<String> request = builder
				.contentType( type )
				.body( data );
		return request;
	}
	
	/**
	 * HTTP PATCH 実装.
	 * 
	 * @param url     コールするAPIのURL
	 * @param headers HTTPヘッダ情報
	 * @param type    メディアタイプ
	 * @param data    送信するデータ
	 * @return PATCHメソッドでのAPIコール結果
	 */
	protected RestApi patch( String url, Map<String, String> headers, MediaType type, String data ) {
		
		RestApi status;
		
		
		HttpEntity<String> entity = buildHttpPatchRequest( data, type, headers );
		try {
			ResponseEntity<String> response = this.rest.exchange( url, HttpMethod.PATCH, entity, String.class );
			
			status = new RestApi(
					RestApi.Converter.Request.fromPatchEntity( url, entity ),
					RestApi.Converter.Response.fromEntity( response ) );
		}
		// 不明なステータスコード例外の場合：
		catch ( UnknownHttpStatusCodeException ex ) {

			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromPatchEntity( url, entity ),
					RestApi.Converter.Response.fromClientError( ex ) );
		}
		// 予期せぬ例外：
		catch ( Exception ex ) {

			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromPatchEntity( url, entity ),
					RestApi.Converter.Response.fromException( ex ) );
		}
		
		return status;
	}
	
	// org.springframework.http.RequestEntity.patch(URI) だとうまくPATCHが動かなかったので、PATCH処理は独自実装。
	private HttpEntity<String> buildHttpPatchRequest(
			String data,
			MediaType type,
			Map<String, String> headers ) {
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( type );
		
		if ( null != headers ) {
			for ( String name : headers.keySet() ) {
				String header = headers.get( name );
				httpHeaders.set( name, header );
			}
		}
		
		HttpEntity<String> httpEntity = new HttpEntity<String>( data, httpHeaders );
		return httpEntity;
	}
	
	
	protected RestApi put( String url, Map<String, String> headers, MediaType type, String data ) {
		
		RestApi status;
		
		
		RequestEntity<String> request = buildPutRequest( url, type, data, headers );
		try {
			ResponseEntity<String> response = rest.exchange( request, String.class );
			
			status = new RestApi(
					RestApi.Converter.Request.fromString( request ),
					RestApi.Converter.Response.fromEntity( response ) );
		}
		// 不明なステータスコード例外の場合：
		catch ( UnknownHttpStatusCodeException ex ) {

			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromString( request ),
					RestApi.Converter.Response.fromClientError( ex ) );
		}
		// 予期せぬ例外：
		catch ( Exception ex ) {

			//TODO：例外処理のポリシーをプロパティ化検討。
			
			log.error( ex.getMessage(), ex );
			status = new RestApi(
					RestApi.Converter.Request.fromString( request ),
					RestApi.Converter.Response.fromException( ex ) );
		}
		
		return status;
	}
	
	private RequestEntity<String> buildPutRequest(
			String url,
			MediaType type,
			String data,
			Map<String, String> headers ) {
		
		RequestEntity.BodyBuilder builder = RequestEntity.put( uri( url ) );
		
		if ( null != headers ) {
			for ( String name : headers.keySet() ) {
				String header = headers.get( name );
				builder.header( name, header );
			}
		}
		
		RequestEntity<String> request = builder
				.contentType( type )
				.body( data );
		return request;
	}
}
