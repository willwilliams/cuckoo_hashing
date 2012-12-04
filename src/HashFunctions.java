import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;

// Class to store, generate and calculate the hash functions
// needed to perform Cuckoo Hashing

public class HashFunctions {

	public ArrayList<ArrayList<Integer>> storedFunctionValues = new ArrayList<ArrayList<Integer>>();
	public ArrayList<ArrayList<BigInteger>> storedFunctionValuesBig = new ArrayList<ArrayList<BigInteger>>();
	Integer primeNumber;
	Random generator;
	BigInteger p, ts;

	// Amount of independence between hash functions
	int k;

	// Table size
	Integer sizeOfTable;

	HashFunctions(int tableSize)
	{
		// Variables required for all hash functions
		sizeOfTable = tableSize;
		generator = new Random(System.currentTimeMillis() * 2307);
		p = new BigInteger(128, 100, generator);
		ts = new BigInteger(sizeOfTable.toString());
		//primeNumber = 805306457;
		// p = new BigInteger(24593 + "");
	}
	
	// *********** RANDOM NUMBER GENERATION *************** //
	private int getRandomInt()
	{
		return generator.nextInt(Math.abs(p.intValue()));
	}
	
	private BigInteger getRandomBigInt()
	{
		return new BigInteger(128, generator).mod(p);
	}

	// *********** FAMILY CHOOSER ****************** //
	
	public int evaluate(String family, int functionNumber, String key)
	{
		if (family.equals("k-independent"))
			return evaluateHashFunction(functionNumber, key);
		else if (family.equals("dotproduct"))
			return evaluateDPHashFunction(functionNumber, key);
		else
		{
			throw new Error("Family doesn't exist, quitting");
		}
	}
	public void generate(String family, int k, int quantity)
	{
		if (family.equals("k-independent"))
			generateFunctions(quantity);
		else if (family.equals("dotproduct"))
			generateDPFunctions(k, quantity);
		else
		{
			throw new Error("Family doesn't exist, quitting");
		}
	}
	
	// ******** K INDEPENDENT HASH FUNCTIONS **************** // 
	
	/** Construct k-independent hash functions
	 * 
	 * k = number of coefficients in this hash function
	 * quantity = number of hash functions required
	 */
	public void generateFunctions(int quantity) {
		
		// 13-wise independence for O(log(n)) independent functions
		//k = (int) (Math.log(sizeOfTable) / Math.log(2));
		
		k = 2;
		
		// Generate a given number of hashfunctions
		for (int functionNumber = 0; functionNumber < quantity; functionNumber++) {
			ArrayList<BigInteger> coefficients = new ArrayList<BigInteger>();
			
			// Generate (0,1,...k-1) coefficients
			for (int i = 0; i < k; i++) {
				coefficients.add(getRandomBigInt());
			}
			storedFunctionValuesBig.add(coefficients);
//			System.out.println("Added new hash function with coefficients: ");
//			for (int i =0; i<coefficients.size(); i++){ System.out.print(coefficients.get(i) + " ");}
		}
	}

	/** Evaluate k-independent hash functions of the form :
	 * 
	 * ((c0 + c1x + c2x^2 + ... + c(k-1)x^(k-1)) mod p) mod ts
	 *
	 */
	public int evaluateHashFunction(int functionNumber, String key) {
		// Take the functions coefficients and evaluate:
		// h_functionNumber(key) =
		// ((c0 + c1x + c2x^2 + ... + c(k-1)x^(k-1)) mod p) mod ts

		String key_x = key.hashCode() + "";
		ArrayList<BigInteger> f = storedFunctionValuesBig.get(functionNumber);
		
		// Final hash value and x^i containers
		BigInteger result = new BigInteger("0");
		BigInteger bigBoy = new BigInteger(key_x);
		
		// Prime and table size to mod the hash function with
		// BigInteger p = new BigInteger(primeNumber.toString());

			for (int i = 0; i < k; i++) {
				// get the correct x
				if (i != 0)
					bigBoy = bigBoy.multiply(new BigInteger(key_x)).mod(p);

				// System.out.println("x^" + i + " = " + bigBoy.toString());
				// add coefficient*x^i to the accumulator

				result = result.add(
						(new BigInteger(f.get(i).toString())).multiply(bigBoy))
						.mod(p);

			}
		
		return result.mod(ts).intValue();
	}

	
	// *********** DOT PRODUCT HASH FUNCTIONS ********* //
	
