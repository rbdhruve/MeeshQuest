package cmsc420.meeshquest.part1;

public class Pair<T, U> {         
	private final T t;
	private final U u;

	public Pair(T t, U u) {         
		this.t = t;
	    this.u = u;
	}
	
	public T T() { 
		return this.t;
	}
	
	public U U() { 
		return this.u;
	}
}
