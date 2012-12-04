import java.util.Random;
import java.io.*;

public class RandomInputGenerator {
	
	public Random generator;
	
	RandomInputGenerator()
	{
		generator = new Random(System.currentTimeMillis() * 23);
	}
	
	public static void main(String args[]) {
		RandomInputGenerator rig = new RandomInputGenerator();

		try {
			// Create file
			FileWriter fstream = new FileWriter("out.txt");
			BufferedWriter out = new BufferedWriter(fstream);

			for (int i = 0; i < 10000; i++)
			{
				out.write(rig.generator.nextInt(1000000000) + "\n");
			}
			// Close the output stream
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("Done");
	}
}
