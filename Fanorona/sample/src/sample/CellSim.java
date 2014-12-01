package sample;


public class CellSim{

	public static void main(String[] args){
		
		//1st: make an array, read information from user:
			
			System.out.println("Enter n, size of n x n Grid:");
			int n = IO.readInt();
			char [][] tissueSample = new char [n][n];
			
			//1A: error check for size:
				
				if(n < 0){
					IO.reportBadInput();
				}
				
			System.out.println("Enter threshold:");
			int threshold = IO.readInt();
		
			//1B: error check for threshold:
			
				if(threshold <= 0 || threshold > 100){
					IO.reportBadInput();	
				}
			
			System.out.println("Enter maxRounds:");
			int maxRounds = IO.readInt();
			
			//1C: error check for maxRounds:
			
				if(maxRounds < 0){
					IO.reportBadInput();	
				}
			
			System.out.println("Enter frequency:");
			int frequency = IO.readInt();
			
			//1D: error check for frequency:
			
				if(frequency < 0 || frequency > maxRounds){
					IO.reportBadInput();
				}
			
			System.out.println("Enter percent of X:");
			int percentX = IO.readInt();
			
			//1E: error check for percentX:
			
				if(percentX < 0 || percentX > 100){
					IO.reportBadInput();
				}
				
			System.out.println("Enter percent of Blank:");
			int percentBlank = IO.readInt();
			
			//1E: error check for percentX:
			
				if(percentBlank < 0 || percentBlank > 100){
					IO.reportBadInput();
				}
			
		
		//2nd: assign the array, given array, percent of blank, and % of X cells:
		
			assignCellTypes(tissueSample, percentBlank, percentX);
		
		//3rd: print the randomized array:
		
			System.out.println("The inital board is:");
			printTissue(tissueSample);
		
		//4th: check if cell is satisfied:
		
		/*	//IO.outputBooleanAnswer(isSatisfied(tissueSample, 3,3, threshold));
			for(int row = 0; row < n; row++) {
			      for(int col = 0; col < tissueSample[row].length; col++) {
			    	  IO.outputBooleanAnswer(isSatisfied(tissueSample, row, col, threshold));
			      }
			}
			*/
		
		//5th: check if whole board is satisfied:
			
			System.out.println();
			System.out.println("Is the board satisfied?: ");
			IO.outputBooleanAnswer(boardSatisfied(tissueSample, threshold));
			
		//6th: move cells:
			
			int replaced = 0;
			int rounds = 1;
			double totalCells = 0;
			char[][] newTissue = newTissueSample(tissueSample,threshold);
			for(int i = 0; i<maxRounds; i++){
				
				if(boardSatisfied(newTissue, threshold)==false){
					
					moveAllUnsatisfied(newTissue,threshold);
					replaced += moveAllUnsatisfied(newTissue,threshold);
					rounds++;
					}
				else {
					replaced+=moveAllUnsatisfied(newTissue,threshold);
					System.out.println();
					System.out.println("The final board:");
					printTissue(newTissue);
					
					System.out.println("Total Rounds: ");
					IO.outputIntAnswer(rounds);
					
					System.out.println();
					System.out.println("The total nummber of cells moved:");
					IO.outputIntAnswer(replaced);
					break;
				}
				
				if(i%frequency==0 && boardSatisfied(tissueSample,threshold)==false){
					System.out.println();
					System.out.println("Round: ");
					IO.outputIntAnswer(rounds);
					printTissue(newTissue);
				}
			}
		
			for(int row = 0; row < n; row++) {
				      for(int col = 0; col < tissueSample[row].length; col++) {
				    	 if(isSatisfied(tissueSample, row, col, threshold) == true){
				    		 totalCells++;
				    	 }
				      }
				}
			
			if(boardSatisfied(tissueSample,threshold)==false){
				System.out.println();
				System.out.println("Gave Up");
				System.out.println("Total percent of Satisfied: ");
				System.out.println(totalCells);
				double percent = (totalCells / (double) (n*n)) * 100;
				IO.outputDoubleAnswer(percent);
				printTissue(tissueSample);
				System.out.println();
				System.out.println("The total nummber of cells moved:");
				IO.outputIntAnswer(replaced);
			}
	}
	
//2nd:
	
