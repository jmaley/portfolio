import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Stack;

public class NDPDA {
	
	private class Transition {
		private String state, nextState;
		private char input, tos, op, opArg;
		
		public Transition(String state, char input, char tos, String nextState, char op, char opArg) {
			this.state = state;
			this.input = input;
			this.tos = tos;
			this.nextState = nextState;
			this.op = op;
			this.opArg = opArg;
		}
	}
	
	private LinkedList<Transition> transitions;
	private LinkedList<String> initStates;
	private Stack<Character> stack;
	
	private char initTOS;
	
	public NDPDA() {
		transitions = new LinkedList<Transition>();
		initStates = new LinkedList<String>();
		stack = new Stack<Character>();
	}
	
	public boolean addInitialState(String state) {
		
		for (String s : initStates) {
			
			if (s.compareTo(state) == 0) {
				return false;
			}
		}
		
		return initStates.add(state);
	}
	
	public boolean addTransition(String state, char input, char tos, String nextState, char op, char opArg) {
		
		for (Transition t : transitions) {
			
			if (t.state.compareTo(state) == 0 
					&& t.input == input
					&& t.tos == tos
					&& t.nextState.compareTo(nextState) == 0
					&& t.op == op
					&& t.opArg == opArg) {
				return false;
			}
		}
		
		return transitions.add(new Transition(state, input, tos, nextState, op, opArg));
	}
	
	public boolean accept(String state, char tos, String word) {

		initTOS = tos;
		stack.push(initTOS);
		
		for (String s : initStates) {
			
			if (traverseNDPDA(s, word)) {
				return true;
			}
		}
		
		
		return false;
	}
	
	private boolean traverseNDPDA(String state, String word) {
		
		for (Transition t : transitions) {
			
			if (t.state.compareTo(state) != 0 ||
					t.tos != stack.peek()) {
				continue;
			}

			if (word.length() == 0) {
				
				if (t.input != '#') {
					continue;
				}
			} else {
				
				if (t.input != '#' &&
				    t.input != word.charAt(0)) {
					continue;
				}
			}

			char popped = 0;
			
			switch (t.op) {
			case 's':
				stack.push(t.opArg);
				break;
			case 'p':
				popped = stack.pop();
				break;
			case 'n':
				break;
			default:
				System.out.println("ERROR: Unrecognized op code: "  + t.op);
				return false;
			}

			if (traverseNDPDA(t.nextState, t.input == '#' ? word : word.substring(1))) {
				return true;
			} else {
				switch (t.op) {
				case 's':
					stack.pop();
					break;
				case 'p':
					stack.push(popped);
					break;
				}
			}
		}
		
		if (word.length() == 0) {
			
			if (stack.peek() == initTOS) {
				return true;
			}
			else {
				return false;
			}
		}
		
		return false;
	}
}