	/** Perform base reduction mod N */
	private ArrayList<Integer> decomposeKey(String key_in)
	{
		BigInteger key = new BigInteger(key_in);
		ArrayList<Integer> decomposedResult = new ArrayList<Integer>();
		BigInteger remainder;
		
		while(key.intValue()!=0)
		{
			remainder = key.mod(p);
			decomposedResult.add(remainder.intValue());
			
			key = key.subtract(remainder);
			key = key.divide(p);
		}
		return decomposedResult;
	}
	
	public void generateDPFunctions(int key_length, int quantity)
	{
		k = key_length;
		// Generate a given number of hashfunctions
		for (int functionNumber = 0; functionNumber < quantity; functionNumber++) {
			ArrayList<BigInteger> array = new ArrayList<BigInteger>();

			// Generate (0,1,...k-1) coefficients
			for (int i = 0; i < k; i++) {
				array.add(getRandomBigInt());
			}
			storedFunctionValuesBig.add(array);
		}
	}
	
	public int evaluateDPHashFunction(int functionNumber, String key)
	{
		ArrayList<BigInteger> a = storedFunctionValuesBig.get(functionNumber);
		BigInteger b;
		BigInteger result = new BigInteger("0");	
		ArrayList<Integer> dKey = decomposeKey(key);

//		// Vector a
//		for (int i = 0; i < dKey.size(); i++) {
//			a.add(getRandomInt());
//		}
		
		// Dot product mod prime_in
		for (int i=0; i< dKey.size(); i++)
		{
			b = a.get(i).multiply(new BigInteger(dKey.get(i) + ""));
			result = result.add(b.mod(p).mod(ts));
		}
		return result.mod(ts).intValue();
	}
	
	// ************ TEST THE FUNCTIONS *************** //
	
	/** Test functionality of hash function */
	private void fullHashingTest(String hashFamily)
	{
		File file = new File(System.getProperty("user.home")
       + "/workspace/CuckooHashing/src/testkeys.txt");
		
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				System.out.println("Key: " + line + " -> "
						+ evaluate(hashFamily, 0, line));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/** Check that the functions are at least weakly universal */
	private void weaklyUniversalTest()
	{
		File file = new File(System.getProperty("user.home")
			      + "/workspace/CuckooHashing/src/testkeys.txt");
		ArrayList<String> keys = new ArrayList<String>();
		int totalCollisions = 0;
		int totalPairs = 0;
		
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				keys.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int hashFunctionNumber = 0;
		
		for (int i=0; i<keys.size(); i++)
		{
			for (int j=0; j<keys.size(); j++)
			{
				if (evaluate("k-independent", hashFunctionNumber, keys.get(i)) == evaluate("k-independent", hashFunctionNumber, keys.get(j))){
					if (i!=j) totalCollisions++;
				}
				totalPairs++;
			}
			System.out.println("Doing: " + i);
		}
		System.out.println("Pr(collision with random HF) = " + 100*(float)totalCollisions/totalPairs + "%") ;
		// 13-wise independence Pr(collision with random HF) = 0.011920929%
		// Dotproduct family Pr(collision with random HF) = 0.012228%
		Pr(collision with random HF) = 0.01229%
	}

	public static void main(String[] args)
	{
		HashFunctions test = new HashFunctions(8192);
		test.generate("k-independent", 10, 2);
		//test.fullHashingTest("dotproduct");
		test.weaklyUniversalTest();
	}
}
