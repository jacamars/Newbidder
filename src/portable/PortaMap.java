package portable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * Portable String based HashMap. Keys are strings, so are values.
 */
public class PortaMap {
	
	private int SIZE = 16;
	private double loadFactor = 0.75f;
	private Entry table[] = new Entry[SIZE];
	private int boundary = Double.valueOf(loadFactor * SIZE).intValue();
	
	private int entries = 0;

	public PortaMap() {
		
	}
	public PortaMap(double loadFactor) {
		this.loadFactor = loadFactor;
	}
	
	/**
	 * User defined map data structure with key and value.
	 * 
	 * This is also used as linked list in case multiple key-value pairs lead to
	 * the same bucket with same hashcodes and different keys (collisions) using
	 * the pointer 'next'.
	 *
	 * @author ntallapa
	 */
	class Entry {
		private String key;
		private String value;
		private Entry next;
		private int hashCode;

		Entry(String k, String v) {
			key = k;
			value = v;
			hashCode = k.hashCode();
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getKey() {
			return key;
		}
		
		public int getHashCode() {
			return hashCode;
		}
	}

	/**
	 * It makes sure the bucket number falls within the size of the hashmap
	 * 
	 * @param hash
	 * @return returns index for hashcode hash
	 */
	private int getBucketNumber(int hash) {
		return hash & (SIZE - 1);
	}

	/**
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for the key, the old value is
	 * replaced.
	 */
	public void put(String key, String value) {
		int hash = key.hashCode();
		
		if (key.equals("a1")) {
			System.out.println("HERE");
		}

		// compute the bucket number (0-15 based on our default size)
		// this always results in a number between 0-15
		int bucket = getBucketNumber(hash);
		Entry existingElement = table[bucket];

		for (; existingElement != null; existingElement = existingElement.next) {
			if (existingElement.key.equals(key)) {
				existingElement.value = value;
				return;
			}
		}
		Entry entryInOldBucket = new Entry(key, value);
		entryInOldBucket.next = table[bucket];
		table[bucket] = entryInOldBucket;
		
		addEntryAndRehashIfNeeded();
	}
	
	void put(Entry e, Entry[] array) {
		if (e == null)
			return;
		
		int hash = e.getHashCode();

		// compute the bucket number (0-15 based on our default size)
		// this always results in a number between 0-15
		
		if (e.key.equals("a1")) {
			System.out.println("HERE");
		}
		int bucket = getBucketNumber(hash);
		
		System.out.println(e.getKey() + ", BUCKET: " + bucket);
		Entry existingElement = array[bucket];
		if (existingElement == null) {
			array[bucket] = e;
		} else {		
			while(existingElement.next != null)
				existingElement = existingElement.next;
			existingElement.next = e;
		}
		
		if (e.next != null) {
			var next = e.next;
			e.next = null;
			put(next,array);
		}
	}
	
	void addEntryAndRehashIfNeeded() {
		entries++;
		if (entries >= boundary) {
			int oldSize = SIZE;
			SIZE = SIZE * 2;
			Entry [] array = new Entry[SIZE];
			for (int i=0;i<oldSize;i++) {
				Entry e = table[i];
				put(e,array);
			}
			table = array;
			boundary = Double.valueOf(loadFactor * SIZE).intValue();
		}
	}

	/**
	 * Returns the entry associated with the specified key in the HashMap.
	 * Returns null if the HashMap contains no mapping for the key.
	 */
	public String get(String key) {
		int hash = key.hashCode();
		// compute the bucket number (0-15 based on our default size)
		// this always results in a number between 0-15
		int bucket = getBucketNumber(hash);

		// get the element at the above bucket if it exists
		Entry existingElement = table[bucket];

		// if bucket is found then traverse through the linked list and
		// see if element is present
		while (existingElement != null) {
			if (existingElement.key.equals(key)) {
				return existingElement.getValue();
			}
			existingElement = existingElement.next;
		}

		// if nothing is found then return null
		return null;
	}
	
	public void remove(String key) {
		// get the hashcode and regenerate it to be optimum
		int hash = key.hashCode();

		// compute the bucket number (0-15 based on our default size)
		// this always results in a number between 0-15
		int bucket = getBucketNumber(hash);

		// get the element at the above bucket if it exists
		Entry existingElement = table[bucket];
		Entry lastElement = null;
		
		if (existingElement == null)
			return;

		// if bucket is found then traverse through the linked list and
		// see if element is present
		entries--;
		while (existingElement != null) {
			System.out
					.println("Traversing the list inside the bucket for the key "
							+ existingElement.getKey());
			if (existingElement.key.equals(key)) {
				if (lastElement != null) 
					lastElement.next = existingElement.next;
				else 
					table[bucket] = null;
				return;
			}
			lastElement = existingElement;
			existingElement = existingElement.next;
		}
	}

	// for testing our own map
	public static void main(String[] args) {
		PortaMap porto = new PortaMap();
		Map<String,String> check = new HashMap<>();
		for (int i=0;i<10000;i++) {
			porto.put("a" + i, "Hello " + i);
			check.put("a" + i, "Hello " + i);
		}
		
		String v = porto.get("a1000");
		System.out.println("Final = " + v);
	}

}