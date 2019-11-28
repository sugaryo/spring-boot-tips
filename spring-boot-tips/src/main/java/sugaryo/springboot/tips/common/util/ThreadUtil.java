package sugaryo.springboot.tips.common.util;

public class ThreadUtil {
	public static void sleep(long ms) {
		try {
			Thread.sleep( ms );
		}
		catch (InterruptedException ex) {
			throw new RuntimeException( ex );
		}
	}
}
