import java.util.LinkedList;

public class HashValue {
	private String key;
	
	// List of all functions which have acted on this record
	private LinkedList<Integer> list = new LinkedList<Integer>();


	public HashValue(String key, int insertFunction) {
		this.key = key;
		this.list.add(insertFunction);
	}
	
	public String getKey()
	{
		return key;
	}
}
