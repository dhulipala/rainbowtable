package net.stmdhr.rt.generate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import net.stmdhr.rt.HasherFactory;
import net.stmdhr.rt.ReducerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableGenerator {

	private static Logger LOGGER = LoggerFactory.getLogger(TableGenerator.class);

	private long chains;
	private int chainLength;
	private int startWordLength;

	private String hashAlgorithm;
	private String reducerType;

	private String tableFileName;
	private String wordFileName;

	private int numOfThreads;

	private HasherFactory hasherFactory;
	private ReducerFactory reducerFactory;

	public TableGenerator(long chains, int chainLength, int startWordLength, String hashAlgorithm, String reducerType, String tableFileName,
			String wordFileName, int numOfThreads) {

		this.chains = chains;
		this.chainLength = chainLength;
		this.startWordLength = startWordLength;

		this.hashAlgorithm = hashAlgorithm;
		this.reducerType = reducerType;

		this.hasherFactory = new HasherFactory(hashAlgorithm, numOfThreads);
		this.reducerFactory = new ReducerFactory(reducerType, startWordLength);

		this.tableFileName = tableFileName;
		this.wordFileName = wordFileName;

		this.numOfThreads = numOfThreads;
	}

	public void generate() {

		BufferedWriter tableWriter = null;
		BufferedWriter wordWriter = null;

		try {

			LOGGER.info("Cleaning");
			tableWriter = new BufferedWriter(new FileWriter(tableFileName, false));

			tableWriter.write("chains=" + chains);
			tableWriter.write(",chainLength=" + chainLength);
			tableWriter.write(",startWordLength=" + startWordLength);
			tableWriter.write(",hashAlgorithm=" + hashAlgorithm);
			tableWriter.write(",reducerType=" + reducerType);
			tableWriter.write("\n");

			if (wordFileName != null) {
				wordWriter = new BufferedWriter(new FileWriter(wordFileName, false));
			}

			ExecutorService service = Executors.newFixedThreadPool(numOfThreads);
			AtomicBoolean failures = new AtomicBoolean(false);

			for (int i = 0; i < chains; i++) {

				ChainGenerator task = new ChainGenerator(i, chainLength, hasherFactory, reducerFactory, tableWriter, wordWriter, failures);

				LOGGER.debug("Submitting chain {}", i);
				service.submit(task);
			}

			LOGGER.info("Waiting");
			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
			LOGGER.info("Waiting done !!");

			if (failures.get()) {
				throw new RuntimeException("Failures occurred in chain generators.");
			}

		} catch (Exception e) {
			LOGGER.error("Error during generate", e);
			throw new RuntimeException(e);

		} finally {
			LOGGER.info("Closing");
			try {
				tableWriter.flush();
				tableWriter.close();
			} catch (Exception e) {
			}
			try {
				wordWriter.flush();
				wordWriter.close();
			} catch (Exception e) {
			}
		}

	}

}
