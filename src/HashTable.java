import java.util.ArrayList;
import java.util.Random;

public class HashTable {
	public HashValue[] table;
	private int numberOfCollisions;
	public static int evictions;
	public static boolean rehash;
	public Random gen;
	
	HashTable(int tableSize)
	{
		table  = new HashValue[tableSize];
		numberOfCollisions = 0;
		gen = new Random(System.currentTimeMillis() * 29);
	}
	
	void insertChoose2(String key, HashFunctions functions, String hashFamily, int functionToUse, int limit)
	{	
//		System.out.println("Starting to insert: " + key);
//		System.out.println("functionToUse: " + functionToUse );
		

		int index = functions.evaluate(hashFamily, functionToUse, key);
		HashValue hv = new HashValue(key, functionToUse);
		HashValue evicted;
		
//		System.out.println("Found index: " + index);
		
		if (evictions >= limit) {
			HashTable.rehash = true;
		} else {
			if (table[index] == null) {
				table[index] = hv;
				// System.out.println("Great. Empty slot");
			} else {
				evictions++;
				// System.out.println("Just evicted this bad boy");
				evicted = table[index];
				table[index] = hv;

				String evicted_key = evicted.getKey();

				// TODO: this is messed up
				int h1 = functions.evaluate(hashFamily, 0, evicted_key);

//				 System.out.println("Collision! Kicking out: " + evicted_key +
//				 " in slot " + index);

				// Insert evicted value in another location
				// System.out.println("h1 = " + h1 + " h2 = " +
				// functions.evaluate(hashFamily, 1, key) + " index = " +
				// index);
				if (h1 != index) {
					// System.out.println("Chose h1");
					insertChoose2(evicted_key, functions, hashFamily, 0, limit);
				} else {
					// System.out.println("Chose h2");
					insertChoose2(evicted_key, functions, hashFamily, 1, limit);
				}
			}
		}
		// System.out.println("Finished insertion for: " + key +
		// " ended up in: " + index);
	}

	void insertChoose3(String key, HashFunctions functions, String hashFamily,
			int functionToUse, int limit)
	{
		int index = functions.evaluate(hashFamily, functionToUse, key);
		HashValue hv = new HashValue(key, functionToUse);
		HashValue evicted;

		if (evictions >= limit){
			HashTable.rehash = true;
		} else {
			if (table[index] == null) {
				table[index] = hv;
			} else {
				evictions++;
				evicted = table[index];
				table[index] = hv;

				String evicted_key = evicted.getKey();
				int h1 = functions.evaluate(hashFamily, 0, evicted_key);
				
//				System.out.println("Current: " + index);
//				System.out.println("Collision: h1 = " + h1 + " h2 = " + h2
//						+ " h3 = "
//						+ functions.evaluate(hashFamily, 2, evicted_key));
				if (h1 != index) {
					insertChoose3(evicted_key, functions, hashFamily, 0, limit);
//					System.out.println("Chose 1");
				} else if (gen.nextBoolean()) {
					insertChoose3(evicted_key, functions, hashFamily, 1, limit);
//					System.out.println("Chose 2");
				} else {
					insertChoose3(evicted_key, functions, hashFamily, 2, limit);
//					System.out.println("Chose 3");
				}
			}
		}
	}
	
	void insertChoose4(String key, HashFunctions functions, String hashFamily,
			int functionToUse, int limit)
	{
		int index = functions.evaluate(hashFamily, functionToUse, key);
		HashValue hv = new HashValue(key, functionToUse);
		HashValue evicted;
		int randomFunction = 0, result, tryCandidate;
		
		if (evictions >= limit){
			HashTable.rehash = true;
		} else {
			if (table[index] == null) {
				table[index] = hv;
			} else {
				evictions++;
				evicted = table[index];
				table[index] = hv;

				ArrayList<Integer> candidates = new ArrayList<Integer>();
				String evicted_key = evicted.getKey();
				
				candidates.add(0);
				candidates.add(1);
				candidates.add(2);
				candidates.add(3);
				
//				System.out.println("Trying to find a different function for " + evicted_key + " than " + index);
				
				do{
					// If all four functions hash to the same position choose random function
					if (candidates.size() ==0)
					{
						tryCandidate = gen.nextInt(4);
						break;
					}
					randomFunction = gen.nextInt(candidates.size());
					tryCandidate = candidates.get(randomFunction);
					result = functions.evaluate(hashFamily, tryCandidate, evicted_key);
					candidates.remove(randomFunction);					
				} while (result==index);
				
				insertChoose4(evicted_key, functions, hashFamily, tryCandidate, limit);
			}
		}
	}
	
	int getNumberOfCollisions()
	{
		return numberOfCollisions;
	}
}