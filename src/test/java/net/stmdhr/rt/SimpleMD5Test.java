package net.stmdhr.rt;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;
import net.stmdhr.rt.reduce.SimpleReducer;
import net.stmdhr.rt.search.TableSearcher;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.junit.Test;

public class SimpleMD5Test extends TestCase {

	@Test
	public void test100Hashes() throws IOException, NoSuchAlgorithmException {

		int numOfThreads = 4;
		String tableFileName = "bin/output/simple-5-table.txt";
		TableSearcher searcher = new TableSearcher(tableFileName, numOfThreads);

		int wordLength = 5;
		SimpleReducer reducer = new SimpleReducer(wordLength);

		Hasher hasher = new Hasher(MessageDigestAlgorithms.MD5);

		int count = 0;
		for (int i = 0; i < 100; i++) {

			String randomWord = reducer.startWord();
			System.out.println("Iteration: " + i + ", searching for:" + randomWord);
			
			byte[] hash = hasher.hash(randomWord);
			String outputWord = searcher.search(hash);

			if (outputWord != null) {
				System.out.println("Found");
				count++;
			}
		}

		System.out.println("Total count = " + count);
	}

}
