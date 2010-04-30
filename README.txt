------------------------------------------------------------------------------
Quick start for running book examples on Windows platform
------------------------------------------------------------------------------

______________________________________________________________________________		
1. Verify your environment

	1.1. Make sure that you've placed all distribution files under C:\iWeb2\ 

	1.2. Start windows command line interpreter (cmd.exe) and confirm that you 
	     can run java and ant from the command line on your system. 

         Within a Windows command prompt, execute the following:

		 java -version
	     ant -version

		If you get an error see step 2. Otherwise skip to step 3.

______________________________________________________________________________		
2. Configure your Java and Ant environment variables

	You can skip this step, if you already have JDK and Ant configured on your 
	system to run from command line. Assuming that java jdk is in C:\jdk1.5.0_12 
	and Ant is in C:\apache-ant-1.7.0 use the following commands:

		SET JAVA_HOME=C:\jdk1.5.0_12
		SET ANT_HOME=C:\apache-ant-1.7.0
		SET PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%
	
	If your JDK or Ant are installed elsewhere, please, change the above values 
	accordingly.

	At this point you should be able to run java and ant from command line 
	without errors. If you've only configured environment for your current 
	command line interpreter make sure that you perform steps 3 and 4 in 
	the same instance of interpreter.

______________________________________________________________________________		
3. Reset your CLASSPATH environment variable

	Type the following command in your command prompt:
	
		SET CLASSPATH=

	This command empties your classpath, which is required for the rest of the 
	process to work. In other words, we assume a clean slate for the CLASSPATH 
	environment variable.
 
______________________________________________________________________________		
4. Run ant build file for the project: 

	Within the same command prompt execute the following two commands:

		cd /D C:\iWeb2\build
		ant

	Ant will execute the default target from the C:\iWeb2\build\build.xml 
	build file. It will build all source code and will prepare the 
	'C:\iWeb2\deploy' directory. 

______________________________________________________________________________		
5. Start the BeanShell

	Within the same command prompt execute:

		C:\iWeb2\deploy\bin\bsc.bat

______________________________________________________________________________		
	
You are ready to run book examples!!!	

Note: Within the BeanShell you will have command history. So, if you typed 
something and you would like to repeat it the command with different argument
values or type something else similar to the previous command, you can use the 
UP / DOWN arrows to move up and down the history of the BeanShell commands,
respecatiavely.

Enjoy! 
______________________________________________________________________________		
	
	