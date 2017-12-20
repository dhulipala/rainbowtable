# Rainbow Table
- After dowloading the folder, open command window and go to 'bin' folder.

### Table Generation #

- To Generate rainbow table enter the following in the command window.

	java -cp rainbowtable-1212.jar net.stmdhr.rt.main.Generate numOfChains chainLength wordLength NumOfThreads hashFunction reductionFunction pathOfFileToWrite

### Table Search #

- To Search enter the following in the command window.

	java -cp rainbowtable-1212.jar net.stmdhr.rt.main.Search HashToSearch pathOfFileToSearchFrom NumOfThreads

### Table Test #

- To Test enter the following in the command window.

	java -cp rainbowtable-1212.jar net.stmdhr.rt.main.Test100 NumOfThreads wordLength numOfTests pathOfFileToSearchFrom
	

- The Hash function can be MD5
- The reduction function for a random string based rainbow table is "SIMPLE", dictionary based rainbow table is either "SIMPLE_DICTIONARY" or "COMPLEX_DICTIONARY"
