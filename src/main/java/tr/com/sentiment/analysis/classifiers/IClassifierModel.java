package tr.com.sentiment.analysis.classifiers;

import java.util.List;

import weka.classifiers.Classifier;

public interface IClassifierModel {
	void setParams(String... params);

	void setParams(List<String> params);

	void createTrainInstances(String dataArff) throws Exception;

	void createTestInstances(String dataArff) throws Exception;

	void crossValidateModel(int int1) throws Exception;

	void trainModel() throws Exception;

	void run() throws Exception;

	void outputResults(String outputFile, boolean append) throws Exception;

	Classifier getClassifier() throws Exception;

	String getOutputHeader();
}
