package tr.com.sentiment.analysis.run;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import tr.com.sentiment.analysis.classifiers.ClassifierFactory;
import tr.com.sentiment.analysis.classifiers.IClassifierModel;
import tr.com.sentiment.common.Constant;
import tr.com.sentiment.common.PropertiesLoader;
import tr.com.sentiment.common.SentimentAnalysisUtil;

/**
 * This class is the starting point of the application.
 * Possible parameters that can be used are:<br/><br/>
 * 
 * <b>Feature Related Parameters:</b><br/>
 * <b>ti:</b> use tf-idf,<br/>
 * <b>f:</b> use word frequency,<br/>
 * <b>r:</b> remove stop words,<br/>
 * <b>l:</b> convert lowercase,<br/>
 * <b>b:</b> use bigram,<br/>
 * <b>t:</b> use trigram,<br/>
 * <b>s:</b> use stemmer,<br/>
 * <b>n:</b> handle negation,<br/><br/>
 * <b>Classification Related Parameters:</b><br/>
 * <b>cv:</b> cross validate,<br/>
 * <b>tt:</b> train and test,<br/>
 * <b>us:</b> use sampling,<br/>
 * <b>ta:</b> test all<br/>
 *
 */
public class Run {

	private static Logger logger = SentimentAnalysisUtil.getLogger();

	public static void main(String[] args) {

		try {

			if (args != null && args.length > 0) {

				List<String> params = Arrays.asList(args);

				List<IClassifierModel> classifiers = ClassifierFactory.getModels(params);
				runClassifier(classifiers);

			}
			// If no parameters are given, run with the default classifiers!
			else {

				logger.log(Level.INFO, "No parametes has been specified! Continuing with default parameters...");

				List<String> defaultParams = PropertiesLoader.getInstance()
						.getStringList(Constant.PROPS.DEFAULT_PARAMETERS);
				if (defaultParams == null) {
					logger.log(Level.SEVERE, "Default parameters cannot be found! Exiting Sentiment Analyzer...");
					System.exit(-1);
				}

				List<IClassifierModel> classifiers = ClassifierFactory.getModels(defaultParams);
				runClassifier(classifiers);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void runClassifier(List<IClassifierModel> classifiers) throws Exception {

		if (classifiers == null) {
			logger.log(Level.SEVERE, "No classifier has been specified! Exiting Sentiment Analyzer...");
			System.exit(-1);
		}

		boolean appendFile = PropertiesLoader.getInstance().getBoolean(Constant.PROPS.APPEND_FILE);
		for (IClassifierModel classifier : classifiers) {
			classifier.run();
			classifier.outputResults(Constant.FILES.OUTPUT_FILE, appendFile);
		}

	}

}
