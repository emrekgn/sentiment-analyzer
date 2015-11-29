package tr.com.sentiment.analysis.classifiers;

import java.util.ArrayList;
import java.util.List;

import tr.com.sentiment.common.Constant;

public class ClassifierFactory {
	
	public static List<IClassifierModel> getModels(List<String> params) {
		
		List<IClassifierModel> classifiers = new ArrayList<IClassifierModel>();
		
		if (params.contains(Constant.CLASSIFIERS.NAIVE_BAYES)) {
			IClassifierModel c = new NaiveBayes();
			c.setParams(params);
			classifiers.add(c);
		}
		if (params.contains(Constant.CLASSIFIERS.NAIVE_BAYES_MULTINOMIAL)) {
			IClassifierModel c = new NaiveBayesMultinomial();
			c.setParams(params);
			classifiers.add(c);
		}
		if (params.contains(Constant.CLASSIFIERS.SVM)) {
			IClassifierModel c = new SVM();
			c.setParams(params);
			classifiers.add(c);
		}
		if (params.contains(Constant.CLASSIFIERS.LOGISTIC_REGRESSION)) {
			IClassifierModel c = new LogisticRegression();
			c.setParams(params);
			classifiers.add(c);
		}
		
		return classifiers;
	}

}
