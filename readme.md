# Rainbow Table
- After dowloading the folder, open command window and go to 'bin' folder.

### Table Generation #

- To Generate rainbow table enter the following in the command window.

	java -cp rainbowtable-1212.jar net.stmdhr.rt.main.Generate numOfChains chainLength wordLength NumOfThreads hashFunction reductionFunction pathOfFileToWrite
    
    > java -cp rainbowtable-1212.jar net.stmdhr.rt.main.Generate 10000 1500 4 4 MD5 simple C:\workspace\sita\rainbowtable\output\test\simple-4.txt

### Table Search #

- To Search enter the following in the command window.

	java -cp rainbowtable-1212.jar net.stmdhr.rt.main.Search HashToSearch pathOfFileToSearchFrom NumOfThreads
    
    > java -cp rainbowtable-1212.jar net.stmdhr.rt.main.Search AC23B464BE6D5F4CC262288A9F06F0EF C:\workspace\sita\rainbowtable\output\test\simple-3.txt 4

### Table Test #

- To Test enter the following in the command window.

	java -cp rainbowtable-1212.jar net.stmdhr.rt.main.Test100 NumOfThreads wordLength numOfTests pathOfFileToSearchFrom
	
    > java -cp rainbowtable-1212.jar net.stmdhr.rt.main.Test100 4 5 10 C:\workspace\sita\rainbowtable\output\test\simple-5.txt
    

- The Hash function can be MD5
- The reduction function for a random string based rainbow table is "SIMPLE", dictionary based rainbow table is either "SIMPLE_DICTIONARY" or "COMPLEX_DICTIONARY"
