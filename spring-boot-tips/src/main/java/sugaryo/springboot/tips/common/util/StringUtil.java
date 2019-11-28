package sugaryo.springboot.tips.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;


/**
 * @author sugaryo
 * @see java.nio.charset.StandardCharsets
 * @see java.util.Arrays
 * @see java.util.Base64
 */
public class StringUtil {
	
	/**
	 * @param string 文字列
	 * @return {@code null} を 空文字列 {@code ""} に置き換えた値。
	 * 
	 * @see #nvl(String, String)
	 */
	public static String nvl( final String string ) {
		return null == string ? "" : string;
	}
	
	/**
	 * @param string 文字列
	 * @param alter  代替文字列
	 * @return {@code null} を 代替文字列 {@code alter} に置き換えた値。<br>
	 *         代替文字列も {@code null} の場合は 空文字列 {@code ""} を返します。
	 * 
	 * @see #nvl(String)
	 */
	public static String nvl( final String string, final String alter ) {
		return null == string ? nvl( alter ) : string;
	}
	
	
	
	/**
	 * @param string 文字列
	 * @return 文字列が {@code null} または 空文字列 {@code ""} の場合は {@code true} を、<br>
	 *         それ以外の場合は {@code false} を返します。
	 * 
	 * @see java.lang.String#isEmpty()
	 */
	public static boolean isNullOrEmpty( final String string ) {
		return null == string || string.isEmpty();
	}
	
	/**
	 * @param string 文字列
	 * @return {@code !isNullOrEmpty( string )}
	 * 
	 * @see #isNullOrEmpty(String)
	 * @see java.lang.String#isEmpty()
	 */
	public static boolean notNullOrEmpty( final String string ) {
		return !isNullOrEmpty( string );
	}
	
	
	
	/**
	 * @param string 文字列
	 * @return 指定した文字列をBASE64エンコードした値。
	 * 
	 * @see #nvl(String)
	 */
	public static String base64( final String string ) {
		return Base64.getEncoder().encodeToString( nvl( string ).getBytes( StandardCharsets.UTF_8 ) );
	}
	
	
	
	/**
	 * @param separator 区切り文字
	 * @param strings   文字列群
	 * @return 指定した文字列群を、指定した区切り文字で結合した値。
	 * 
	 * @see #join(String, String, String, Iterable)
	 */
	public static String join( final String separator, final String[] strings ) {
		return join( null, separator, null, Arrays.asList( strings ) );
	}
	
	/**
	 * @param separator 区切り文字
	 * @param strings   文字列群
	 * @return 指定した文字列群を、指定した区切り文字で結合した値。
	 * 
	 * @see #join(String, String, String, Iterable)
	 */
	public static String join( final String separator, final Iterable<String> strings ) {
		return join( null, separator, null, strings );
	}
	
	/**
	 * @param bracketBegin 括り文字（開始）
	 * @param separator    区切り文字
	 * @param bracketEnd   括り文字（終了）
	 * @param strings      文字列群
	 * @return 指定した文字列群を、指定した区切り文字で結合し、括り文字で括った文字列。
	 * 
	 * @see #join(String, String, String, Iterable)
	 */
	public static String join(
			final String bracketBegin,
			final String separator,
			final String bracketEnd,
			final String[] strings ) {
		return join( bracketBegin, separator, bracketEnd, Arrays.asList( strings ) );
	}
	
	/**
	 * @param bracketBegin 括り文字（開始）
	 * @param separator    区切り文字
	 * @param bracketEnd   括り文字（終了）
	 * @param strings      文字列群
	 * @return 指定した文字列群を、指定した区切り文字で結合し、括り文字で括った文字列。
	 */
	public static String join(
			final String bracketBegin,
			final String separator,
			final String bracketEnd,
			final Iterable<String> strings ) {
		StringBuilder sb = new StringBuilder();
		
		final String nseparator = nvl( separator );
		
		sb.append( nvl( bracketBegin ) );
		
		String add = "";
		for ( String string : strings ) {
			sb.append( add );
			sb.append( nvl( string ) );
			
			add = nseparator;
		}
		
		sb.append( nvl( bracketEnd ) );
		
		return sb.toString();
	}
	
}
