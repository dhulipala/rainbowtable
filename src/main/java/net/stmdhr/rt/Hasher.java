package net.stmdhr.rt;

import java.security.MessageDigest;

public class Hasher {

	MessageDigest md;
	boolean available = true;

	public Hasher(String algorithm) {
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] hash(String word) {
		return md.digest(word.getBytes());
	}
	
	public synchronized void borrow() {
		available = false;
	}

	public synchronized void release() {
		available = true;
	}

	public synchronized boolean isAvailable() {
		return available;
	}
	
	public int getDigestLength(){
		return md.getDigestLength();
	}

}
