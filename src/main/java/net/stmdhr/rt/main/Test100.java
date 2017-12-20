package net.stmdhr.rt.main;

import net.stmdhr.rt.Hasher;
import net.stmdhr.rt.reduce.SimpleReducer;
import net.stmdhr.rt.search.TableSearcher;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;

public class Test100 {

	public static void main(String[] args) {
		try {
			long startTime = System.nanoTime();

			int numOfThreads = Integer.parseInt(args[0]);
			int wordLength = Integer.parseInt(args[1]);
			int numberOfTests = Integer.parseInt(args[2]);
			String tableFileName = args[3];
			TableSearcher searcher = new TableSearcher(tableFileName, numOfThreads);

			
			SimpleReducer reducer = new SimpleReducer(wordLength);
			Hasher hasher = new Hasher(MessageDigestAlgorithms.MD5);
			
			int count = 0;
			for (int i = 0; i < numberOfTests; i++) {

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
			
			long endTime = System.nanoTime();
			System.out.println("Took "+(endTime - startTime) + " ns"); 
		}catch (Exception e) {

			e.printStackTrace();
		}
			
		}

	@Override
	public String toString() {
		return "test100 []";
	}
		
	}

