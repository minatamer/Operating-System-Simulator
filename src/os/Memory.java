package os;
import java.util.ArrayList;
import java.util.HashMap;
public class Memory {
	private Object[] mainMemory; 
	private int count =0;
	public Memory() {
		 mainMemory= new Object[40];
	}

	public void add(Object o) {
			mainMemory[count]=o;
			count++;

	}

	public Object[] getMainMemory() {
		return mainMemory;
	}

	public void setMainMemory(Object[] mainMemory) {
		this.mainMemory = mainMemory;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}


	
	
	
}

		

