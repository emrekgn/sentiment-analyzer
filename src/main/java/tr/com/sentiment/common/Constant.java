package tr.com.sentiment.common;

/**
 * Constants of Sentiment Analyzer. Do not modify this file!<br/>
 * RUNTIME_PARAMS and CLASSIFIERS can be retrieved from the command line.
 * 
 */
public class Constant {

	public static class FILES {

		public static final String STOP_WORD_LIST_FILE = "stop-word-list";
		public static final String PROPERTIES_FILE = "config.properties";

		public static final String DATA_CSV_TEST = "raw-test-data.csv";
		public static final String DATA_CSV_TRAIN = "raw-train-data.csv";

		public static final String DATA_ARFF_TEST = "test-data.arff";
		public static final String DATA_ARFF_TRAIN = "train-data.arff";

		public static final String DATA_TRAIN_OBJ = "train-data.o";

		public static final String OUTPUT_FILE = "sentiment-analyzer.result";
		public static final String LOG_FILE = "sentiment-analyzer.log";

	}

	public static class RUNTIME_PARAMS {
		// Feature-related Parameters
		public static final String USE_TFIDF = "ti";
		public static final String USE_WORD_FREQ = "f";
		public static final String REM_STOP_WORDS = "r";
		public static final String CONV_LOWERCASE = "l";
		public static final String USE_BIGRAM = "b";
		public static final String USE_TRIGRAM = "t";
		public static final String USE_STEMMER = "s";
		public static final String HANDLE_NEGATION = "n";
		// Classification-related Parameters
		public static final String CROSS_VALIDATE = "cv";
		public static final String TRAIN_AND_TEST = "tt";
		public static final String USE_SAMPLING = "us";
		public static final String TEST_ALL = "ta";
	}

	public static class CLASSIFIERS {
		public static final String NAIVE_BAYES = "nb";
		public static final String NAIVE_BAYES_MULTINOMIAL = "nbm";
		public static final String SVM = "svm";
		public static final String LOGISTIC_REGRESSION = "lr";
	}

	public static class PROPS {
		public static final String NUMBER_OF_FOLDS = "numFolds";
		public static final String DEFAULT_PARAMETERS = "defaultParams";
		public static final String APPEND_FILE = "appendOutput";
		public static final String SAMPLING_RATIO = "samplingRatio";
	}

}
