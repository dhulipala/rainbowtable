package net.stmdhr.rt.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import net.stmdhr.rt.Hasher;
import net.stmdhr.rt.HasherFactory;
import net.stmdhr.rt.Reducer;
import net.stmdhr.rt.ReducerFactory;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChainSearcher implements Runnable {

	private static Logger LOGGER = LoggerFactory.getLogger(ChainSearcher.class);

	private final String tableFileName;
	private final int bytesPerLine;
	private final int chainLength;

	private final long startAt;
	private final long numOfChains;
	private final byte[] searchHash;

	private final HasherFactory hasherFactory;
	private final ReducerFactory reducerFactory;

	private int threadIndex;

	private final AtomicReference<String> result;

	public ChainSearcher(int threadIndex, String tableFileName, int bytesPerLine, int chainLength, long startAt, long numOfChains, byte[] searchHash,
			HasherFactory hasherFactory, ReducerFactory reducerFactory, AtomicReference<String> result) {

		this.threadIndex = threadIndex;

		this.tableFileName = tableFileName;
		this.bytesPerLine = bytesPerLine;
		this.chainLength = chainLength;

		this.startAt = startAt;
		this.numOfChains = numOfChains;
		this.searchHash = searchHash;

		this.hasherFactory = hasherFactory;
		this.reducerFactory = reducerFactory;
		this.result = result;
	}

	public void run() {

		LOGGER.info("Starting search !! ");
		Hasher hasher = hasherFactory.getInstance();
		Reducer reducer = reducerFactory.getInstance();

		String searchHashStr = Hex.encodeHexString(searchHash);
		LOGGER.info("searchHash={}", searchHashStr);

		try {

			for (int position = chainLength; position > 0; position--) {

				//System.out.println("Searching: thread#=" + threadIndex + ", position=" + position);

				FileReader fileReader = new FileReader(tableFileName);
				BufferedReader reader = new BufferedReader(fileReader);

				reader.readLine(); // Skip first line
				fileReader.skip(bytesPerLine * startAt); // Skip some more lines

				try {

					byte[] compareHash = searchHash.clone();
					if (LOGGER.isDebugEnabled()) {
						String compareHashStr = Hex.encodeHexString(compareHash);
						LOGGER.debug("Calculate Entry: position={}, word=???, hash={}", position - 1, compareHashStr);
					}

					for (int i = position; i < chainLength; i++) {
						String word = reducer.reduce(compareHash, i);
						compareHash = hasher.hash(word);

						if (LOGGER.isDebugEnabled()) {
							String compareHashStr = Hex.encodeHexString(compareHash);
							LOGGER.debug("Calculate Entry: position={}, word={}, hash={}", i, word, compareHashStr);
						}
					}

					long endAt = startAt + numOfChains;
					for (long currentChain = startAt; currentChain < endAt; currentChain++) {

						String line = reader.readLine();
						if (line == null) {
							break;
						}

						final String[] split = line.split(",");
						final String startWordInFile = split[0];
						final String hashInFileStr = split[1];
						int length = hashInFileStr.length();

						if (length % 2 != 0) {
							LOGGER.error("Odd length hash string:{}", line);
							continue;
						}

						byte[] hashInFile = Hex.decodeHex(hashInFileStr);

						if (LOGGER.isDebugEnabled()) {
							String compareHashStr = Hex.encodeHexString(compareHash);
							LOGGER.debug("Checking: position={}, chain={}, compareHash={}, hashInFile={}", position, currentChain, compareHashStr,
									hashInFileStr);
						}

						if (Arrays.equals(compareHash, hashInFile)) {
							LOGGER.info("Chain probably found !!");

							String currentWord = startWordInFile;
							for (int j = 0; j < chainLength; j++) {

								byte[] currentHash = hasher.hash(currentWord);
								if (LOGGER.isDebugEnabled()) {
									String currentHashStr = Hex.encodeHexString(currentHash);
									LOGGER.debug("Retracing chain: position={}, word={}, hash={}", j, currentWord, currentHashStr);
								}

								if (Arrays.equals(currentHash, searchHash)) {
									LOGGER.info("Match found !!");
									result.set(currentWord);
									return;
								}

								currentWord = reducer.reduce(currentHash, j + 1);
							}
							LOGGER.info("Match not found in the chain !!");
						}

					}

					String word = result.get();
					if (word != null) {
						// Result has been found
						LOGGER.info("Search stopped !! ");
						return;
					}

				} finally {
					reader.close();
				}

			}

		} catch (Exception e) {
			LOGGER.error("Error during search", e);
			throw new RuntimeException(e);

		} finally {

			LOGGER.info("Search completed !! ");
			hasher.release();
		}
	}

}