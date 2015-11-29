package tr.com.sentiment.analysis.classifiers;

import org.apache.commons.lang3.StringUtils;

import tr.com.sentiment.common.Constant;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.stemmers.SnowballStemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class NaiveBayesMultinomial extends AbstractClassifierModel {

	public Classifier getClassifier() throws Exception {
		
		StringToWordVector stwv = new StringToWordVector();
		stwv.setTFTransform(hasParam(Constant.RUNTIME_PARAMS.USE_TFIDF));
		stwv.setIDFTransform(hasParam(Constant.RUNTIME_PARAMS.USE_TFIDF));
		stwv.setLowerCaseTokens(hasParam(Constant.RUNTIME_PARAMS.CONV_LOWERCASE));
		stwv.setUseStoplist(hasParam(Constant.RUNTIME_PARAMS.REM_STOP_WORDS));
		stwv.setOutputWordCounts(hasParam(Constant.RUNTIME_PARAMS.USE_WORD_FREQ));
		if (hasParam(Constant.RUNTIME_PARAMS.TRAIN_AND_TEST)) stwv.setInputFormat(getTrainData());
		if (hasParam(Constant.RUNTIME_PARAMS.USE_BIGRAM)) {
			NGramTokenizer tokenizer = new NGramTokenizer();
			tokenizer.setNGramMinSize(2);
			stwv.setTokenizer(tokenizer);
		}
		else if (hasParam(Constant.RUNTIME_PARAMS.USE_TRIGRAM)) {
			NGramTokenizer tokenizer = new NGramTokenizer();
			tokenizer.setNGramMinSize(3);
			stwv.setTokenizer(tokenizer);			
		}
		if (hasParam(Constant.RUNTIME_PARAMS.USE_STEMMER)) {
			SnowballStemmer stemmer = new SnowballStemmer("porter");
			stwv.setStemmer(stemmer);
		}
		
		weka.classifiers.bayes.NaiveBayesMultinomial naiveBayes = new weka.classifiers.bayes.NaiveBayesMultinomial();
		
		FilteredClassifier cls = new FilteredClassifier();
		cls.setClassifier(naiveBayes);
		cls.setFilter(stwv);
		if (hasParam(Constant.RUNTIME_PARAMS.TRAIN_AND_TEST)) cls.buildClassifier(getTrainData());
		
		return cls;
	}
	
	@SuppressWarnings("unchecked")
	public String getOutputHeader() {
		return "Multinomial Naive Bayes with parameters: " + StringUtils.join(getParams());
	}
	
}
