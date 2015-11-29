package tr.com.sentiment.analysis.classifiers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import tr.com.sentiment.analysis.preprocess.Preprocess;
import tr.com.sentiment.common.Constant;
import tr.com.sentiment.common.PropertiesLoader;
import tr.com.sentiment.common.SentimentAnalysisUtil;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;

/**
 * Base class for all classifiers which is responsible for creating/storing instances 
 * and running specified classifier model with given parameters. 
 * 
 */
public abstract class AbstractClassifierModel implements IClassifierModel {
	
	private static Logger logger = SentimentAnalysisUtil.getLogger();

	private Instances trainData = null;
	private Instances testData = null;
	private Evaluation eval = null;
	private String timer = null;
	private List<String> params;
	
	public void setParams(String... params) {
		this.params = params != null ? Arrays.asList(params) : null;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}
	
	public boolean hasParam(String param) {
		return this.params != null && params.contains(param);
	}
	
	public void createTrainInstances(String dataArff) throws Exception {
		
		logger.log(Level.INFO, "Creating training instances...");
		
		DataSource source = new DataSource(dataArff);
		
		trainData = source.getDataSet();
		trainData.setClassIndex(trainData.numAttributes() - 1);
		
		if (hasParam(Constant.RUNTIME_PARAMS.USE_SAMPLING)) {
			
			// Try to read the same data set!
			Instances tmpData = null;
			File tmp = new File(Constant.FILES.DATA_TRAIN_OBJ);
			if (tmp.exists() && !tmp.isDirectory()) {
				logger.log(Level.INFO, "Reading previously saved dataset.");
				tmpData = (Instances) SerializationHelper.read(Constant.FILES.DATA_TRAIN_OBJ);
			}
			if (tmpData != null) {
				trainData = tmpData;
			}
			else {
				
				logger.log(Level.INFO, "Sampling instances.");
				
				Resample resample = new Resample();
				resample.setSampleSizePercent(PropertiesLoader.getInstance().getDouble(Constant.PROPS.SAMPLING_RATIO));
				resample.setInputFormat(trainData);
				
				trainData = Filter.useFilter(trainData, resample);
				trainData.setClassIndex(trainData.numAttributes() - 1);
				
				// Save the data for future use!
				SerializationHelper.write(Constant.FILES.DATA_TRAIN_OBJ, trainData);
			}
			
		}
		
		logger.log(Level.INFO, "Training instances created.");
		
	}
	
	public void createTestInstances(String dataArff) throws Exception {
		
		logger.log(Level.INFO, "Creating test instances...");
		
		DataSource source = new DataSource(dataArff);
		testData = source.getDataSet();
		testData.setClassIndex(testData.numAttributes() - 1);
		
		logger.log(Level.INFO, "Test instances created.");
	}

	public void crossValidateModel(int numFolds) throws Exception {
		
		logger.log(Level.INFO, "Cross validating model. This may take a while...");
		
		eval = new Evaluation(trainData);
		startTimer();
		eval.crossValidateModel(getClassifier(), trainData, numFolds, new Random(1));
		stopTimer();
		
		logger.log(Level.INFO, "Validation performed successfully!");
	}
	
	public void trainModel() throws Exception {
		
		logger.log(Level.INFO, "Training classifier. This may take a while...");
		
		eval = new Evaluation(trainData);
		startTimer();
		eval.evaluateModel(getClassifier(), testData);
		stopTimer();
		
		logger.log(Level.INFO, "Validation performed successfully!");
	}
	
	private void startTimer() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		timer = dateFormat.format(date);
	}
	
	private void stopTimer() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		timer += " - " + dateFormat.format(date);		
	}
	
	public void run() throws Exception {
		
		logger.log(Level.INFO, "Running classifier: " + this.getOutputHeader());
		
		Preprocess pp = new Preprocess();
		pp.setRemoveNonAlphabetic(true);
		pp.setHandleNegation(hasParam(Constant.RUNTIME_PARAMS.HANDLE_NEGATION));
		
		if (hasParam(Constant.RUNTIME_PARAMS.CROSS_VALIDATE)) {

			pp.execute(Constant.FILES.DATA_CSV_TRAIN, Constant.FILES.DATA_ARFF_TRAIN, 5, 0, false);
			System.gc();
			
			createTrainInstances(Constant.FILES.DATA_ARFF_TRAIN);
			crossValidateModel(PropertiesLoader.getInstance().getInt(Constant.PROPS.NUMBER_OF_FOLDS));
		}
		else if (hasParam(Constant.RUNTIME_PARAMS.TRAIN_AND_TEST)) {
			
			pp.execute(Constant.FILES.DATA_CSV_TRAIN, Constant.FILES.DATA_ARFF_TRAIN, 5, 0, false);
			pp.execute(Constant.FILES.DATA_CSV_TEST, Constant.FILES.DATA_ARFF_TEST, 5, 0, false);
			System.gc();
			
			createTrainInstances(Constant.FILES.DATA_ARFF_TRAIN);
			createTestInstances(Constant.FILES.DATA_ARFF_TEST);
			trainModel();
		}
		else {
			throw new RuntimeException("Missing parameters!");
		}
		
	}

	public void outputResults(String outputFile, boolean append) throws Exception {
		
		logger.log(Level.INFO, "Printing classification results.");
		
		File file = new File(outputFile);
		if (!file.exists()) {
			file.createNewFile();
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
		
		if (writer != null) {
			
			// Print header
			writer.write("########################################");
			writer.newLine();
			writer.write(getOutputHeader());
			writer.newLine();
			writer.write(timer);
			writer.newLine();
			
			// Print results
			writer.write(eval.toSummaryString());
			writer.newLine();

			writer.write(eval.toClassDetailsString());
			writer.newLine();
			
			// Print confusion matrix
			writer.write(eval.toMatrixString());
			writer.newLine();
			
			writer.newLine();
			writer.flush();
			
		}
		
		SentimentAnalysisUtil.close(writer);
	}

	public Instances getTrainData() {
		return trainData;
	}

	public Instances getTestData() {
		return testData;
	}

	public Evaluation getEval() {
		return eval;
	}

	public String getTimer() {
		return timer;
	}

	public List<String> getParams() {
		return params;
	}
	
}
