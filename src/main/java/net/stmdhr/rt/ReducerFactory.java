package net.stmdhr.rt;

import net.stmdhr.rt.reduce.ComplexDictionaryReducer;
import net.stmdhr.rt.reduce.SimpleDictionaryReducer;
import net.stmdhr.rt.reduce.SimpleReducer;


public class ReducerFactory {

	public static final String SIMPLE = "simple";
	public static final String SIMPLE_DICTIONARY = "simple-dictionary";
	public static final String COMPLEX_DICTIONARY = "complex-dictionary";

	Reducer instance;

	public ReducerFactory(String reducerType, int startWordLength) {

		switch (reducerType) {

		case SIMPLE:
			instance = new SimpleReducer(startWordLength);
			break;
			
		case SIMPLE_DICTIONARY:
			instance = new SimpleDictionaryReducer(startWordLength);
			break;
			
		case COMPLEX_DICTIONARY:
			instance = new ComplexDictionaryReducer(startWordLength);
			break;

		default:
			throw new RuntimeException("Unknown reducer type !!");
		}
	}

	public Reducer getInstance() {
		return instance;
	}

}
