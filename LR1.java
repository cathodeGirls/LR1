import java.util.*;
/*
 * @author Deirdre Brennan
 * Takes an expression in the grammar as an argument and returns
 * the value of the expression if it is valid.
 * 
 * Terms in the input expression are pushed onto the stack and
 * evaluated based on the rules of the included parsing table.
 */
public class LR1 
{
	public static String expression; //expression to be evaluated
	public static String[] tokens; //list of terms in expression
	public static Deque<Item> exprStack = new LinkedList<Item>(); //stack of symbols parsed
	
	/*
	 * Inner class used for stack items;
	 * Holds a symbol (terminal or non-terminal), a state,
	 * and an integer value for holding the value of 
	 * non-terminal symbols in the stack.
	 */
	static class Item
	{
		String symbol, state;
		int value;
		
		public Item(String symbol, String state, int value)
		{
			this.symbol = symbol;
			this.state = state;
			this.value = value;
		}
		
		public Item(String symbol, String state)
		{
			this.symbol = symbol;
			this.state = state;
			this.value = 0;
		}
		
		public Item(String state)
		{
			this.symbol = "-";
			this.state = state;
			this.value = 0;
		}
		
		/*
		 * Overridden toString method for Items in the stack
		 */
		public String toString()
		{
			return "[" + symbol + ":" + state + "]";
		}

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}
		
		public int getValue()
		{
			return value;
		}
		
