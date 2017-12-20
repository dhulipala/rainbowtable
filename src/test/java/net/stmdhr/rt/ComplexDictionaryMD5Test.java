package net.stmdhr.rt;

import junit.framework.TestCase;
import net.stmdhr.rt.generate.TableGenerator;
import net.stmdhr.rt.search.TableSearcher;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.junit.Test;

public class ComplexDictionaryMD5Test extends TestCase {

	int chains = 150;
	int chainLength = 50;
	int startWordLength = 4;

	String hashAlgorithm = MessageDigestAlgorithms.MD5;
	String reducerType = ReducerFactory.COMPLEX_DICTIONARY;

	String tableFileName = "output/complex-dict-table.txt";
	String wordFileName = "output/complex-dict-words.txt";

	int numOfThreads = 4;

	@Test
	public void testGenerate() {

		try {

			TableGenerator generator = new TableGenerator(chains, chainLength, startWordLength, hashAlgorithm, reducerType, tableFileName,
					wordFileName, numOfThreads);
			generator.generate();

		} catch (Exception e) {

			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDeHash() {

		try {
			// tommy123
			String hashStr = "AEEF78208162E12ED9C7F3FB4E6253E5";
			byte[] inputHash = Hex.decodeHex(hashStr);

			TableSearcher searcher = new TableSearcher(tableFileName, numOfThreads);
			String outputWord = searcher.search(inputHash);

			System.out.println("inputHash=" + hashStr);
			System.out.println("outputWord=" + outputWord);

			if (outputWord == null) {
				fail();
			}

		} catch (Exception e) {
			
			e.printStackTrace();
			fail();
		}
	}

}