	public static void assignCellTypes(char[][] tissue, int percentBlank, int percentX){
	
		//1: error check: for correct %:
			
			if (percentBlank + percentX > 200 || percentBlank + percentX <= 0 || percentBlank == 0){
				IO.reportBadInput();
			}
			
		//2: find number of empty cells:
				
			int n = tissue.length;
			double numEmpty = (((double) percentBlank / (double) 100) * (n*n));
			int emptyCells = (int) Math.round(numEmpty);
			
		//3: assign cells, by number of empty, and alternating XO:
			 		
		/*	int evenORodd = 1;
			int notemptyAssign = 0;
			
			for(int row = 0; row < n; row++) {
			      for(int col = 0; col < tissue[row].length; col++) {
			    	  
			    	  if (notemptyAssign < (n*n) - emptyCells) {
			    		  if (evenORodd%2 == 0){
			    			  tissue[row][col] ='X';	  
			    		  }
			    		  else{
								tissue[row][col] = 'O';
							}
						}
			    	  else{
			    		  tissue[row][col] =  ' ';
			    		  }
			    	  evenORodd++;
			    	  notemptyAssign++;
			    	  }
			      System.out.println();
			      }
			*/
			
		//4: find number of X, O cells:
		
			double numOfX = (((double) percentX / (double) 100) * ((double)(n*n)- emptyCells));
			int xCells = (int) Math.ceil(numOfX);
			
			int oCells = ( (n * n) - xCells - emptyCells);
			
			
		
		//5: assign cells, randomly , by number of empty, number of X,:
			
			int y = 0;
			int z = 0;
			int f = 0;
			
			for(int i = 0; i <tissue.length; i++){
				for(int j = 0; j<tissue[i].length; j++){
					
					int x = (int) (Math.random()*3+1);
					
					if (x == 1  && z < xCells) {
						tissue[i][j] = 'X';
						z++;
					}
					else if (x==3  && y< emptyCells){
						tissue[i][j] = ' ';
						y++;
					}
					else if (x == 2 && f< oCells ){
							tissue[i][j] = 'O';
							}
					else {
						j--;
					}
					}
				}
				
	}
		
//3rd:
	
public static void printArray(int[][] tissue){
		
		//1: print the assigned array:
			for(int i = 0; i <tissue.length; i++) {
				for(int j = 0; j < tissue[i].length; j++) {
					System.out.print( tissue[i][j]);
					}
				System.out.println();
				}
			System.out.println("done");
	}

	
	public static void printTissue(char[][] tissue){
		
		//1: print the assigned array:
			for(int i = 0; i <tissue.length; i++) {
				for(int j = 0; j < tissue[i].length; j++) {
					System.out.print( tissue[i][j]);
					}
				System.out.println();
				}
			System.out.println("done");
	}

//4th:
	
	public static boolean isSatisfied(char[][] tissue, int row, int col, int threshold){
		
		//1: declare:
			double numOfSurrX = 0;
			double numOfSurrO = 0;
			
			int startR =row-1;
			int startC =col-1;
			int endR = row+1;
			int endC = col+1;
	  
		//2: set boundaries for corner cells:
			
			if(row == 0){
				startR = row;
			}
			else if (row == tissue.length-1){
				endR = tissue.length;
			}
			 
			if(col == 0){
				startC = col;
			}
			else if (col == tissue[0].length-1){
				endC = tissue[0].length;
			}
		
		//3: check surrounding cell to see if it an X, O, or empty, count:
			
			for(int i = startR; i < endR; i++){       
				for(int j = startC; j < endC; j++){	
					
					if (tissue[i][j]=='X') {			
						numOfSurrX++;			
					}
					else if (tissue[i][j]=='O') { 
						numOfSurrO++;			
					}
				 }
			}
			
		//4: check if counts, match with threshold:
			
			if (tissue[row][col]=='X'){		
				if (((numOfSurrX)/(numOfSurrO + numOfSurrX))*100 >= threshold){
					return true;	
				}
			}
			else if (tissue[row][col]=='O'){
				if((numOfSurrO/(numOfSurrO+numOfSurrX))*100>= threshold){
					return true;
				}
			}
		//5: return:
			if(tissue[row][col] == ' '){
				return true;
			}
			return false;
 }
 
//5th:

	public static boolean boardSatisfied(char[][] tissue, int threshold){
		
		//1: check each cell:
		
			for(int i =0; i < tissue.length; i++){           
				for(int j = 0; j < tissue[i].length; j++){	
					
					if(isSatisfied(tissue, i, j, threshold)==false && tissue[i][j] !=' '){
						return false;
					}
				}
			}
			
			return true;
	 }
	 
