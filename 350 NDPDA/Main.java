import java.io.*;

public class Main {
	
	private enum InputState {
		IDLE,
		TABLE,
		INIT,
		TEST;
	}
	
	public static void main(String[] args) {
		
		if (args.length < 1) {
			System.out.println("ERROR: Usage <Input File>");
			System.exit(0);
		}
		
		BufferedReader inputBuffer;
		
		try {
			inputBuffer = new BufferedReader(new FileReader(args[0]));
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: Could not open file: " + args[0]);
			return;
		}
		
		String currentLine = null;
		
		try {
			inputBuffer.close();
			inputBuffer = new BufferedReader(new FileReader(args[0]));
		} catch (Exception e) {
			System.err.println("ERROR: Could not open file: " + args[0]);
			return;
		}
		
		InputState inputState = InputState.IDLE;
		NDPDA ndpda = null;
		
		String initState = null;
		char initTOS = 0;
		
		try {
			while ((currentLine = inputBuffer.readLine()) != null) {
				System.out.println(currentLine);
				
				if (currentLine.startsWith("%T")) {
					continue;
				}
				
				switch (inputState) {
				case IDLE:
					
					if (currentLine.compareTo("%BT") == 0) {
						ndpda = new NDPDA();
						inputState = InputState.TABLE;
						continue;
					} else if (currentLine.compareTo("") != 0) {
						System.err.println("WARNING: expected %BT, read: \"" + currentLine + "\"");
						continue;
					}
					
					break;
				case TABLE:
					
					if (currentLine.compareTo("%ST") == 0) {
						inputState = InputState.INIT;
						continue;
					}
					
					parseTransitionTable(currentLine, ndpda);
					break;
				case INIT:
					initTOS = currentLine.charAt(0);
					
					inputState = InputState.TEST;
					
					break;
				case TEST:
					
					if (currentLine.compareTo("%ET") == 0) {
						inputState = InputState.IDLE;
						continue;
					}
					
					System.out.println(ndpda.accept(initState, initTOS, currentLine) ? "ACCEPT" : "REJECT");
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("ERROR: Could not read file: "  + args[0]);
			return;
		}
			
			
		try {
			inputBuffer.close();
		} catch (IOException e) {
			System.out.println("ERROR: Could not close file: "  + args[0]);
			return;
		}
	}

	private static void parseTransitionTable(String transitionString, NDPDA ndpda) {		
		String[] split = transitionString.split("\t");
		
		if (split.length == 5 || split.length == 6) {
			
			String state = split[0];
			if (state.startsWith("+")) {
				state = state.substring(1);
				ndpda.addInitialState(state);
			}
			
			ndpda.addTransition(state, split[1].charAt(0), split[2].charAt(0),
			                   split[3], split[4].charAt(0), split.length == 5 ? 0 : split[5].charAt(0));
		} else {
			System.err.println("WARNING: unable to parse table entry: " + transitionString);
			return;
		}
	}
}