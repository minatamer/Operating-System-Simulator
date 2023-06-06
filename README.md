# Operating-System-Simulator
A project that builds a simulation of an operating system using Java

The best way to understand the concepts of an Operating System is to build
an operating system and then to experiment with it to see how the OS manages
resources and processes. <br> In this project, a simulation of
an operating system is built. The main focus of the project was building a correct
architecture that simulates a real operating system

#
## Building Blocks of the project:
1) Implementing a basic interpreter. 
2) A Text file represents a program. <br> 
3) When you read that text file and start executing
it, it becomes a process.  <<br> 
4) The interpreter reads
the txt files and executes their code.
5) Implementing a
memory and save the processes in it. 
6) Implementing
mutexes that ensure mutual exclusion over the critical resources. 
7) Implement a scheduler that schedules the
processes that we have in our system. In this project Round Robin schedule was used.

#
## Program Syntax

For the programs, the following syntax is used: <br>
• print: to print the output on the screen. Example: print x<br>
• assign: to initialize a new variable and assign a value to it. Example:
assign x y, where x is the variable and y is the value assigned. The value
could be an integer number, or a string. If y is input, it first prints to the
screen "Please enter a value", then the value is taken as an input from
the user. <br>
• writeFile: to write data to a file. Example: writeFile x y, where x is
the filename and y is the data. <br>
• readFile: to read data from a file. Example: readFile x, where x is the
filename <br>
• printFromTo: to print all numbers between 2 numbers. Example: printFromTo
x y, where x is the first number, and y is the second number.<br>
• semWait: to acquire a resource. Example: semWait x, where x is the resource name. For more details refer to section Mutual Exclusion <br>
• semSignal: to release a resource. Example: semSignal x, where x is the
resource name. For more details refer to section Mutual Exclusion
