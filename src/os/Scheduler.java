package os;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Scheduler {
	private int currentProcessID;
	private int timeSlice;
	private Interpreter interpreter;
	private int p1Arrival;
	private int p2Arrival;
	private int p3Arrival;
	private static int clock;
	
	
	
	public Scheduler(int timeSlice, int p1Arrival, int p2Arrival, int p3Arrival) throws IOException {
		this.timeSlice = timeSlice;
		currentProcessID =-1;
		interpreter = new Interpreter();
		this.p1Arrival = p1Arrival;
		this.p2Arrival = p2Arrival;
		this.p3Arrival = p3Arrival;
		clock =0;
		activateScheduler();
	}

	public int getCurrentProcessID() {
		return currentProcessID;
	}

	public void setCurrentProcessID(int currentProcessID) {
		this.currentProcessID = currentProcessID;
	}

		
	public int getTimeSlice() {
		return timeSlice;
	}

	public void setTimeSlice(int timeSlice) {
		this.timeSlice = timeSlice;
	}

	public void switchProcess(){
		if(interpreter.getReadyQueue().peek() != null)
			this.currentProcessID = (int) interpreter.getReadyQueue().remove();
		else
			this.currentProcessID = -1;
			
	}
	
	public void activateScheduler() throws IOException {
		if (p1Arrival ==clock) 
			interpreter.read(new File ("src/resources/Program_1.txt"));
		else if (p2Arrival ==clock) 
			interpreter.read(new File ("src/resources/Program_2.txt"));
		else if (p3Arrival ==clock)
			interpreter.read(new File ("src/resources/Program_3.txt"));
		else {
			if (clock > p1Arrival &&  clock > p2Arrival && clock > p3Arrival && interpreter.getMainMemory().getMainMemory()[0] == null && interpreter.getMainMemory().getMainMemory()[20] == null ) {
				return;
			}
			else if (interpreter.getMainMemory().getMainMemory()[0] == null && interpreter.getMainMemory().getMainMemory()[20] == null) {
				clock++;
				activateScheduler();
				return;
			} 
			
			
		}
	
		
		switchProcess();
		
		//print memory
		
		System.out.println("The main memory contents of clock cycle " + clock + " is :");
		System.out.println("In partition one: ");
		if(interpreter.getMainMemory().getMainMemory()[0]==null)
			System.out.println("null");
		else {
			System.out.println("Process ID : " + interpreter.getMainMemory().getMainMemory()[0]);
			
			if (interpreter.getBlockedQueue().contains(interpreter.getMainMemory().getMainMemory()[0])) {
				interpreter.getMainMemory().getMainMemory()[1] = "Blocked";
				System.out.println("State : " + interpreter.getMainMemory().getMainMemory()[1]);
			}
			else {
				interpreter.getMainMemory().getMainMemory()[1] = "Ready";
				System.out.println("State : " + interpreter.getMainMemory().getMainMemory()[1]);
			}
				
			
			
			System.out.println("PC : " + interpreter.getMainMemory().getMainMemory()[2]);
			System.out.println("Lower Boundary : " + interpreter.getMainMemory().getMainMemory()[3]);
			System.out.println("Upper Boundary : " + interpreter.getMainMemory().getMainMemory()[4]);
			System.out.println("Variable 1 : " + interpreter.getMainMemory().getMainMemory()[5]);
			System.out.println("Variable 2 : " + interpreter.getMainMemory().getMainMemory()[6]);
			System.out.println("Variable 3: " + interpreter.getMainMemory().getMainMemory()[7]);
			System.out.println("The process' Instructions: ");
			for (int i=8; i<20 ; i++) {
				if (interpreter.getMainMemory().getMainMemory()[i] instanceof String[])
					System.out.println(String.join(" ", (String[])interpreter.getMainMemory().getMainMemory()[i]));
			}
		}
		
		System.out.println("In partition two: ");
		if(interpreter.getMainMemory().getMainMemory()[20]==null)
			System.out.println("null");
		else {
			System.out.println("Process ID : " + interpreter.getMainMemory().getMainMemory()[20]);
			
			
			if (interpreter.getBlockedQueue().contains(interpreter.getMainMemory().getMainMemory()[20])) {
				interpreter.getMainMemory().getMainMemory()[21] = "Blocked";
				System.out.println("State : " + interpreter.getMainMemory().getMainMemory()[21]);
			}
			else {
				interpreter.getMainMemory().getMainMemory()[21] = "Ready";
				System.out.println("State : " + interpreter.getMainMemory().getMainMemory()[21]);
			}
			
			
			System.out.println("PC : " + interpreter.getMainMemory().getMainMemory()[22]);
			System.out.println("Lower Boundary : " + interpreter.getMainMemory().getMainMemory()[23]);
			System.out.println("Upper Boundary : " + interpreter.getMainMemory().getMainMemory()[24]);
			System.out.println("Variable 1 : " + interpreter.getMainMemory().getMainMemory()[25]);
			System.out.println("Variable 2 : " + interpreter.getMainMemory().getMainMemory()[26]);
			System.out.println("Variable 3: " + interpreter.getMainMemory().getMainMemory()[27]);
			System.out.println("The process' Instructions: ");
			for (int i=28; i<40 ; i++) {
				if (interpreter.getMainMemory().getMainMemory()[i] instanceof String[]) {
					System.out.println(String.join(" ", (String[])interpreter.getMainMemory().getMainMemory()[i]));
				}
					
			}
		}
		
		//if there is a ready process make it execute instructions according to timeslice then switch
		/*if (interpreter.getReadyQueue().peek()!= null)
			currentProcessID=(int) interpreter.getReadyQueue().remove(); */
		
		
		if (currentProcessID != -1) {  
			int processIndex = interpreter.searchForProcess(currentProcessID);
			
			
			//if processIndex -1 , it might be that it is in the secondary memory, get it and replace it with another process
			if (processIndex==-1 && interpreter.hasProcess(interpreter.getDisk(), currentProcessID)) {
				
				//first, move the old instruction to secondary memory
				int iLowerBound =Interpreter.getPartitionReplace();
				int iUpperBound=20;
				
				if (Interpreter.getPartitionReplace() == 0) 
					Interpreter.setPartitionReplace(20); 
				else {
					Interpreter.setPartitionReplace(0);
					iUpperBound = 40;
				}
				//print that this process is the one going to the disk
				System.out.println("The process ID swapping out and going to Disk is: " + interpreter.getMainMemory().getMainMemory()[iLowerBound]);
				System.out.println("The process ID swapping in and going to Memory is: " + currentProcessID);
				
				int numOfInstructions = interpreter.countInstructions(interpreter.getDisk());
				String [] diskInstruction = new String[20];
				//removing it from main memory
				int counter =0;
				for(int i=iLowerBound; i<iUpperBound; i++) {
					
					if (interpreter.getMainMemory().getMainMemory()[i] !=null) {
						if (interpreter.getMainMemory().getMainMemory()[i] instanceof String[])
							diskInstruction[counter] = String.join(" ", (String[])interpreter.getMainMemory().getMainMemory()[i]);
						else
							diskInstruction[counter] = interpreter.getMainMemory().getMainMemory()[i] + "";
						counter++;
					}
					interpreter.getMainMemory().getMainMemory()[i]=null;
				}
				
				//putting disk content into memory
				Object [] arrInstruction = interpreter.processFromDisk(interpreter.getDisk());
				counter=0;
				for (int i=iLowerBound; i< arrInstruction.length+iLowerBound ; i++) {
					if (arrInstruction[counter]!=null)
						interpreter.getMainMemory().getMainMemory()[i] = arrInstruction[counter];
					else
						break;
					counter++;
				}
				
				//putting swapped instruction back to disk
				interpreter.processToDisk(diskInstruction, interpreter.getDisk());
				
				processIndex = iLowerBound;
				interpreter.getMainMemory().getMainMemory()[processIndex+3] = iLowerBound;
				interpreter.getMainMemory().getMainMemory()[processIndex+4] = iLowerBound + 8 + numOfInstructions;
				
				//print new seconday memory and skip the 3 files in beginning 
				
				System.out.println("The contents of Disk is: ");
				interpreter.printDisk(interpreter.getDisk());
					
			}
			
			// the process is in the memory in this order: ID, state, PC , lower boundary, upper boundary, var1, var2, var3, then the instructions
			//fetch and execute 2 instructions according to the pc, initially pc = 1
			
			//print current process ID
			String processName = "";
			if (currentProcessID == 1000)
				processName = "Program_1.txt";
			else if (currentProcessID == 1001)
				processName = "Program_2.txt";
			else
				processName = "Program_3.txt";		
			System.out.println("Current process ID running: " + currentProcessID + " (" + processName + ")" );
			
			System.out.println("Ready Queue : " + interpreter.getReadyQueue());
			System.out.println("General blocked Queue : " + interpreter.getBlockedQueue());
			System.out.println("userInput Blocked Queue : " + interpreter.getBlockedInputQueue());
			System.out.println("userOutput Blocked Queue : " + interpreter.getBlockedOutputQueue());
			System.out.println("File Blocked Queue : " + interpreter.getBlockedFileQueue());
		
			
			int pc = (int) interpreter.getMainMemory().getMainMemory()[processIndex + 2];
			
			//EXECUTING INSTRUCTIONS ACCRODING TO TIMESLICE
			
			for(int i=0; i<timeSlice ; i++) {
				if ((interpreter.getBlockedQueue().contains(currentProcessID))) {
					break;
				}
				String[] instruction = (String[]) interpreter.getMainMemory().getMainMemory()[processIndex+pc+7];
				if (instruction != null) {
					System.out.println("Current process ID running: " + currentProcessID + " (" + processName + ")" );
					if (instruction[0].equals("assign") && instruction[2].equals("input") && interpreter.getTempVariable() ==null) {
						pc--;
					}
					else if (instruction[0].equals("assign") && instruction[2].equals("readFile") && interpreter.getTempVariable2() ==null) {	
						pc--;
					}
					

					interpreter.execute(currentProcessID, instruction);
				}
					
				if ((interpreter.getBlockedQueue().contains(currentProcessID))) {
					break;
				}
				pc++;
				
				if (i+1 < timeSlice && instruction != null &&  interpreter.getMainMemory().getMainMemory()[processIndex+pc+7] != null){
					clock++;
					
					
					//PRINTING MEMORY CONTENT
					System.out.println("---------------------------------------------------------------");
					//CLOCK GOT INCREMENTED, so you need to print outputs ++ check for any arriving new programs
					int diskIndex = 0;
					if (processIndex ==0)
						diskIndex=20;
					if (p1Arrival ==clock) 
						interpreter.read(new File ("src/resources/Program_1.txt"), diskIndex);
					else if (p2Arrival ==clock) 
						interpreter.read(new File ("src/resources/Program_2.txt"), diskIndex);
					else if (p3Arrival ==clock)
						interpreter.read(new File ("src/resources/Program_3.txt"), diskIndex);
					System.out.println("The main memory contents of clock cycle " + clock + " is :");
					
					System.out.println("In partition one: ");
					if(interpreter.getMainMemory().getMainMemory()[0]==null)
						System.out.println("null");
					else {
						System.out.println("Process ID : " + interpreter.getMainMemory().getMainMemory()[0]);
						
						if (interpreter.getBlockedQueue().contains(interpreter.getMainMemory().getMainMemory()[0])) {
							interpreter.getMainMemory().getMainMemory()[1] = "Blocked";
							System.out.println("State : " + interpreter.getMainMemory().getMainMemory()[1]);
						}
						else {
							interpreter.getMainMemory().getMainMemory()[1] = "Ready";
							System.out.println("State : " + interpreter.getMainMemory().getMainMemory()[1]);
						}
							
						
						
						System.out.println("PC : " + interpreter.getMainMemory().getMainMemory()[2]);
						System.out.println("Lower Boundary : " + interpreter.getMainMemory().getMainMemory()[3]);
						System.out.println("Upper Boundary : " + interpreter.getMainMemory().getMainMemory()[4]);
						System.out.println("Variable 1 : " + interpreter.getMainMemory().getMainMemory()[5]);
						System.out.println("Variable 2 : " + interpreter.getMainMemory().getMainMemory()[6]);
						System.out.println("Variable 3: " + interpreter.getMainMemory().getMainMemory()[7]);
						System.out.println("The process' Instructions: ");
						for (int j=8; j<20 ; j++) {
							if (interpreter.getMainMemory().getMainMemory()[j] instanceof String[])
								System.out.println(String.join(" ", (String[])interpreter.getMainMemory().getMainMemory()[j]));
						}
					}
					
					System.out.println("In partition two: ");
					if(interpreter.getMainMemory().getMainMemory()[20]==null)
						System.out.println("null");
					else {
						System.out.println("Process ID : " + interpreter.getMainMemory().getMainMemory()[20]);
						
						
						if (interpreter.getBlockedQueue().contains(interpreter.getMainMemory().getMainMemory()[20])) {
							interpreter.getMainMemory().getMainMemory()[21] = "Blocked";
							System.out.println("State : " + interpreter.getMainMemory().getMainMemory()[21]);
						}
						else {
							interpreter.getMainMemory().getMainMemory()[21] = "Ready";
							System.out.println("State : " + interpreter.getMainMemory().getMainMemory()[21]);
						}
						
						
						System.out.println("PC : " + interpreter.getMainMemory().getMainMemory()[22]);
						System.out.println("Lower Boundary : " + interpreter.getMainMemory().getMainMemory()[23]);
						System.out.println("Upper Boundary : " + interpreter.getMainMemory().getMainMemory()[24]);
						System.out.println("Variable 1 : " + interpreter.getMainMemory().getMainMemory()[25]);
						System.out.println("Variable 2 : " + interpreter.getMainMemory().getMainMemory()[26]);
						System.out.println("Variable 3: " + interpreter.getMainMemory().getMainMemory()[27]);
						System.out.println("The process' Instructions: ");
						for (int j=28; j<40 ; j++) {
							if (interpreter.getMainMemory().getMainMemory()[j] instanceof String[]) {
								System.out.println(String.join(" ", (String[])interpreter.getMainMemory().getMainMemory()[j]));
							}
								
						}
							
						
					} 
					
					
					
					
				}
				
			}
			
			
			//store new pc in memory
			interpreter.getMainMemory().getMainMemory()[processIndex + 2] = pc;
			
			if (!(interpreter.getBlockedQueue().contains(currentProcessID)))
				interpreter.getReadyQueue().add(currentProcessID);
			
			//if current process is completely finished, remove it from main memory
			if (interpreter.getMainMemory().getMainMemory()[processIndex+pc+6] ==null|| interpreter.getMainMemory().getMainMemory()[processIndex+pc+7] == null) {
				
				if(interpreter.getReadyQueue().contains(interpreter.getMainMemory().getMainMemory()[processIndex]))
					interpreter.getReadyQueue().remove(interpreter.getMainMemory().getMainMemory()[processIndex]);
				
				int lowerBound = (int) interpreter.getMainMemory().getMainMemory()[processIndex+3];
				int upperBound = 20;
				if (lowerBound==20)
					upperBound=40;
						
				for (int i=lowerBound;i<upperBound;i++) {
					interpreter.getMainMemory().getMainMemory()[i]=null;
				}
				
				
				
				System.out.println("Ready Queue : " + interpreter.getReadyQueue());
				System.out.println("General blocked Queue : " + interpreter.getBlockedQueue());
				System.out.println("userInput Blocked Queue : " + interpreter.getBlockedInputQueue());
				System.out.println("userOutput Blocked Queue : " + interpreter.getBlockedOutputQueue());
				System.out.println("File Blocked Queue : " + interpreter.getBlockedFileQueue());
			} 
				
				
				
		
						
			
		}

			
		//switch proccesses
		
		
		clock++;
		
		System.out.println ("-------------------------------------------------------------------------------------------------");
		activateScheduler();
	}
	
	
	public static void main (String[] args) throws IOException {
		Scheduler scheduler = new Scheduler(2,0,1,4);

	}
	
	

}
