package net.stmdhr.rt.reduce;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import net.stmdhr.rt.Reducer;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexDictionaryReducer implements Reducer {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SimpleDictionaryReducer.class);

	private int startWordLength;

	private List<String> dictionary;
	private int dictionarySize;
	private BigInteger dictionarySizeBigInt;

	private final Random random = new Random(System.currentTimeMillis());

	public ComplexDictionaryReducer(int startWordLength) {

		try {
			this.startWordLength = startWordLength;

			String dictionaryFileName = "src/main/resources/1000MostCommon.txt";
			File dictionaryFile = new File(dictionaryFileName);
			this.dictionary = FileUtils.readLines(dictionaryFile, Charsets.UTF_8);
			
			this.dictionarySize = dictionary.size();
			this.dictionarySizeBigInt = BigInteger.valueOf(dictionarySize);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String reduce(byte[] hash, int position) {

		BigInteger hashInteger = new BigInteger(hash);
		hashInteger = hashInteger.add(BigInteger.valueOf(position));
		
		BigInteger index = hashInteger.mod(dictionarySizeBigInt);
		int indexInt = index.intValue();
		
		String word = dictionary.get(indexInt);
		word = doCharReplacements(word, hashInteger);
		word = doConcat(word, hashInteger);
		
		return word;
	}

	private String doConcat(String word, BigInteger hashInteger) {
		
		BigInteger num = BigInteger.valueOf(5);
		BigInteger rem = hashInteger.mod(num);
		int remInt = rem.intValue();
		
		switch (remInt) {
		
		case 0:
			word = word.concat("123");
			break;
			
		case 1:
			word = word.concat("!");
			break;
			
		default:
			break;
		}
		return word;
	}

	private String doCharReplacements(String word, BigInteger hashInteger) {
		
		BigInteger num = BigInteger.valueOf(11);
		BigInteger rem = hashInteger.mod(num);
		int remInt = rem.intValue();
		
		switch (remInt) {
		
		case 0:
			word = word.replaceAll("c", "(");
			break;
			
		case 1:
			word = word.replaceAll("i", "!");
			break;

		case 2:
			word = word.replaceAll("a", "&");
			break;
			
		case 3:
			word = word.replaceAll("h", "#");
			break;
			
		default:
			break;
		}
		
		return word;
	}

	@Override
	public String startWord() {

		while (true) {

			int rand = random.nextInt(dictionarySize);
			String word = dictionary.get(rand);
			LOGGER.debug("Generating start word. Trying {}", word);
			
			if (word.length() == startWordLength) {
				return word;
			}
		}
	}

}
