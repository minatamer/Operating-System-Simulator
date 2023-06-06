package os;

	public class PCB {

		private int processID;
		private String processState;
		private int PC;
		private int lowerBoundary;
		private int upperBoundary;

		public PCB(int processID, String processState) {
			this.processID = processID;
			this.processState = processState;

     }

		public PCB(int processID, String processState, int PC, int lowerBoundary, int upperBoundary) {
			this.processID = processID;
			this.processState = processState;
			this.PC = PC;
			this.lowerBoundary = lowerBoundary;
			this.upperBoundary = upperBoundary;
		}

		public int getProcessID() {
			return processID;
		}

		public void setProcessID(int processID) {
			this.processID = processID;
		}

		public String getProcessState() {
			return processState;
		}

		public void setProcessState(String processState) {
			this.processState = processState;
		}

		public int getPC() {
			return PC;
		}

		public void setPC(int pC) {
			PC = pC;
		}

		public int getLowerBoundary() {
			return lowerBoundary;
		}

		public void setLowerBoundary(int lowerBoundary) {
			this.lowerBoundary = lowerBoundary;
		}

		public int getUpperBoundary() {
			return upperBoundary;
		}

		public void setUpperBoundary(int upperBoundary) {
			this.upperBoundary = upperBoundary;
		}
		
		
		
	}		
