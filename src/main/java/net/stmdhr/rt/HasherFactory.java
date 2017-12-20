package net.stmdhr.rt;

public class HasherFactory {

	Hasher[] instances;

	public HasherFactory(String algorithm, int size) {
		instances = new Hasher[size];
		for (int i = 0; i < size; i++) {
			instances[i] = new Hasher(algorithm);
		}
	}

	public Hasher getInstance() {
		for (Hasher hasher : instances) {
			synchronized (hasher) {
				if (hasher.isAvailable()) {
					hasher.borrow();
					return hasher;
				}
			}
		}
		throw new RuntimeException("Ran out of available hashers !!");
	}
	
}
