package bce;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import bce.Person;

/**
 * Reads all the books from "./novels_mannually_cleaned" dir and writes
 * co-reference results in "./Raw.Index" dir
 */
public class BookCoReference {
	public static void main(String[] args) throws Exception {

		long startTime = System.nanoTime();
		File folder = new File("./novels_mannually_cleaned");
		File[] listOfFiles = folder.listFiles();
		System.out.println(listOfFiles.length + " files to analyze");

		for (int j = 0; j < listOfFiles.length; j++) {
			File file = listOfFiles[j];
			if (file.isFile() && file.getName().endsWith(".txt")) {
				String fileString = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
				System.out.println("Analyzing " + file.getName() + " ...");

				Annotation document = new Annotation(fileString);
				Properties props = new Properties();

				System.out.println("Annotationg document ...");
				props.setProperty("Annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");

				StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
				pipeline.annotate(document);

				List<Person> characters = new ArrayList<Person>();
				int tokenCounter = 1;
				String currNeToken = "";

				for (CoreLabel token : document.get(TokensAnnotation.class)) {
					currNeToken = token.get(NamedEntityTagAnnotation.class);
					if (currNeToken.equals("PERSON")) {
						Person p = new Person(tokenCounter, currNeToken, token.word());
						characters.add(p);
					}
					tokenCounter++;
				}

				for (int i = 0; i < characters.size() - 1; i++) {
					if (characters.get(i).getPosition()
							.intValue() == (characters.get(i + 1).getPosition().intValue() - 1)) {
						String fullName = characters.get(i).getToken() + " " + characters.get(i + 1).getToken();
						characters.get(i).setToken(fullName);
						characters.remove(characters.get(i + 1));
					}
				}

				FileWriter writer = new FileWriter("./Raw.Index/" + file.getName() + "-o.txt");
				for (Person p : characters) {
					writer.write(p.toString() + "\n");
				}
				writer.close();
				long endTime = System.nanoTime();
				System.out.println("time: " + (endTime - startTime) / 1000000000.0);
			}
		}
	}
}