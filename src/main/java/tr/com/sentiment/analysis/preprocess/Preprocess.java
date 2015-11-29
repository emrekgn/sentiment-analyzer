package tr.com.sentiment.analysis.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import com.opencsv.CSVParser;

import tr.com.sentiment.common.Constant;
import tr.com.sentiment.common.SentimentAnalysisUtil;

public class Preprocess {
	
	private static HashSet<String> stopWordsSet = null;
	private boolean removeNonAlphabetic = false;
	private boolean removeStopWords = false;
	private boolean convertLowerCase = false;
	private boolean handleNegation = false;

	public Process execute(String inputFile, String outputFile, int reviewIndice, int sentimentIndice, boolean skipHeader) {

		BufferedReader reader = null;
		BufferedWriter writer = null;
		Process resultObj = null;

		try {

			reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(inputFile), "UTF-8"));
			
			File file = new File(outputFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			writer = new BufferedWriter(new FileWriter(file, false));

			if (reader != null && writer != null) {
				
				// Write ARFF annotations
				// For ARFF syntax, please refer to http://weka.wikispaces.com/ARFF
				writer.write("@relation sentiment-analysis-machine-learning");
				writer.newLine();

				writer.newLine();
				writer.write("@attribute Review String");
				writer.newLine();
				writer.write("@attribute Sentiment {negative,positive}");
				writer.newLine();

				writer.newLine();
				writer.write("@data");
				writer.newLine();
				writer.flush();
				
				CSVParser parser = new CSVParser();
				String line = null;
				
				if (skipHeader) line = reader.readLine(); // Skip header
				while((line = reader.readLine()) != null && line.length() > 0) {

					String[] columns = parser.parseLine(line);
					
					String sentiment = columns[sentimentIndice];
					if ("2".equals(sentiment)) continue;
					if ("0".equals(sentiment)) {
						sentiment = "negative";
					} else {
						sentiment = "positive";
					}
					
					String review = columns[reviewIndice];
					
					if (removeNonAlphabetic) {
						review = removeNonAlphabetic(review);
					}
					if (convertLowerCase) {
						review = convertLowerCase(review);
					}
					if (removeStopWords) {
						String[] words = tokenize(review);
						words = removeStopWords(words);
						review = array2String(words);
					}
					if (handleNegation) {
						String[] words = tokenize(review);
						words = handleNegation(words);
						review = array2String(words);
					}

					writer.write("'" + review + "'," + sentiment);
					writer.flush();
					writer.newLine();
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SentimentAnalysisUtil.close(reader);
			SentimentAnalysisUtil.close(writer);
		}

		return resultObj;
	}

	private String removeNonAlphabetic(String review) {
		return review.replaceAll("[^a-zA-Z ]", "");
	}

	private String convertLowerCase(String review) {
		return review.toLowerCase(Locale.ENGLISH);
	}
	
	public String[] tokenize(String review) {
		return review.split("\\s+");
	}
	
	private String[] removeStopWords(String[] words) {
		ArrayList<String> list = new ArrayList<String>();
		for (String word : words) {
			if (!isEmpty(word) && !stopWordsSet.contains(word)) {
				list.add(word);
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	private String[] handleNegation(String[] words) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (!isEmpty(word)) {
				if ("not".equalsIgnoreCase(word) && i+1 < words.length) {
					word = words[++i] + "_not";
					System.out.println("WORD: " + word);
				}
				list.add(word);
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	private static void generateStopWordSet() {
		if(stopWordsSet == null || stopWordsSet.size() == 0){
			stopWordsSet = new HashSet<String>();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(
						Constant.FILES.STOP_WORD_LIST_FILE));
				if (reader != null) {
					for (String line; (line = reader.readLine()) != null;) {
						Collections.addAll(stopWordsSet, line.split(","));
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				SentimentAnalysisUtil.close(reader);
			}			
		}
	}
	
	public boolean isEmpty(String s) {
		return s == null || "".equals(s) || "null".equals(s);
	}

	public String array2String(String[] arr) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			builder.append(arr[i]);
			if (i < arr.length - 1) {
				builder.append(" ");
			}
		}
		return builder.toString();
	}

	public boolean isRemoveNonAlphabetic() {
		return removeNonAlphabetic;
	}

	public void setRemoveNonAlphabetic(boolean removeNonAlphabetic) {
		this.removeNonAlphabetic = removeNonAlphabetic;
	}

	public boolean isRemoveStopWords() {
		return removeStopWords;
	}

	public void setRemoveStopWords(boolean removeStopWords) {
		this.removeStopWords = removeStopWords;
		generateStopWordSet();
	}

	public boolean isConvertLowerCase() {
		return convertLowerCase;
	}

	public void setConvertLowerCase(boolean convertLowerCase) {
		this.convertLowerCase = convertLowerCase;
	}
	
	public boolean isHandleNegation() {
		return handleNegation;
	}

	public void setHandleNegation(boolean handleNegation) {
		this.handleNegation = handleNegation;
	}

}
