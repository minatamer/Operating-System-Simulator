package os;

public class Mutex {
	
	private String type;
	private int processID;
	private boolean keyFlag;
	
	public Mutex(String type, int processID, boolean keyFlag) {
		this.type = type;
		this.processID = processID;
		this.keyFlag = keyFlag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getProcessID() {
		return processID;
	}

	public void setProcessID(int processID) {
		this.processID = processID;
	}

	public boolean isKeyFlag() {
		return keyFlag;
	}

	public void setKeyFlag(boolean keyFlag) {
		this.keyFlag = keyFlag;
	}
	
	
	
	
	

}
