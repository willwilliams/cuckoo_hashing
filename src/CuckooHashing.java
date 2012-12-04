import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class CuckooHashing {

	HashTable ht;
	HashFunctions functions;
	int elementsAdded;
	Random inputGenerator;
	int limit;
	
	CuckooHashing()
	{
		inputGenerator = new Random(System.currentTimeMillis() * 327);
		limit = 20;
	}
	
	/** Constructs all the parts needed to perform Cuckoo Hashing */
	public HashFunctions construct(int size, int numberOfHashFunctions, String hashFamily)
	{
		// Create hash functions give table size and
		// number of hash functions required
		// ********** CREATE FUNCTIONS *************** //
		HashFunctions funcs = new HashFunctions(size);
		
		// Generate particular family of hash functions with fixed input length
		funcs.generate(hashFamily, 10 ,numberOfHashFunctions);
		
		// File file = new File("testkeys.txt");
		// *********** READ INPUT ******************** //
		File file = new File(System.getProperty("user.home")
				+ "/workspace/CuckooHashing/src/testkeys.txt");

		
		// ************ MAKE HASH TABLE ************** //
		ht = new HashTable(size);
		elementsAdded = 0;
		HashTable.rehash = false;

		// For each line:
		// 1) Hash
		// 2) Create record with key and List of functions used
		// 3) Insert record into the table

		try {
			Scanner scanner = new Scanner(file);

			while (true) {
				String line = scanner.nextLine();
				
				HashTable.evictions = 0;

				if (numberOfHashFunctions==2)
					ht.insertChoose2(line, funcs, hashFamily, 0, limit);
				else if (numberOfHashFunctions==3)
					ht.insertChoose3(line, funcs, hashFamily, 0, limit);
				else if (numberOfHashFunctions==4)
					ht.insertChoose4(line, funcs, hashFamily, 0, limit);
				else
				{
					System.err.println("Tests not defined other than for 2, 3 or 4 functions");
					System.exit(1);
				}
				elementsAdded++;
				
				// Dont actually rehash, but stop the insertion process and store how many we managed
				if (HashTable.rehash==true) break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return funcs;
	}
	
	/** Iterates through functions to perform O(1) lookup */
	public int lookup(String key)
	{
	
		for (int i=0; i<functions.storedFunctionValues.size(); i++)
		{
			int index = functions.evaluateHashFunction(i, key);
			System.out.println(index + " contains " + ht.table[index].getKey());
			if (ht.table[index].getKey().equals(key)) return index;
		}
		return -1;
	}
	
	/** Prints out the dynamically created table */
	public void printTable()
	{
		HashValue hv; int count=0;
		for (int i=0; i<functions.sizeOfTable; i++)
		{
			hv = ht.table[i];
			if (hv==null)
				{System.out.println("ht[" + i + "] = null"); }
			else
				{System.out.println("ht[" + i + "] = " + hv.getKey()); count++; }
		}
		System.out.println(count);
	}
	
	/** Straight forward execution without tests */
	public void run()
	{
		do {
			functions = construct(8192,2, "k-independent");
		} while (HashTable.rehash);
		System.out.println("Cuckoo Hash Table successfully created");
		printTable();
	}
	
	/** Creates table using 2 functions */
	public int runExperiment2Functions()
	{
		functions = construct(8192,2, "dotproduct");
//		System.out.println("Elements added in this table before rehash with 2 functions: " + (float)elementsAdded*100/8192 + "%");
		return elementsAdded;
	}
	
	/** Creates table using 3 functions */
	public int runExperiment3Functions()
	{
		functions = construct(8192,3, "dotproduct");
//		System.out.println("Elements added in this table before rehash with 3 functions: " + (float)elementsAdded*100/8192 + "%");
		return elementsAdded;
	}
	
	/** Creates table using 4 functions */
	public int runExperiment4Functions()
	{
		functions = construct(8192,4, "dotproduct");
//		System.out.println("Elements added in this table before rehash with 4 functions: " + (float)elementsAdded*100/8192 + "%");
		return elementsAdded;
	}
	
	/** Runs the test with both 2 and 3 functions and outputs results */
	public void runFullTest(int iterations)
	{
		int total2 =0, total3 = 0, total4 =0;
		float result2, result3, result4;
		float max2 = 0, max3 = 0, max4 = 0;
		
		int experiment2, experiment3, experiment4;
		
		for (int i=0; i<iterations; i++)
		{
			experiment2  = runExperiment2Functions();
			max2 = Math.max(max2, experiment2);
			total2+= experiment2;
			this.elementsAdded = 0;
//			System.out.println("i= " + i);
		}
		result2 = (float)total2/iterations;
		System.out.println("Average size before rehash with 2 functions (after 1000 tests): " + (float)result2*100/8192 + "%");
		System.out.println("Max with 2 functions: " + (float)max2*100/8192 + "%");
		
		for (int i=0; i<iterations; i++)
		{
			experiment3 = runExperiment3Functions();
			max3 = Math.max(max3, experiment3);
			total3+= experiment3;
			this.elementsAdded = 0;
//			System.out.println("i= " + i);
		}
		result3 = (float)total3/iterations;
		
		System.out.println("Average size before rehash with 3 functions (after 1000 tests): " + (float)result3*100/8192 + "%");
		System.out.println("Max with 3 functions: " + (float)max3*100/8192 + "%");
		
		for (int i=0; i<iterations; i++)
		{
			experiment4 = runExperiment4Functions();
			max4 = Math.max(max4, experiment4);
			total4+= experiment4;
			this.elementsAdded = 0;
//			System.out.println("i= " + i);
		}
		result4 = (float)total4/iterations;
		
		System.out.println("Average size before rehash with 4 functions (after 1000 tests): " + (float)result4*100/8192 + "%");
		System.out.println("Max with 4 functions: " + (float)max4*100/8192 + "%");
	}

	public static void main(String[] args) {
		CuckooHashing ch = new CuckooHashing();
				
//		ch.runExperiment2Functions();
//		ch.runExperiment3Functions();
//		ch.runExperiment4Functions();
		ch.runFullTest(100);
	}
}
