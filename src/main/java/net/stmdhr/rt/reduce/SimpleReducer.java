package net.stmdhr.rt.reduce;

import java.math.BigInteger;
import java.util.Random;

import net.stmdhr.rt.Reducer;

public class SimpleReducer implements Reducer {

	private final String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private final char[] charsetArray = charset.toCharArray();
	private final int charsetLength = charset.length();
	private final BigInteger charsetLengthBigInt = BigInteger.valueOf(charsetLength);

	private final Random random = new Random(System.currentTimeMillis());

	private int startWordLength;

	public SimpleReducer(int wordLength) {
		this.startWordLength = wordLength;
	}

	@Override
	public String reduce(byte[] hash, int position) {
		try {
			
			BigInteger hashInteger = new BigInteger(hash);
			hashInteger = hashInteger.add(BigInteger.valueOf(position));
			
			char[] wordArray = new char[startWordLength];
			for (int i = 0; i < startWordLength; i++) {
				
				BigInteger index = hashInteger.mod(charsetLengthBigInt);
				int indexInt = index.intValue();
				wordArray[i] = charsetArray[indexInt];
				
				hashInteger = hashInteger.shiftRight(8);
			}
			
			return new String(wordArray);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String startWord() {
		char[] wordArray = new char[startWordLength];
		for (int i = 0; i < startWordLength; i++) {
			int charIndex = random.nextInt(charsetLength);
			wordArray[i] = charsetArray[charIndex];
		}

		return new String(wordArray);
	}

}