 /**
 * Given a tissue sample, move all unsatisfied agents to a vacant cell
 *
 * @param tissue a 2-D character array that has been initialized
 * @param threshold the percentage of like agents that must surround the agent to be satisfied
 * @return an integer representing how many cells were moved in this round
 **/
//6th:
	
	public static char[][] unSatisfied(char[][] tissue, int threshold){
 
		//1: find all unsatisfied, save into array:
			
			char [][] unSatisfied = new char[tissue.length][tissue.length];
			
			for(int i =0; i < tissue.length; i++){           
				for(int j = 0; j < tissue[i].length; j++){
					if (isSatisfied(tissue, i, j, threshold) == false){
						unSatisfied[i][j] = tissue[i][j];
					}
				}
			}
			
			return unSatisfied;
	}
	
	public static char[][] emptyCells(char[][] tissue, int threshold){
		 
		//1: find all empty cells, save into array:
		
			char [][] emptyCells = new char[tissue.length][tissue.length];
			
			for(int i =0; i < tissue.length; i++){           
				for(int j = 0; j < tissue[i].length; j++){
					if (tissue[i][j] == ' '){
				emptyCells[i][j] = tissue[i][j];
					}
				}
			}
			
			return emptyCells;
	}
			
	public static int moveAllUnsatisfied(char[][] tissue, int threshold){	
		
		//1: put unsatisfied into empty ones:
			
			char[][] emptyCells = emptyCells(tissue, threshold);
			char[][] unSatisfied = unSatisfied(tissue, threshold);
			int numMoved = 0;
		
			for(int q =0; q < tissue.length; q++){           
				for(int k = 0; k < tissue[q].length; k++){
					if(tissue[q][k] == ' '){
						//for every cell in tissue, if it space, check innerLoop(for every cell in unSatisfied, if it is x, o,):
						innerLoop:
						for(int w =0; w < unSatisfied.length; w++){           
							for(int e = 0; e < unSatisfied[w].length; e++){
								if(unSatisfied[w][e] == 'X' || unSatisfied[w][e] == 'O'){
									//for every cell in unSatisfied, if it is x, o, swap unSatisfied with space
									tissue[q][k] = tissue[w][e];
									tissue[w][e] = ' ';
									numMoved++;
									break innerLoop;
									//break innerLoop so it doesnt continue swaping for that empty that has been already swaped
								}	
							}
						}
						
							
						
				}
			}
				
			/**for(int y =0; y < tissue.length; y++){           
				for(int h = 0; h < tissue[y].length; h++){
					if(emptyCells[y][h] == 'X' || emptyCells[y][h] == 'O'){
						tissue[y][h] = emptyCells[y][h];
					}
					if(unSatisfied[y][h] == ' '){
						tissue[y][h] = unSatisfied[y][h];
					}
				}
				
				/**emptyCells = unSatisfied(tissue, threshold);
				unSatisfied = emptyCells(tissue, threshold);
				System.out.println("UGH");
				printTissue(tissue);
				System.out.println("UGH");**/
			
		
			
			/**System.out.println("UGH");
			printTissue(tissue);
			System.out.println("UGH");
			**/
			//emptyCells = unSatisfied(tissue, threshold);
			//unSatisfied = emptyCells(tissue, threshold);
			}	
			return numMoved;
		}
//7th: Change tissueSample to new sample:
	
public static char[][] newTissueSample(char[][] tissue, int threshold){	
		
		
			
			char[][] emptyCells = emptyCells(tissue, threshold);
			char[][] unSatisfied = unSatisfied(tissue, threshold);
			int numMoved = 0;
		
			for(int q =0; q < tissue.length; q++){           
				for(int k = 0; k < tissue[q].length; k++){
					if(tissue[q][k] == ' '){
						innerLoop:
						for(int w =0; w < unSatisfied.length; w++){           
							for(int e = 0; e < unSatisfied[w].length; e++){
								if(unSatisfied[w][e] == 'X' || unSatisfied[w][e] == 'O'){
									tissue[q][k] = tissue[w][e];
									tissue[w][e] = ' ';
									numMoved++;
									break innerLoop;
								}	
							}
						}
					}
				}
			}
			return tissue;

		}
			
}
