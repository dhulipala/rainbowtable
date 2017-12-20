package net.stmdhr.rt.generate;

import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

import net.stmdhr.rt.Hasher;
import net.stmdhr.rt.HasherFactory;
import net.stmdhr.rt.Reducer;
import net.stmdhr.rt.ReducerFactory;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChainGenerator implements Runnable {

	private static Logger LOGGER = LoggerFactory.getLogger(ChainGenerator.class);

	private int chainIndex;
	private int chainLength;

	private HasherFactory hasherFactory;
	private ReducerFactory reducerFactory;

	private Writer tableWriter;
	private Writer wordWriter;

	private AtomicBoolean failures;

	public ChainGenerator(int chainIndex, int chainLength, HasherFactory hasherFactory, ReducerFactory reducerFactory, Writer tableWriter,
			Writer wordWriter, AtomicBoolean failures) {
		this.chainIndex = chainIndex;
		this.chainLength = chainLength;

		this.hasherFactory = hasherFactory;
		this.reducerFactory = reducerFactory;

		this.tableWriter = tableWriter;
		this.wordWriter = wordWriter;

		this.failures = failures;
	}

	public void run() {

		Hasher hasher = hasherFactory.getInstance();
		Reducer reducer = reducerFactory.getInstance();

		String startWord = reducer.startWord();
		LOGGER.info("Starting chain !!!");

		try {

			String currentWord = startWord;
			byte[] currentHash = null;

			// iterate for length
			for (int j = 0; j < chainLength; j++) {

				if (wordWriter != null) {
					wordWriter.write(currentWord + ",");
				}

				// Hash
				currentHash = hasher.hash(currentWord);
				if (LOGGER.isDebugEnabled()) {
					String currentHashStr = Hex.encodeHexString(currentHash);
					LOGGER.debug("Chain entry: position={}, word={}, hash={}", j, currentWord, currentHashStr);
				}

				// Reduce
				currentWord = reducer.reduce(currentHash, j + 1);
			}

			// Write line to file
			String currentHashStr = Hex.encodeHexString(currentHash);
			tableWriter.write(startWord + "," + currentHashStr + "\n");

		} catch (Exception e) {

			failures.set(true);

			LOGGER.error("Error during generate", e);
			throw new RuntimeException(e);

		} finally {
			System.out.println("Chain completed !! #=" + chainIndex);
			LOGGER.info("Chain completed !! #={}", chainIndex);
			hasher.release();
		}
	}

}