		public void setValue(int value)
		{
			this.value = value;
		}
		
	}
	
	public static void main(String[] args) 
	{
		expression = args[0];
		prepExpression();
		parseExpression();
	}
	
	/*
	 * Prepares the expression for parsing, adding a $ 
	 * at the end to symbolize end of expression and separating
	 * the expression into tokens.
	 */
	public static void prepExpression()
	{
		expression = expression.concat(" $");
		System.out.println(expression);
		expression = expression.replaceAll("\\+", " \\+ ");
		expression = expression.replaceAll("-", " - ");
		expression = expression.replaceAll("\\*", " \\* ");
		expression = expression.replaceAll("/", " / ");
		expression = expression.replaceAll("\\(", " \\( ");
		expression = expression.replaceAll("\\)", " \\) ");
		expression = expression.replaceAll("  ", " ");
		tokens = expression.split("\\s");
	}
	
	/*
	 * Method for determining which state to go to
	 * after a non-terminal symbol is encountered.
	 */
	public static int nonTerminalState(String NTSymbol)
	{
		Item currItem = exprStack.peek();
		if(currItem.getState().equals("0"))
		{
			if(NTSymbol.equals("E"))
				return 1;
			else if(NTSymbol.equals("T"))
				return 2;
			else if(NTSymbol.equals("F"))
				return 3;
			else return -1;
		}
		else if(currItem.getState().equals("4"))
		{
			if(NTSymbol.equals("E"))
				return 8;
			else if(NTSymbol.equals("T"))
				return 2;
			else if(NTSymbol.equals("F"))
				return 3;
			else return -1;
		}
		else if(currItem.getState().equals("6"))
		{
			if(NTSymbol.equals("T"))
				return 9;
			else if(NTSymbol.equals("F"))
				return 3;
			else return -1;
		}
		else if(currItem.getState().equals("7"))
		{
			if(NTSymbol.equals("F"))
				return 10;
			else return -1;
		}
		else return -1;
	}
	
	/*
	 * Implementation of the LR(1) method of parsing for this grammar;
	 * Uses a loop of nested switch statements to represent what each state will do
	 * for each symbol. If the expression is invalid, the state is changed to -1, which
	 * causes the loop to exit.
	 */
	public static void parseExpression()
	{
		int state = 0;
		int position = 0; //current position in the expression
		if(tokens[0].equals("")) position++; //handles discrepancy in tokenized expression
		String currSymbol; //holds current symbol in expression

		Item first; //these hold values popped from the stack in state 9,10,and 11
		Item second;
		Item third;
		int newValue = 0;
		exprStack.push(new Item(Integer.toString(state)));
		//loop until equation is accepted or state = -1
		while(state != -1)
		{
			Iterator stackIterator = exprStack.descendingIterator();
			
			//print stack
			while(stackIterator.hasNext())
				System.out.print(stackIterator.next().toString());
			System.out.print(" ");
			
			//print remaining portion of expression to be parsed
			for(int i = position; i<tokens.length; i++)
				System.out.print(tokens[i]);
			System.out.println();
			currSymbol = tokens[position];
			
			//nested switches to implement parse table
			switch(state)
			{
			case 0:

				switch(currSymbol)
				{
				case "(":
					state = 4;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				default:
					if(currSymbol.matches("-?\\d+"))
					{
						state = 5;
						exprStack.push(new Item(currSymbol, Integer.toString(state)));
						position++;
					}
					else state = -1;
					break;
				}
				break;
			case 1:
				switch(currSymbol)
				{
				case "+":
					state = 6;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				case "-":
					state = 6;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				case "$"://expression is valid and accepted, value is displayed
					System.out.println("Valid expression, value = " + exprStack.peek().getValue());
					System.exit(0);
					break;
				default:
					state = -1;
					break;
				}
				break;
			case 2:
				switch(currSymbol)
				{
				case "+":
					if(exprStack.peek().getSymbol().equals("T"))
					{
						newValue = exprStack.peek().getValue();
						exprStack.pop();
						state = nonTerminalState("E");
						exprStack.push(new Item("E",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "-":
					if(exprStack.peek().getSymbol().equals("T"))
					{
						newValue = exprStack.peek().getValue();
						exprStack.pop();
						state = nonTerminalState("E");
						exprStack.push(new Item("E",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "*":
					state = 7;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				case "/":
					state = 7;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				case ")":
					if(exprStack.peek().getSymbol().equals("T"))
					{
						newValue = exprStack.peek().getValue();
						exprStack.pop();
						state = nonTerminalState("E");
						exprStack.push(new Item("E",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "$":
					if(exprStack.peek().getSymbol().equals("T"))
					{
						newValue = exprStack.peek().getValue();
						exprStack.pop();
						state = nonTerminalState("E");
						exprStack.push(new Item("E",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				default:
					state = -1;
					break;
				}
				break;
			case 3:
				switch(currSymbol)
				{
				case "+":
					if(exprStack.peek().getSymbol().equals("F"))
					{
						newValue = exprStack.peek().getValue();
						exprStack.pop();
						state = nonTerminalState("T");
						exprStack.push(new Item("T",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "-":
					if(exprStack.peek().getSymbol().equals("F"))
					{
						newValue = exprStack.peek().getValue();
						exprStack.pop();
						state = nonTerminalState("T");
						exprStack.push(new Item("T",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "*":
					if(exprStack.peek().getSymbol().equals("F"))
					{
						newValue = exprStack.peek().getValue();
						exprStack.pop();
						state = nonTerminalState("T");
						exprStack.push(new Item("T",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "/":
					if(exprStack.peek().getSymbol().equals("F"))
					{
						newValue = exprStack.peek().getValue();
						exprStack.pop();
						state = nonTerminalState("T");
						exprStack.push(new Item("T",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case ")":
					if(exprStack.peek().getSymbol().equals("F"))
					{
						newValue = exprStack.peek().getValue();
						exprStack.pop();
						state = nonTerminalState("T");
						exprStack.push(new Item("T",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "$":
					if(exprStack.peek().getSymbol().equals("F"))
					{
						newValue = exprStack.peek().getValue();
						exprStack.pop();
						state = nonTerminalState("T");
						exprStack.push(new Item("T",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				default:
					state = -1;
					break;
				}
				break;
			case 4:

				switch(currSymbol)
				{
				case "(":
					state = 4;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				default:
					if(currSymbol.matches("-?\\d+"))
					{
						state = 5;
						exprStack.push(new Item(currSymbol, Integer.toString(state)));
						position++;
					}
					else state = -1;
					break;
				}
				break;
			case 5:
				switch(currSymbol)
				{
				case "+":
					if(exprStack.peek().getSymbol().matches("-?\\d+"))
					{
						newValue = Integer.parseInt(exprStack.peek().getSymbol());
						exprStack.pop();
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "-":
					if(exprStack.peek().getSymbol().matches("-?\\d+"))
					{
						newValue = Integer.parseInt(exprStack.peek().getSymbol());
						exprStack.pop();
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "*":
					if(exprStack.peek().getSymbol().matches("-?\\d+"))
					{
						newValue = Integer.parseInt(exprStack.peek().getSymbol());
						exprStack.pop();
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "/":
					if(exprStack.peek().getSymbol().matches("-?\\d+"))
					{
						newValue = Integer.parseInt(exprStack.peek().getSymbol());
						exprStack.pop();
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case ")":
					if(exprStack.peek().getSymbol().matches("-?\\d+"))
					{
						newValue = Integer.parseInt(exprStack.peek().getSymbol());
						exprStack.pop();
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "$":
					if(exprStack.peek().getSymbol().matches("-?\\d+"))
					{
						newValue = Integer.parseInt(exprStack.peek().getSymbol());
						exprStack.pop();
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				default:
					state = -1;
					break;
				}
				break;
			case 6:
				switch(currSymbol)
				{
				case "(":
					state = 4;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				default:
					if(currSymbol.matches("-?\\d+"))
					{
						state = 5;
						exprStack.push(new Item(currSymbol, Integer.toString(state)));
						position++;
					}
					else state = -1;
					break;
				}
				break;
			case 7:
				switch(currSymbol)
				{
				case "(":
					state = 4;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				default:
					if(currSymbol.matches("-?\\d+"))
					{
						state = 5;
						exprStack.push(new Item(currSymbol, Integer.toString(state)));
						position++;
					}
					else state = -1;
					break;
				}
				break;
			case 8:
				switch(currSymbol)
				{
				case "+":
					state = 6;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				case "-":
					state = 6;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				case ")":
					state = 11;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				default:
					state = -1;
					break;
				}
				break;
			case 9:
				switch(currSymbol)
				{
				case "+":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals("T") 
							&& third.getSymbol().equals("E"))
					{
						if(second.getSymbol().equals("+"))
						{
							newValue = third.getValue() + first.getValue();
							exprStack.push(new Item("E",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else if(second.getSymbol().equals("-"))
						{
							newValue = third.getValue() - first.getValue();
							exprStack.push(new Item("E",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else state = -1;
					}
					else state = -1;
					break;
				case "-":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals("T") 
							&& third.getSymbol().equals("E"))
					{
						if(second.getSymbol().equals("+"))
						{
							newValue = third.getValue() + first.getValue();
							exprStack.push(new Item("E",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else if(second.getSymbol().equals("-"))
						{
							newValue = third.getValue() - first.getValue();
							exprStack.push(new Item("E",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else state = -1;
					}
					else state = -1;
					break;
				case "*":
					state = 7;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				case "/":
					state = 7;
					exprStack.push(new Item(currSymbol, Integer.toString(state)));
					position++;
					break;
				case ")":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals("T") 
							&& third.getSymbol().equals("E"))
					{
						if(second.getSymbol().equals("+"))
						{
							newValue = third.getValue() + first.getValue();
							exprStack.push(new Item("E",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else if(second.getSymbol().equals("-"))
						{
							newValue = third.getValue() - first.getValue();
							exprStack.push(new Item("E",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else state = -1;
					}
					else state = -1;
					break;
				case "$":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals("T") 
							&& third.getSymbol().equals("E"))
					{
						if(second.getSymbol().equals("+"))
						{
							newValue = third.getValue() + first.getValue();
							exprStack.push(new Item("E",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else if(second.getSymbol().equals("-"))
						{
							newValue = third.getValue() - first.getValue();
							exprStack.push(new Item("E",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else state = -1;
					}
					else state = -1;
					break;
				default:
					state = -1;
					break;
				}
				break;
			case 10:
				switch(currSymbol)
				{
				case "+":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals("F") 
							&& third.getSymbol().equals("T"))
					{
						if(second.getSymbol().equals("*"))
						{
							newValue = third.getValue() * first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else if(second.getSymbol().equals("/"))
						{
							newValue = third.getValue() / first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else state = -1;
					}
					else state = -1;
					break;
				case "-":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals("F") 
							&& third.getSymbol().equals("T"))
					{
						if(second.getSymbol().equals("*"))
						{
							newValue = third.getValue() * first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else if(second.getSymbol().equals("/"))
						{
							newValue = third.getValue() / first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else state = -1;
					}
					else state = -1;
					break;
				case "*":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals("F") 
							&& third.getSymbol().equals("T"))
					{
						if(second.getSymbol().equals("*"))
						{
							newValue = third.getValue() * first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else if(second.getSymbol().equals("/"))
						{
							newValue = third.getValue() / first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else state = -1;
					}
					else state = -1;
					break;
				case "/":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals("F") 
							&& third.getSymbol().equals("T"))
					{
						if(second.getSymbol().equals("*"))
						{
							newValue = third.getValue() * first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else if(second.getSymbol().equals("/"))
						{
							newValue = third.getValue() / first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else state = -1;
					}
					else state = -1;
					break;
				case ")":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals("F") 
							&& third.getSymbol().equals("T"))
					{
						if(second.getSymbol().equals("*"))
						{
							newValue = third.getValue() * first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else if(second.getSymbol().equals("/"))
						{
							newValue = third.getValue() / first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else state = -1;
					}
					else state = -1;
					break;
				case "$":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals("F") 
							&& third.getSymbol().equals("T"))
					{
						if(second.getSymbol().equals("*"))
						{
							newValue = third.getValue() * first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else if(second.getSymbol().equals("/"))
						{
							newValue = third.getValue() / first.getValue();
							exprStack.push(new Item("T",third.getState(),newValue));
							state = Integer.parseInt(exprStack.peek().getState());
						}
						else state = -1;
					}
					else state = -1;
					break;
				default:
					state = -1;
					break;
				}
				break;
			case 11:
				switch(currSymbol)
				{
				case "+":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals(")") 
							&& second.getSymbol().equals("E")
							&& third.getSymbol().equals("("))
					{
						newValue = (second.getValue());
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "-":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals(")") 
							&& second.getSymbol().equals("E")
							&& third.getSymbol().equals("("))
					{
						newValue = (second.getValue());
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "*":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals(")") 
							&& second.getSymbol().equals("E")
							&& third.getSymbol().equals("("))
					{
						newValue = (second.getValue());
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "/":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals(")") 
							&& second.getSymbol().equals("E")
							&& third.getSymbol().equals("("))
					{
						newValue = (second.getValue());
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case ")":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals(")") 
							&& second.getSymbol().equals("E")
							&& third.getSymbol().equals("("))
					{
						newValue = (second.getValue());
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				case "$":
					first = exprStack.pop();
					second = exprStack.pop();
					third = exprStack.pop();
					if(first.getSymbol().equals(")") 
							&& second.getSymbol().equals("E")
							&& third.getSymbol().equals("("))
					{
						newValue = (second.getValue());
						state = nonTerminalState("F");
						exprStack.push(new Item("F",Integer.toString(state),newValue));
					}
					else state = -1;
					break;
				default:
					state = -1;
					break;
				}
				break;
			default:
				state = -1;
				break;
			}
		}
		System.out.println("Invalid expression");//reached only if state = -1 and expression is invalid
		System.exit(0);
	}
}
