package net.stmdhr.rt.main;

import org.apache.commons.codec.binary.Hex;

import net.stmdhr.rt.search.TableSearcher;

public class Search {

	public static void main(String[] args) {

		try {

			String hashStr = args[0];
			String tableFileName = args[1];
			int numOfThreads = Integer.parseInt(args[2]);

			byte[] hashStrByte = Hex.decodeHex(hashStr);

			TableSearcher searcher = new TableSearcher(tableFileName, numOfThreads);
			String word = searcher.search(hashStrByte);

			System.out.println("Search result:" + word);
			
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
