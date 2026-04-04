public class NQueens{
	//05061219,06071219,07081219,"01091219",07150226,06040426.
	private static int counter = 0;
	private static final int SIZE = 8;

	private static void print(int[] vector){
		for(int i = 0; i < vector.length; i++){
			System.out.print((vector[i] + 1) + ((i < vector.length - 1) ? ", " : ""));
		}
		System.out.print("\n");
	}

	private static void print(int[] vector, int counter){
		for(int i = 0; i < vector.length; i++){
			System.out.println(counter + ") Row " + (i + 1) + ": Column " + (vector[i] + 1));
		}
	}

	private static void printSolution(int[] vector){
		System.out.print("\n");
		for(int i = 0; i < vector.length; i++){
			for(int j = 0; j < vector.length; j++){
				System.out.print((vector[i] == j) ? "#" : "*");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}

	private static boolean checkBishop(int[] vector){
		int x, y;
		for(int i = 0; i < vector.length; i++){
			for(int j = i + 1; j < vector.length; j++){
				y = vector[i] - vector[j];
				x = i - j;
				if(x*x == y*y){
					return false;
				}
			}
		}
		return true;
	}

	private static int[] flipRow(int[] row){
		int[] v = new int[SIZE];
		for(int i = 0; i < SIZE; i++){
			v[i] = row[SIZE - i - 1];
		}
		return v;
	}

	private static int[] flipColumn(int[] row){
		int[] v = new int[SIZE];
		for(int i = 0; i < SIZE; i++){
			v[i] = SIZE - 1 - row[i];
		}
		return v;
	}

	private static int[] rotate(int[] row){
		int[] v = new int[SIZE];
		for(int i = 0; i < SIZE; i++){
			v[row[i]] = i;
		}
		return v;
	}

	private static int compare(int[] vector1, int[] vector2){
		int greater = 0;
		int index = 0;
		boolean flag = true;
		while (flag){
			greater = (vector1[index] == vector2[index]) ? 0 : ((vector1[index] > vector2[index]) ? 1 : 2);
			flag = (greater == 0);
			flag = (index > SIZE - 2) ? false : flag;
			index += (flag && (index < SIZE - 1)) ? 1 : 0;
		}
		return greater;
	}

	private static boolean check(int[] vector){
		return (
				(compare(vector, flipRow(vector)) == 1) ||
				(compare(vector, flipColumn(vector)) == 1) ||
				(compare(vector, flipRow(flipColumn(vector))) == 1) ||
				(compare(vector, rotate(vector)) == 1) ||
				(compare(vector, rotate(flipRow(vector))) == 1) ||
				(compare(vector, rotate(flipColumn(vector))) == 1) ||
				(compare(vector, rotate(flipRow(flipColumn(vector)))) == 1)
				) ? false : true;
	}

	private static void solveRecursive(int[] column, boolean[] available, int index){
		for(column[index] = 0; column[index] < SIZE; column[index]++){
			if(!available[column[index]]){
				continue;
			}
			available[column[index]] = false;
			if(index < SIZE - 1){
				solveRecursive(column, available, index + 1);
			}else{
				if(checkBishop(column)){
					if(check(column)){
						counter++;
						print(column, counter);
						printSolution(column);
					}
				}
			}
			available[column[index]] = true;
		}
	}

	private static void solveNQueens(){
		int[] column = new int[SIZE];
		boolean[] available = new boolean[SIZE];
		for(int i = 0; i < SIZE; i++){
			available[i] = true;
		}
		solveRecursive(column, available, 0);
	}

	public static void main(String[] args){
		int vector[] = {0, 1, 2, 3, 4, 5, 6, 7};
		if(vector.length != 8) {
			print(vector);
		}
		solveNQueens();
	}
}