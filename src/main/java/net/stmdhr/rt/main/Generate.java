package net.stmdhr.rt.main;

import net.stmdhr.rt.generate.TableGenerator;

public class Generate {
	
	public static void main(String[] args) {
		
		try {
			long startTime = System.nanoTime();
			int chains = Integer.parseInt(args[0]);
			int chainLength = Integer.parseInt(args[1]);
			int startWordLength = Integer.parseInt(args[2]);

			int numOfThreads = Integer.parseInt(args[3]);

			String hashAlgorithm = args[4];
			String reducerType = args[5];

			String tableFileName = args[6];
			String wordFileName = null;

			TableGenerator generator = new TableGenerator(chains, chainLength, startWordLength, hashAlgorithm, reducerType, tableFileName,
					wordFileName, numOfThreads);
			generator.generate();
			
			long endTime = System.nanoTime();
			System.out.println("Took "+(endTime - startTime) + " ns"); 

		} catch (Exception e) {

			e.printStackTrace();
		}
		
	}
	
	

}
