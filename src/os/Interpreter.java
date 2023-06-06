package os;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Interpreter {
	private Memory mainMemory;
	private Mutex userInput;
	private Mutex userOutput;
	private Mutex file;
	private Queue<Object> readyQueue;
	private Queue<Object> blockedQueue;
	private Queue<Object> blockedFileQueue;
	private Queue<Object> blockedInputQueue;
	private Queue<Object> blockedOutputQueue;
	private static int id;
	private static int partitionReplace;
	private File Disk;
	private Object tempVariable;
	private Object tempVariable2;


	public Interpreter() {
		mainMemory = new Memory();
		Disk = new File("src/resources/Disk.txt");
		tempVariable = null;
		tempVariable2 = null;
		//timer = 0;
		id = 1000;
		userInput = new Mutex("userInput",0,false);
		userOutput = new Mutex("userOutput",0,false);;
		file = new Mutex("file",0,false);;
		//this.scheduler = scheduler;
		//this.timeOfArrivals=timeOfArrivals;
		readyQueue =  new LinkedList<Object>();
		blockedQueue = new LinkedList<Object>();
		blockedFileQueue = new LinkedList<Object>();
		blockedInputQueue = new LinkedList<Object>();
		blockedOutputQueue = new LinkedList<Object>();
		partitionReplace = 0;  //alternates between 0 and 20 
	}
	
	public void read (File file) {
		if (mainMemory.getMainMemory()[0]==null) 
			mainMemory.setCount(0);
		else if (mainMemory.getMainMemory()[20]==null) {
			mainMemory.setCount(20);
		}
			
		else {
			//replace instruction using FIFO
			mainMemory.setCount(partitionReplace);
		
			int iLowerBound = partitionReplace;
			int iUpperBound =20;
			if (partitionReplace == 0) 
				partitionReplace = 20;
			else {
				partitionReplace = 0;	
				iUpperBound = 40;
			}
				
			System.out.println("Memory FULL, putting process ID " +  mainMemory.getMainMemory()[iLowerBound] + " in the DISK");	
			//first, move the old instruction to the secondary memory
			String [] diskInstruction = new String[20];
			int counter =0;
			for(int i=iLowerBound; i<iUpperBound; i++) {	
				if (mainMemory.getMainMemory()[i] !=null) {
					if (mainMemory.getMainMemory()[i] instanceof String[])
						diskInstruction[counter] = String.join(" ", (String[])mainMemory.getMainMemory()[i]);
					else
						diskInstruction[counter] = mainMemory.getMainMemory()[i] + "";
					counter++;
				}
				mainMemory.getMainMemory()[i]=null;
			}
			
		
			
			processToDisk(diskInstruction, Disk);
			
			System.out.println("The contents of Disk is: ");
			printDisk(Disk);
				
		
			
			

		}
			
		int lowerBound = mainMemory.getCount();
		ArrayList<String[]> instructions = new ArrayList<String[]>();
		try {
	            FileReader fileReader = new FileReader(file.getPath());
	            BufferedReader bufferedReader = new BufferedReader(fileReader);

	            String line;
	            while ((line = bufferedReader.readLine()) != null) {
	                String[]array = line.split(" ");
	                instructions.add(array);
	            }

	            bufferedReader.close();
	            fileReader.close();
	        } catch (IOException e) {
	            System.err.println("Error: " + e.getMessage());
	        }
		
		int limit = 0;
		if (lowerBound==0)
			limit = lowerBound+8+instructions.size();
		else
			limit = lowerBound-20+8+instructions.size();
		
		if(limit <= 20) {
			PCB pcb = new PCB(id++, "Ready" , 1 , lowerBound,lowerBound+8+instructions.size() );
			//dont just add it to ready queue, but make it at the head of the queue aswell
			readyQueue.add(pcb.getProcessID());
			while(true) {
				if ((int)readyQueue.peek()!= pcb.getProcessID())
					readyQueue.add(readyQueue.remove());
				else
					break;
			}
			
			Object var1 = 0;
			Object var2 = 0;
			Object var3 = 0;
			
			mainMemory.add(pcb.getProcessID());
			mainMemory.add(pcb.getProcessState());
			mainMemory.add(pcb.getPC());
			mainMemory.add(pcb.getLowerBoundary());
			mainMemory.add(pcb.getUpperBoundary());
			mainMemory.add(var1);
			mainMemory.add(var2);
			mainMemory.add(var3);
			
			
			
			for(String[] instruction : instructions ) {
				mainMemory.add(instruction);
			}
			
			
		}
		
		
		
	}
	
	public void read (File file , int index) {  //this method is used in case a programs arrives in the midst of a timeslice
		if (mainMemory.getMainMemory()[0]==null) 
			mainMemory.setCount(0);
		else if (mainMemory.getMainMemory()[20]==null) {
			mainMemory.setCount(20);
		}
			
		else {
			//replace instruction that is not being executed
			mainMemory.setCount(index);
		
			int iLowerBound = index;
			int iUpperBound =20;
			if (index != 0) 
				iUpperBound = 40;
				
			System.out.println("Memory FULL, putting process ID " +  mainMemory.getMainMemory()[iLowerBound] + " in the DISK and putting "  + file.getName() + " in the memory");	
			//first, move the old instruction to the secondary memory
			String [] diskInstruction = new String[20];
			int counter =0;
			for(int i=iLowerBound; i<iUpperBound; i++) {	
				if (mainMemory.getMainMemory()[i] !=null) {
					if (mainMemory.getMainMemory()[i] instanceof String[])
						diskInstruction[counter] = String.join(" ", (String[])mainMemory.getMainMemory()[i]);
					else
						diskInstruction[counter] = mainMemory.getMainMemory()[i] + "";
					counter++;
				}
				mainMemory.getMainMemory()[i]=null;
			}
			
		
			
			processToDisk(diskInstruction, Disk);
			
			System.out.println("The contents of Disk is: ");
			printDisk(Disk);
				
		
			
			

		}
			
		int lowerBound = mainMemory.getCount();
		ArrayList<String[]> instructions = new ArrayList<String[]>();
		try {
	            FileReader fileReader = new FileReader(file.getPath());
	            BufferedReader bufferedReader = new BufferedReader(fileReader);

	            String line;
	            while ((line = bufferedReader.readLine()) != null) {
	                String[]array = line.split(" ");
	                instructions.add(array);
	            }

	            bufferedReader.close();
	            fileReader.close();
	        } catch (IOException e) {
	            System.err.println("Error: " + e.getMessage());
	        }
		
		int limit = 0;
		if (lowerBound==0)
			limit = lowerBound+8+instructions.size();
		else
			limit = lowerBound-20+8+instructions.size();
		
		if(limit <= 20) {
			PCB pcb = new PCB(id++, "Ready" , 1 , lowerBound,lowerBound+8+instructions.size() );
			//dont just add it to ready queue, but make it at the head of the queue aswell
			readyQueue.add(pcb.getProcessID());
			while(true) {
				if ((int)readyQueue.peek()!= pcb.getProcessID())
					readyQueue.add(readyQueue.remove());
				else
					break;
			}
			
			Object var1 = 0;
			Object var2 = 0;
			Object var3 = 0;
			
			mainMemory.add(pcb.getProcessID());
			mainMemory.add(pcb.getProcessState());
			mainMemory.add(pcb.getPC());
			mainMemory.add(pcb.getLowerBoundary());
			mainMemory.add(pcb.getUpperBoundary());
			mainMemory.add(var1);
			mainMemory.add(var2);
			mainMemory.add(var3);
			
			
			
			for(String[] instruction : instructions ) {
				mainMemory.add(instruction);
			}
			
			
		}
		
		
		
	}
	
	public int searchForProcess (int processID) {
		if (mainMemory.getMainMemory()[0]!=null) {
			if ((int)mainMemory.getMainMemory()[0] == processID)
				return 0;
		}
		if (mainMemory.getMainMemory()[20]!=null) {
			if ((int)mainMemory.getMainMemory()[20] == processID)
				return 20;
		}
		return -1;
		
	}
	
	
	public void execute (int processID, String[] line) throws IOException {
		
		System.out.println("Currently executing : " + String.join(" ", line));
		
		if(line[0].equals("semWait")) {
			
			switch(line[1]) {
			case "userInput" :{
				if (userInput.isKeyFlag()) {
					blockedQueue.add(processID);
					blockedInputQueue.add(processID);
					int processIndex = searchForProcess(processID);
					mainMemory.getMainMemory()[processIndex+1] = "Blocked";
					System.out.println("Ready Queue : " + this.getReadyQueue());
					System.out.println("General blocked Queue : " + this.getBlockedQueue());
					System.out.println("userInput Blocked Queue : " + this.getBlockedInputQueue());
					System.out.println("userOutput Blocked Queue : " + this.getBlockedOutputQueue());
					System.out.println("File Blocked Queue : " + this.getBlockedFileQueue());

					
					}
				else {
					userInput.setKeyFlag(true);
					userInput.setProcessID(processID);
					}
				}break;
			case "userOutput":{
				if (userOutput.isKeyFlag()) {
					blockedQueue.add(processID);
					blockedOutputQueue.add(processID);
					int processIndex = searchForProcess(processID);
					mainMemory.getMainMemory()[processIndex+1] = "Blocked";
					System.out.println("Ready Queue : " + this.getReadyQueue());
					System.out.println("General blocked Queue : " + this.getBlockedQueue());
					System.out.println("userInput Blocked Queue : " + this.getBlockedInputQueue());
					System.out.println("userOutput Blocked Queue : " + this.getBlockedOutputQueue());
					System.out.println("File Blocked Queue : " + this.getBlockedFileQueue());
					
					}
				else {
					userOutput.setKeyFlag(true);
					userOutput.setProcessID(processID);
					}
				
			}break;
			case "file" :{
				if (file.isKeyFlag()) {
					blockedQueue.add(processID);
					blockedFileQueue.add(processID);
					readyQueue.remove(processID);
					int processIndex = searchForProcess(processID);
					mainMemory.getMainMemory()[processIndex+1] = "Blocked";
					
					System.out.println("Ready Queue : " + this.getReadyQueue());
					System.out.println("General blocked Queue : " + this.getBlockedQueue());
					System.out.println("userInput Blocked Queue : " + this.getBlockedInputQueue());
					System.out.println("userOutput Blocked Queue : " + this.getBlockedOutputQueue());
					System.out.println("File Blocked Queue : " + this.getBlockedFileQueue());
					
					}
				else {
					file.setKeyFlag(true);
					file.setProcessID(processID);
					}
				}break;
			}
	
		}
		
		else if(line[0].equals("semSignal")) {
			
			switch(line[1]) {
				case "userInput" : {
					if (userInput.isKeyFlag()==true && userInput.getProcessID()==processID) {
						userInput.setKeyFlag(false);
						if(blockedInputQueue.size()!=0) {
							int blockedProcessID= (int)blockedInputQueue.remove();
							blockedQueue.remove(blockedProcessID);
							readyQueue.add(blockedProcessID);
						}
						
					}
				}break;
				case "userOutput":{
					if (userOutput.isKeyFlag()==true && userOutput.getProcessID()==processID) {
						userOutput.setKeyFlag(false);
						if(blockedOutputQueue.size()!=0) {
							int blockedProcessID= (int)blockedOutputQueue.remove();
							blockedQueue.remove(blockedProcessID);
							readyQueue.add(blockedProcessID);
						}
						
					}
				}break;
				case "file" :{
					
					if (file.isKeyFlag()==true && file.getProcessID()==processID) {
						file.setKeyFlag(false);
						if(blockedFileQueue.size()!=0) {
							int blockedProcessID= (int)blockedFileQueue.remove();
							blockedQueue.remove(blockedProcessID);
							readyQueue.add(blockedProcessID);
						}
						
					}
					
				}break;
					
			}
			

		}
		
	
		
		else if (line[0].equals("print")) {
			
			int processIndex = searchForProcess(processID);
			Object var = null;
	        if (line[1].equals("a") || line[1].equals("x" ) || line[1].equals("1"))
	        	var = mainMemory.getMainMemory()[processIndex+5] ;
	        if (line[1].equals("b") || line[1].equals("y" ) || line[1].equals("2"))
	        	var = mainMemory.getMainMemory()[processIndex+6] ;
	        if (line[1].equals("c") || line[1].equals("z" ) || line[1].equals("3"))
	        	var = mainMemory.getMainMemory()[processIndex+7] ;
			System.out.println(var + "");
			
		}
		
		
		else if (line[0].equals("assign")) {
			if (line[2].equals("input")) {
					
				if (tempVariable == null) {
					Scanner scanner = new Scanner(System.in);
			        System.out.print("Please enter a value: ");
			        Object var = scanner.nextLine();
			        tempVariable=var;
				}
					
			        //scanner.close();
			        
				else {
					int processIndex = searchForProcess(processID);
			        if (line[1].equals("a") || line[1].equals("x" ) || line[1].equals("1"))
			        	mainMemory.getMainMemory()[processIndex+5] = tempVariable;
			        if (line[1].equals("b") || line[1].equals("y" ) || line[1].equals("2"))
			        	mainMemory.getMainMemory()[processIndex+6] = tempVariable;
			        if (line[1].equals("c") || line[1].equals("z" ) || line[1].equals("3"))
			        	mainMemory.getMainMemory()[processIndex+7] = tempVariable;
			        tempVariable=null;
				}
			        
			        
			        
			   
			}
			else if (line[2].equals("readFile")) {
				
				
				if (tempVariable2 == null) {
					int processIndex = searchForProcess(processID);
					Object o = null;	
					 String fileName = "";
				        if (line[3].equals("a") || line[3].equals("x" ) || line[3].equals("1"))
				        	fileName = mainMemory.getMainMemory()[processIndex+5].toString() ;
				        if (line[3].equals("b") || line[3].equals("y" ) || line[3].equals("2"))
				        	fileName = mainMemory.getMainMemory()[processIndex+6].toString() ;
				        if (line[3].equals("c") || line[3].equals("z" ) || line[3].equals("3"))
				        	fileName = mainMemory.getMainMemory()[processIndex+7].toString() ;
					try {
				            FileReader fileReader = new FileReader("src/resources/" + fileName);
				            BufferedReader bufferedReader = new BufferedReader(fileReader);

				            String linee;
				            while ((linee = bufferedReader.readLine()) != null) {
				            	try {
				                    o = Integer.parseInt(linee);
				                } catch (NumberFormatException e) {
				                    o = linee;
				                }
				            }

				            bufferedReader.close();
				            fileReader.close();
				        } catch (IOException e) {
				            System.err.println("Error: " + e.getMessage());
				        }
					
					tempVariable2 = o;
				}
				
					
				else {
					int processIndex = searchForProcess(processID);
					if (line[1].equals("a") || line[1].equals("x" ) || line[1].equals("1"))
			        	mainMemory.getMainMemory()[processIndex+5] = tempVariable2;
			        if (line[1].equals("b") || line[1].equals("y" ) || line[1].equals("2"))
			        	mainMemory.getMainMemory()[processIndex+6] = tempVariable2;
			        if (line[1].equals("c") || line[1].equals("z" ) || line[1].equals("3"))
			        	mainMemory.getMainMemory()[processIndex+7] = tempVariable2;
			        tempVariable2= null;
				}
				
					

				
			} 
			
			
		}
		
		else if(line[0].equals("writeFile")) {   //writeFile x y, where x is the filename and y is the data.
			
			String data = "";
			int processIndex = searchForProcess(processID);
			if (line[2].equals("a") || line[2].equals("x" ) || line[2].equals("1"))
	        	data = mainMemory.getMainMemory()[processIndex+5].toString() ;
	        if (line[2].equals("b") || line[2].equals("y" ) || line[2].equals("2"))
	        	data = mainMemory.getMainMemory()[processIndex+6].toString() ;
	        if (line[2].equals("c") || line[2].equals("z" ) || line[2].equals("3"))
	        	data = mainMemory.getMainMemory()[processIndex+7] .toString();
	        
	        String fileName = "";
	        
	        if (line[1].equals("a") || line[1].equals("x" ) || line[1].equals("1"))
	        	fileName = mainMemory.getMainMemory()[processIndex+5].toString() ;
	        if (line[1].equals("b") || line[1].equals("y" ) || line[1].equals("2"))
	        	fileName = mainMemory.getMainMemory()[processIndex+6].toString() ;
	        if (line[1].equals("c") || line[1].equals("z" ) || line[1].equals("3"))
	        	fileName = mainMemory.getMainMemory()[processIndex+7].toString() ;
			
	        File file = new File("src/resources/"+ fileName);
			file.createNewFile();
		    FileWriter fileWriter = new FileWriter("src/resources/"+ fileName);  //assuming they will not just put file name but also extention .txt
		    fileWriter.write(data);
		    fileWriter.close();
		    file.createNewFile();
			
		}
		
		else if (line[0].equals("printFromTo")) {
			int processIndex = searchForProcess(processID);
			int o1 = 0;
			int o2 = 0;
			 if (line[1].equals("a") || line[1].equals("x" ) || line[1].equals("1")) {
				 if(mainMemory.getMainMemory()[processIndex+5] instanceof Integer)
			        	o1 = (int)mainMemory.getMainMemory()[processIndex+5] ;
				 else
					 o1 = Integer.parseInt((String)mainMemory.getMainMemory()[processIndex+5]);
			 }
				 
			 	
			 if (line[1].equals("b") || line[1].equals("y" ) || line[1].equals("2")) {
		    	 if(mainMemory.getMainMemory()[processIndex+6] instanceof Integer)
			        	o1 = (int)mainMemory.getMainMemory()[processIndex+6] ;
				 else
					 o1 = Integer.parseInt((String)mainMemory.getMainMemory()[processIndex+6]);
		     }

			 if (line[1].equals("c") || line[1].equals("z" ) || line[1].equals("3")) {
		    	 if(mainMemory.getMainMemory()[processIndex+7] instanceof Integer)
			        	o1 = (int)mainMemory.getMainMemory()[processIndex+7] ;
				 else
					 o1 = Integer.parseInt((String)mainMemory.getMainMemory()[processIndex+7]);
		     }
			 
			 
			 if (line[2].equals("a") || line[2].equals("x" ) || line[2].equals("1")) {
				 if(mainMemory.getMainMemory()[processIndex+5] instanceof Integer)
			        	o2 = (int)mainMemory.getMainMemory()[processIndex+5] ;
				 else
					 o2 = Integer.parseInt((String)mainMemory.getMainMemory()[processIndex+5]);
			 }
				 
			 	
			 if (line[2].equals("b") || line[2].equals("y" ) || line[2].equals("2")) {
		    	 if(mainMemory.getMainMemory()[processIndex+6] instanceof Integer)
			        	o2 = (int)mainMemory.getMainMemory()[processIndex+6] ;
				 else
					 o2 = Integer.parseInt((String)mainMemory.getMainMemory()[processIndex+6]);
		     }

			 if (line[2].equals("c") || line[2].equals("z" ) || line[2].equals("3")) {
		    	 if(mainMemory.getMainMemory()[processIndex+7] instanceof Integer)
			        	o2 = (int)mainMemory.getMainMemory()[processIndex+7] ;
				 else
					 o2 =Integer.parseInt((String)mainMemory.getMainMemory()[processIndex+7]);
		     }
			

			for (int i=o1+1; i<o2 ; i++) {
				System.out.print(i + " ");
			}
			System.out.println();
			
		}
		
		else if (line[0].equals("input")) {
			Scanner scanner = new Scanner(System.in);
	        System.out.print("Please enter a value: ");
	        Object var = scanner.nextLine();
	        tempVariable = var;
		}
		
		else if (line[0].equals("readFile")) {
			Object o = null;	
			 String fileName = "";
			 int processIndex = searchForProcess(processID);
		        if (line[1].equals("a"))
		        	fileName = mainMemory.getMainMemory()[processIndex+5].toString() ;
		        if (line[1].equals("b"))
		        	fileName = mainMemory.getMainMemory()[processIndex+6].toString() ;
		        if (line[1].equals("c"))
		        	fileName = mainMemory.getMainMemory()[processIndex+7].toString() ;
			try {
		            FileReader fileReader = new FileReader("src/resources/" + fileName);
		            BufferedReader bufferedReader = new BufferedReader(fileReader);

		            String linee;
		            while ((linee = bufferedReader.readLine()) != null) {
		            	try {
		                    o = Integer.parseInt(linee);
		                } catch (NumberFormatException e) {
		                    o = linee;
		                }
		            }

		            bufferedReader.close();
		            fileReader.close();
		        } catch (IOException e) {
		            System.err.println("Error: " + e.getMessage());
		        }
			tempVariable2=o;
		}
	}
	
	public boolean hasContent(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.readLine() != null;
        } catch (IOException e) {
            return false;
        }
    }
	
	
	public  void printDisk(File disk) {
        try (BufferedReader reader = new BufferedReader(new FileReader(disk))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public boolean hasProcess(File disk , int processID) {
		try  {
        	BufferedReader reader = new BufferedReader(new FileReader(disk));
            String firstLine = reader.readLine();
            if (firstLine != null) {
                if (firstLine.equals(processID + "")) {
                	reader.close();
                	return true;
                }
                	
            }
            reader.close();
        } catch (IOException e) {
        	 e.printStackTrace();
        	return false;
           
        }
		
		return false;
    }
	
	public int countInstructions(File disk) {
      int counter =0;
      int instructionCounter=0;
		try  {
        	BufferedReader reader = new BufferedReader(new FileReader(disk));
            while ((reader.readLine()) != null) {
                counter++;
                if (counter>8)
                	instructionCounter++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return instructionCounter;
    }
	
	
	public void processToDisk(String[] process, File disk) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(disk))) {
            for (int i=0; i<process.length ; i++) {
            	if (process[i] !=null) {
            		 writer.write(process[i]);
            		 writer.newLine();
            	}
            	else
            		break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public Object[] processFromDisk(File disk) {
       
		Object [] array = new Object[20];
		int counter=0;
		try  {
        	BufferedReader reader = new BufferedReader(new FileReader(disk));
        	String line;
            while ((line = reader.readLine()) != null) {
            	if (counter<8) {
            		Object o = parseString(line);
                	array[counter] = o;
            	}
            	else {
            		array[counter] = line.split(" ");
            	}
            			
            	counter++;
            }
            reader.close();
        } catch (IOException e) {
        	
            e.printStackTrace();
        }
		
        return array;
    }
	
	public Object parseString(String input) {
        try {
            int value = Integer.parseInt(input);
            return value;
        } catch (NumberFormatException e) {
            return input;
        }
    }

	public Memory getMainMemory() {
		return mainMemory;
	}


	public void setMainMemory(Memory mainMemory) {
		this.mainMemory = mainMemory;
	}


	public Mutex getUserInput() {
		return userInput;
	}


	public void setUserInput(Mutex userInput) {
		this.userInput = userInput;
	}


	public Mutex getUserOutput() {
		return userOutput;
	}


	public void setUserOutput(Mutex userOutput) {
		this.userOutput = userOutput;
	}


	public Mutex getFile() {
		return file;
	}


	public void setFile(Mutex file) {
		this.file = file;
	}


	public Queue<Object> getReadyQueue() {
		return readyQueue;
	}


	public void setReadyQueue(Queue<Object> readyQueue) {
		this.readyQueue = readyQueue;
	}


	public Queue<Object> getBlockedQueue() {
		return blockedQueue;
	}


	public void setBlockedQueue(Queue<Object> blockedQueue) {
		this.blockedQueue = blockedQueue;
	}


	public Queue<Object> getBlockedFileQueue() {
		return blockedFileQueue;
	}


	public void setBlockedFileQueue(Queue<Object> blockedFileQueue) {
		this.blockedFileQueue = blockedFileQueue;
	}


	public Queue<Object> getBlockedInputQueue() {
		return blockedInputQueue;
	}


	public void setBlockedInputQueue(Queue<Object> blockedInputQueue) {
		this.blockedInputQueue = blockedInputQueue;
	}


	public Queue<Object> getBlockedOutputQueue() {
		return blockedOutputQueue;
	}


	public void setBlockedOutputQueue(Queue<Object> blockedOutputQueue) {
		this.blockedOutputQueue = blockedOutputQueue;
	}


	public static int getId() {
		return id;
	}


	public static void setId(int id) {
		Interpreter.id = id;
	}


	public static int getPartitionReplace() {
		return partitionReplace;
	}


	public static void setPartitionReplace(int partitionReplace) {
		Interpreter.partitionReplace = partitionReplace;
	}

	public File getDisk() {
		return Disk;
	}

	public void setDisk(File disk) {
		Disk = disk;
	}

	public Object getTempVariable() {
		return tempVariable;
	}

	public void setTempVariable(Object tempVariable) {
		this.tempVariable = tempVariable;
	}

	public Object getTempVariable2() {
		return tempVariable2;
	}

	public void setTempVariable2(Object tempVariable2) {
		this.tempVariable2 = tempVariable2;
	}

	
	

	
}
