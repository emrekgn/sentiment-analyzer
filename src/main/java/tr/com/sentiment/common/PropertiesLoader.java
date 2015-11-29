package tr.com.sentiment.common;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PropertiesLoader is responsible for loading and querying properties which defined in config.properties file.
 * When the instance is created for the first time, it will try to load the properties
 * 
 */
public class PropertiesLoader {

	private static Logger logger = SentimentAnalysisUtil.getLogger();

	private static PropertiesLoader instance = null;
	private static Properties prop = null;

	private PropertiesLoader() {
	}

	public static PropertiesLoader getInstance() {
		if (instance == null) {
			instance = new PropertiesLoader();
			loadProperties();
		}
		return instance;
	}

	private static void loadProperties() {

		logger.log(Level.INFO, "Trying to load config.properties");
		
		prop = new Properties();
		InputStream inp = null;

		try {
			prop.load(PropertiesLoader.class.getClassLoader().getResourceAsStream(Constant.FILES.PROPERTIES_FILE));
			logger.log(Level.INFO, "Properties loaded!");
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString());
		} finally {
			SentimentAnalysisUtil.close(inp);
		}

	}

	public String get(String key) {
		return prop != null && key != null ? prop.getProperty(key) : null;
	}

	public int getInt(String key) {
		return Integer.parseInt(get(key));
	}
	
	public double getDouble(String key) {
		return Double.parseDouble(get(key));
	}
	
	public long getLong(String key) {
		return Long.parseLong(get(key));
	}
	
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(get(key));
	}
	
	public String[] getStringArr(String key) {
		return get(key).split(",");
	}

	public List<String> getStringList(String key) {
		return Arrays.asList(get(key).split(","));
	}

}
