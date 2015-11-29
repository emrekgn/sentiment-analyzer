package tr.com.sentiment.common;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Utility class for the application that contains commonly used methods.
 *
 */
public class SentimentAnalysisUtil {

	public static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static Logger logger = null;

	public static Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger("sentiment.analyzer");
			FileHandler handler;
			try {
				handler = new FileHandler(Constant.FILES.LOG_FILE, true);
				if (handler != null) {
					logger.addHandler(handler);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return logger;
	}

}
