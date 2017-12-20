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

public class SimpleDictionaryReducer implements Reducer {

	private static Logger LOGGER = LoggerFactory.getLogger(SimpleDictionaryReducer.class);

	private int startWordLength;

	private List<String> dictionary;
	private int dictionarySize;
	private BigInteger dictionarySizeBigInt;

	private final Random random = new Random(System.currentTimeMillis());

	public SimpleDictionaryReducer(int startWordLength) {

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

		return dictionary.get(indexInt);
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