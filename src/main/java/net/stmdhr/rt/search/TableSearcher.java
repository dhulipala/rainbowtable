package net.stmdhr.rt.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import net.stmdhr.rt.Hasher;
import net.stmdhr.rt.HasherFactory;
import net.stmdhr.rt.ReducerFactory;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableSearcher {

	private static Logger LOGGER = LoggerFactory.getLogger(TableSearcher.class);

	private long chains;
	private int chainLength;
	private int startWordLength;
	private String hashAlgorithm;
	private String reducerType;

	private String tableFileName;
	int bytesPerLine;

	private int numOfThreads;

	private HasherFactory hasherFactory;
	private ReducerFactory reducerFactory;

	private long chainsPerThread;

	public TableSearcher(String tableFileName, int numOfThreads) {

		try {
			this.tableFileName = tableFileName;
			this.numOfThreads = numOfThreads;

			BufferedReader reader = new BufferedReader(new FileReader(tableFileName));
			String line = reader.readLine();
			String[] entries = line.split(",");
			for (String entry : entries) {
				String[] pair = entry.split("=");
				String key = pair[0];
				String value = pair[1];
				switch (key) {
				case "chains":
					this.chains = Long.valueOf(value);
					break;
				case "chainLength":
					this.chainLength = Integer.valueOf(value);
					break;
				case "startWordLength":
					this.startWordLength = Integer.valueOf(value);
					break;
				case "hashAlgorithm":
					this.hashAlgorithm = value;
					break;
				case "reducerType":
					this.reducerType = value;
					break;
				default:
					break;
				}
			}

			this.hasherFactory = new HasherFactory(hashAlgorithm, numOfThreads);
			this.reducerFactory = new ReducerFactory(reducerType, startWordLength);
			reader.close();

			Hasher hasher = hasherFactory.getInstance();
			int digestLength = hasher.getDigestLength();
			hasher.release();

			// word + comma + MD5 + newline
			this.bytesPerLine = startWordLength + 1 + digestLength + 1;
			this.chainsPerThread = chains / numOfThreads;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String search(final byte[] searchHash) {

		try {
			LOGGER.info("searchHash={}", Hex.encodeHexString(searchHash));

			AtomicReference<String> result = new AtomicReference<String>();
			result.set(null);

			ExecutorService service = Executors.newFixedThreadPool(numOfThreads);
			long startAt = 0; // Skip first line

			for (int i = 0; i < numOfThreads; i++) {
				ChainSearcher task = new ChainSearcher(i, tableFileName, bytesPerLine, chainLength, startAt, chainsPerThread, searchHash,
						hasherFactory, reducerFactory, result);
				service.submit(task);

				startAt += chainsPerThread;
			}

			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

			String word = result.get();
			LOGGER.info("Result:word={}", word);

			return word;

		} catch (Exception e) {
			LOGGER.error("Error during search", e);
			throw new RuntimeException(e);
		}

	}
}
