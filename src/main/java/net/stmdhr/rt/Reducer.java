package net.stmdhr.rt;

public interface Reducer {
	
	public String reduce(byte[] hash, int position);
	
	public String startWord();

}
