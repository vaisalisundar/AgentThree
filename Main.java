package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws NullPointerException {

        List<Double> pData = new ArrayList<>();
        List<Integer> noOfBumps = new ArrayList<>();
        List<Integer> cellsProcessed = new ArrayList<>();
        List<Integer> cellsProcessedRFA = new ArrayList<>();
        List<Boolean> solvabilty = new ArrayList<>();
        List<Integer> trajectoryLen = new ArrayList<>();
        List<Float> trajectoryDivideLength = new ArrayList<>();
        for (int l = 0; l < 60; l++) {
            int dim = 101;
            int values[][] = new int[dim][dim];
            int dummyValues[][] = new int[dim][dim];

//            double p =  Math.random();
//            while(p>0.33){
//                p = Math.random();
//            }
            double p = 0.322751285406706;

            //creating a dummy grid with no blocked cells
            for (int rn = 0; rn < dummyValues.length; rn++) {
                for (int cl = 0; cl < dummyValues[rn].length; cl++) {
                    dummyValues[rn][cl] = 0;
                    System.out.print(dummyValues[rn][cl]);
                }
                System.out.println();
            }
            System.out.println("Done printing the dummy grid");

            for (int i = 0; i < values.length; i++) {
                // do the for in the row according to the column size
                for (int j = 0; j < values[i].length; j++) {

                    if (i == 0 && j == 0 || i == dim - 1 && j == dim - 1) {
                        values[i][j] = 0;
                    } else {
                        if (Math.random() >= p) {
                            values[i][j] = 0;
                        } else {
                            values[i][j] = 1;
                        }
                    }

                    System.out.print(values[i][j]);
                }
                // add a new line
                System.out.println();
            }
            System.out.println("Done Printing the actual grid");

            Solution sol = new Solution();
            //grid is the actual maze
            int[][] grid = values;
           // int[][] grid ={{0,0,1,0},{0,0,0,0},{1,1,0,1},{0,0,0,0}};
           // int[][] grid ={{0,1,1,1,0},{0,0,0,1,1},{1,1,0,0,0},{0,0,0,0,0},{0,1,1,0,0}};


            //newGrid is the maze with no blockage
            int[][] newGrid = dummyValues;


            int length = sol.shortestPathBinaryMatrix(grid);
            System.out.println(length);
            System.out.println();

            if (length != -1) {
                // float ans = sol.RFAstarTest(newGrid,0,0,1,grid);
                int ans = sol.AgentTwo(newGrid, 0, 0, 1, grid);
                System.out.println(ans);
                pData.add(p);
                //float result = (float)ans/(float)length;
                // System.out.println(ans);
                // trajectoryLen.add(ans);
                int cells = sol.noOfCellsProcessed;
                noOfBumps.add(ans);
                cellsProcessed.add(cells);
                // trajectoryDivideLength.add(ans);
            }


            //System.out.println(cells);

            //No.of.cells processed in RFA
            //int cellsRFA = sol.cellsProcessedRFA()-1;

            //pData.add(p);
            //lengthData.add(length);

            //cellsProcessedRFA.add(cellsRFA);


        }
         FileWriter locFile1 = null;
        FileWriter locFile2 = null;
        //     FileWriter locFile3 = null;
//        FileWriter locFile4 = null;
        //       FileWriter locFile5 = null;
        // FileWriter locFile6 = null;


       try{
            locFile1 = new FileWriter("noOfBumps.txt");
//            locFile2 = new FileWriter("lengthData.txt");
            locFile2 = new FileWriter("cellsProcessed.txt");
//            locFile4 = new FileWriter("solvability.txt");
//            locFile5 = new FileWriter("cellProcessedRFA.txt");
            // locFile6 = new FileWriter("trajectoryDivideLen.txt");


            for(int i = 0;i<pData.size();i++){
                locFile1.write(noOfBumps.get(i)+"\n");
               locFile2.write(cellsProcessed.get(i)+"\n");
         //      locFile3.write(cellsProcessed.get(i)+"\n");
//                locFile4.write(solvabilty.get(i)+"\n");
//                locFile5.write(cellsProcessedRFA.get(i)+"\n");
             //   locFile6.write(trajectoryDivideLength.get(i)+"\n");
            }
          //  locFile6.close();
//            locFile5.close();
//            locFile4.close();
//            locFile3.close();
          locFile2.close();
          locFile1.close();
        }catch (IOException e){
            System.out.println("In catch block");
            e.printStackTrace();
        }


    }

//    public static int[][] newMaze(){
//        Scanner scanner = new Scanner(System.in);
//        int[][] maze = new int[4][4];
//        for(int i =0;i<4;i++){
//            for (int j =0;j<4;j++){
//                maze[i][j] = scanner.nextInt();
//            }
//        }
//        for(int i =0;i<4;i++){
//            for (int j =0;j<4;j++){
//                System.out.println(maze[i][j]);
//            }
//            System.out.println();
//        }
//        return maze;
//
//    }

    }


class Solution {
    Map<List<Integer>, List<Integer>> pathMapRFA = new HashMap<List<Integer>, List<Integer>>();
    List<Candidate> track = new ArrayList<Candidate>();
    //Map<Integer,List<Candidate>> inferenceKnowledge = new HashMap<Integer,List<Candidate>();
    List<Equation> inferenceKnowledge = new ArrayList<Equation>();

    int noOfCellsProcessed = 0;

    boolean[][] visitedRFA = new boolean[8][8];

    //Queue<Candidate> pqRFA = new PriorityQueue<>((a, b) -> a.totalEstimate - b.totalEstimate);
    // Candidate represents a possible option for travelling to the cell
    // at (row, col).
    class Candidate {

        public int row;
        public int col;
        public int distanceSoFar;
        public int totalEstimate;


        public Candidate(int row, int col, int distanceSoFar, int totalEstimate) {
            this.row = row;
            this.col = col;
            this.distanceSoFar = distanceSoFar;
            this.totalEstimate = totalEstimate;
        }


    }

    class Equation {

        public List<Candidate> candidateList;
        public int c;



        public Equation(List<Candidate> candidateList , int c) {
            this.candidateList = candidateList;
            this.c = c;
            //inferenceKnowledge.put(c,candidateList);


        }


    }


    private static final int[][] directions =
            new int[][]{{-1, 0}, {0, -1}, {0, 1}, {1, 0}};

    //Covering diagonal neighbors -Agent-3
    private static final int[][] directionsAgent3 =
            new int[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

    public int shortestPathBinaryMatrix(int[][] grid) {

        // Firstly, we need to check that the start and target cells are open.
        if (grid[0][0] != 0 || grid[grid.length - 1][grid[0].length - 1] != 0) {
            return -1;
        }

        // Set up the A* search.
        Queue<Candidate> pq = new PriorityQueue<>((a, b) -> a.totalEstimate - b.totalEstimate);
        pq.add(new Candidate(0, 0, 1, estimate(0, 0, grid)));

        boolean[][] visited = new boolean[grid.length][grid[0].length];
        Map<List<Integer>, List<Integer>> pathMap = new HashMap<List<Integer>, List<Integer>>();


        // Carry out the A* search.
        while (!pq.isEmpty()) {

            Candidate best = pq.remove();
           // int counter = cellsProcessed();


            // Is this for the target cell?
            if (best.row == grid.length - 1 && best.col == grid[0].length - 1) {

                List<Integer> goalnode = new ArrayList<>();
                goalnode.add(best.row);
                goalnode.add(best.col);
                System.out.print(goalnode);
                System.out.print("->");
                List<Integer> result = pathMap.get(goalnode);
                for (int i = 0; i < best.distanceSoFar; i++) {
                    System.out.print(result);
                    System.out.print("->");
                    result = pathMap.get(result);

                }

                return best.distanceSoFar;
            }

            // Have we already found the best path to this cell?
            if (visited[best.row][best.col]) {
                continue;
            }

            visited[best.row][best.col] = true;
            // pathQueue.add({new int[]{best.row,best.col},{});

            for (int[] neighbour : getNeighbours(best.row, best.col, grid)) {
                int neighbourRow = neighbour[0];
                int neighbourCol = neighbour[1];

                // This check isn't necessary for correctness, but it greatly
                // improves performance.
                if (visited[neighbourRow][neighbourCol]) {
                    continue;
                }

                // Otherwise, we need to create a Candidate object for the option
                // of going to neighbor through the current cell.
                int newDistance = best.distanceSoFar + 1;
                int totalEstimate = newDistance + estimate(neighbourRow, neighbourCol, grid);
                Candidate candidate =
                        new Candidate(neighbourRow, neighbourCol, newDistance, totalEstimate);
                pq.add(candidate);
                List<Integer> neigh = new ArrayList<>();
                neigh.add(neighbourRow);
                neigh.add(neighbourCol);
                List<Integer> parent = new ArrayList<>();
                parent.add(best.row);
                parent.add(best.col);
                if (pathMap.containsKey(neigh)) {
                    continue;
                } else {
                    pathMap.put(neigh, parent);
                }


            }
        }
        // The target was unreachable.
        return -1;
    }

    private List<int[]> getNeighbours(int row, int col, int[][] grid) {
        List<int[]> neighbours = new ArrayList<>();
        for (int i = 0; i < directions.length; i++) {
            int newRow = row + directions[i][0];
            int newCol = col + directions[i][1];
            if (newRow < 0 || newCol < 0 || newRow >= grid.length
                    || newCol >= grid[0].length
                    || grid[newRow][newCol] != 0) {
                continue;
            }
            neighbours.add(new int[]{newRow, newCol});
        }
        return neighbours;
    }

    //GetNeighbors -Agent3
    private List<int[]> getNeighboursAgent3(int row, int col, int[][] grid) {
        List<int[]> neighbours = new ArrayList<>();
        for (int i = 0; i < directionsAgent3.length; i++) {
            int newRow = row + directionsAgent3[i][0];
            int newCol = col + directionsAgent3[i][1];
            if (newRow < 0 || newCol < 0 || newRow >= grid.length
                    || newCol >= grid[0].length) {
                continue;
            }
            neighbours.add(new int[]{newRow, newCol});
        }
        return neighbours;
    }

    // Get the best case estimate of how much further it is to the bottom-right cell.
    private int estimate(int row, int col, int[][] grid) {
        //Manhattan distance
        int heuristic = Math.abs(row - (grid.length - 1)) + Math.abs(col - (grid[0].length - 1));
        return heuristic;
    }

    //update inference knowledge based on any change in KB
    private void updateInferenceKB(Candidate candidate,int value){
        for(int i=0;i< inferenceKnowledge.size();i++){
            Equation eq = inferenceKnowledge.get(i);
            for(int j=0;j<eq.candidateList.size();j++){
                Candidate candidate1 = eq.candidateList.get(j);
                if(candidate1.row== candidate.row && candidate.col== candidate1.col){
                    if(value == 0){
                        eq.candidateList.remove(j);
                        if(eq.candidateList.size()==0){
                            inferenceKnowledge.remove(eq);
                        }
                    }else{
                        eq.candidateList.remove(j);
                        eq.c = eq.c-1;
                        if(eq.candidateList.size()==0){
                            inferenceKnowledge.remove(eq);
                        }
                    }
                }
            }


        }
    }

    //infer values from updateInferenceKB
    private int inferFromInferenceKB(int[][] newGrid,boolean[][] visited){
        int counter = 0;
        for(int i=0;i<inferenceKnowledge.size();i++){
            Equation eq = inferenceKnowledge.get(i);
           // if((eq.c==2 && eq.candidateList.size()==2) ||(eq.c==1 && eq.candidateList.size()==1) ){
            if(eq.c==eq.candidateList.size()){
                for(int j=0;j<eq.candidateList.size();j++){
                    Candidate candidate = eq.candidateList.get(j);
                    newGrid[candidate.row][candidate.col] = 1;
                    visited[candidate.row][candidate.col]=true;
                }
                inferenceKnowledge.remove(eq);
                counter++;
            }
            if(eq.c==0){
                for(int j=0;j<eq.candidateList.size();j++){
                    Candidate candidate = eq.candidateList.get(j);
                    newGrid[candidate.row][candidate.col] = 0;
                    visited[candidate.row][candidate.col]=true;

                }
                inferenceKnowledge.remove(eq);
            }

        }
        return counter;
    }

    private boolean trackPathHasAnyBlock(int[][] newGrid,int i,boolean[][] visited) {
        boolean hasBlock = false;
        for (int j = 0; j < track.size(); j++) {
            Candidate can = track.get(j);
            if (newGrid[can.row][can.col] == 1) {
                hasBlock = true;
                Candidate newSource;
                Candidate can1 = track.get(j-1);
                if(visited[can1.row][can1.col]==true){
                    newSource = track.get(j-1);
                }else{
                    newSource = track.get(i);
                }

                int ans1 = RFAstar(newGrid, newSource.row, newSource.col, 0);
                Collections.reverse(track);
                //counter = ans1+counter;
                // i=-1;
            }
        }
       return hasBlock;
    }


    public static int cellsProcessed = 0;

//    public int cellsProcessed() {
//        return cellsProcessed++;
//    }

    public static int cellsProcessedRFA = 0;

//    public int cellsProcessedRFA() {
//        return cellsProcessedRFA++;
//    }


    public int RFAstar(int[][] newGrid, int row, int col, int distanceSoFar) {
        int counter = 0;

        // Firstly, we need to check that the start and target cells are open.
        if (newGrid[0][0] != 0 || newGrid[newGrid.length - 1][newGrid[0].length - 1] != 0) {
            return -1;
        }

        // Set up the A* search.
        Queue<Candidate> pq = new PriorityQueue<>((a, b) -> a.totalEstimate - b.totalEstimate);
        pq.add(new Candidate(row, col, distanceSoFar, estimate(0, 0, newGrid)));

        boolean[][] visited = new boolean[newGrid.length][newGrid[0].length];
        Map<Candidate, Candidate> pathMap = new HashMap<Candidate, Candidate>();


        // Carry out the A* search.
        while (!pq.isEmpty()) {

            Candidate best = pq.remove();
            noOfCellsProcessed++;
             counter++;


            // Is this for the target cell?
            if (best.row == newGrid.length - 1 && best.col == newGrid[0].length - 1) {

                if (track.isEmpty()) {
                    Candidate result = pathMap.get(best);
                    System.out.print(best.row+","+best.col);
                    System.out.print("->");
                    track.add(best);
                    Candidate source = new Candidate(row,col,distanceSoFar,estimate(row,col,newGrid));
                   // for (int i = 0; i < best.distanceSoFar - 1; i++) {
                    while (result.row!=source.row || result.col!=source.col){
                        System.out.print(result.row + "," + result.col);
                        track.add(result);
                        System.out.print("->");
                        result = pathMap.get(result);

                    }
                    System.out.println(source.row+","+ source.col);
                    track.add(source);

                } else {
                    track.clear();
                    Candidate result = pathMap.get(best);
                    System.out.print(best.row+","+ best.col);
                    System.out.print("->");
                    track.add(best);
                    Candidate source = new Candidate(row,col,distanceSoFar,estimate(row,col,newGrid));
       //             for (int i = 0; i <7; i++) {
                    while(result.row!=source.row || result.col!=source.col){
                        System.out.print(result.row + "," + result.col);
                        track.add(result);
                        System.out.print("->");
                        result = pathMap.get(result);

                    }
                    track.add(source);
                    //System.out.println(source.row+","+ source.col);
                    System.out.println();

                }
               // return best.distanceSoFar;
                return counter;
            }

                // Have we already found the best path to this cell?
                if (visited[best.row][best.col]) {
                    continue;
                }

                visited[best.row][best.col] = true;
                // pathQueue.add({new int[]{best.row,best.col},{});

                for (int[] neighbour : getNeighbours(best.row, best.col, newGrid)) {
                    int neighbourRow = neighbour[0];
                    int neighbourCol = neighbour[1];

                    // This check isn't necessary for correctness, but it greatly
                    // improves performance.
                    if (visited[neighbourRow][neighbourCol]) {
                        continue;
                    }

                    // Otherwise, we need to create a Candidate object for the option
                    // of going to neighbor through the current cell.
                    int newDistance = best.distanceSoFar + 1;
                    int totalEstimate = newDistance + estimate(neighbourRow, neighbourCol, newGrid);
                    Candidate candidate =
                            new Candidate(neighbourRow, neighbourCol, newDistance, totalEstimate);
                    pq.add(candidate);

                    if (!pathMap.containsKey(candidate)) {
                        pathMap.put(candidate, best);
                    }
                   // pathMap.put(candidate, best);



                }
            }
        // The target was unreachable.
        return -1;
        }

    //AgentTwo - blindfolded
    public int AgentTwo(int[][] newGrid, int row, int col, int distanceSoFar, int[][] grid) {
        int noOfBumps = 0;
        int totalCounter=0;
        int trajectoryLen = 0;
        int ans = RFAstar(newGrid, row, col, distanceSoFar);
        totalCounter =  ans+totalCounter;
        Collections.reverse(track);

        for (int i = 0; i < track.size(); i++) {
            Candidate canValue = track.get(i);
            if (newGrid[canValue.row][canValue.col] != grid[canValue.row][canValue.col]) {
                newGrid[canValue.row][canValue.col] = grid[canValue.row][canValue.col];

                trajectoryLen++;
                trajectoryLen++;
                Candidate newSource = track.get(i - 1);
                noOfBumps++;
                int ans1 = RFAstar(newGrid, newSource.row, newSource.col, 0);
                totalCounter = ans1+totalCounter;
                 Collections.reverse(track);
                 i = -1;
            }else{
                trajectoryLen++;
            }

        }
        int ansLength = RFAstar(newGrid, 0, 0, 1);
        //return trajectoryLen;
        //float result = (float) trajectoryLen/(float)ansLength;
        return noOfBumps;
        //return totalCounter;
       // return -1;
    }

    //AgentOne - can see its four neighbors
    public int AgentOne(int[][] newGrid, int row, int col, int distanceSoFar, int[][] grid) {
        int noOfBumps = 0;
        int totalCounter = 0;
        int trajectoryLen = 0;
        int ans = RFAstar(newGrid, row, col, distanceSoFar);
        totalCounter +=ans;
        Collections.reverse(track);
        boolean[][] visited = new boolean[newGrid.length][newGrid[0].length];
        //int ans=0;
        for (int i = 0; i < track.size(); i++) {
            Candidate canValue = track.get(i);
            for (int[] neighbour : getNeighbours(canValue.row, canValue.col, newGrid)){
                int neighbourRow = neighbour[0];
                int neighbourCol = neighbour[1];
                if(newGrid[neighbourRow][neighbourCol]!=grid[neighbourRow][neighbourCol]){
                    newGrid[neighbourRow][neighbourCol]=grid[neighbourRow][neighbourCol];
                    visited[neighbourRow][neighbourCol]=true;
                }
            }
            if (newGrid[canValue.row][canValue.col] != grid[canValue.row][canValue.col] || visited[canValue.row][canValue.col]==true) {
               // newGrid[canValue.row][canValue.col] = grid[canValue.row][canValue.col];

                Candidate newSource = track.get(i - 1);
                trajectoryLen++;
                trajectoryLen++;
                noOfBumps++;
                int ans1 = RFAstar(newGrid, newSource.row, newSource.col, 1);
                totalCounter+=ans1;
                Collections.reverse(track);
                i = -1;

            }else{
                trajectoryLen++;
            }

        }

        int ansLength = RFAstar(newGrid, 0, 0, 1);
       // System.out.println("TracjectorLen="+trajectoryLen);
       // System.out.println("SPL"+ansLength);
       // float result = (float) trajectoryLen/(float)ansLength;
       // return ansLength;
        return noOfBumps;
        //return result;

        // return -1;
    }

    //AgentThree - Eg inference Agent
    public int AgentThree(int[][] newGrid, int row, int col, int distanceSoFar, int[][] grid){
        int noOfBumps=0;
        int counter = 0;
        int ans = RFAstar(newGrid, row, col, distanceSoFar);
        counter  = ans+counter;
        int c;
        int b;
        int n;
        int e;
        int h;
        Collections.reverse(track);
        boolean[][] visited = new boolean[newGrid.length][newGrid[0].length];
        for (int i = 0; i < track.size(); i++) {
            c=0;
            b=0;
            e=0;
            h=0;
            Candidate canValue = track.get(i);
            //visited[canValue.row][canValue.col]=true;
            if (newGrid[canValue.row][canValue.col] != grid[canValue.row][canValue.col] && i!=-1) {
                newGrid[canValue.row][canValue.col] = grid[canValue.row][canValue.col];
                visited[canValue.row][canValue.col]=true;
                Candidate newSource = track.get(i - 1);
                noOfBumps++;
                int ans1 = RFAstar(newGrid, newSource.row, newSource.col, 0);
                counter = ans1+counter;
                Collections.reverse(track);
                i = -1;
            }else if(i!=-1) {
                visited[canValue.row][canValue.col] = true;


                n = getNeighboursAgent3(canValue.row, canValue.col, newGrid).size();
                h = n;
                for (int[] neighbour : getNeighboursAgent3(canValue.row, canValue.col, newGrid)) {

                    int neighbourRow = neighbour[0];
                    int neighbourCol = neighbour[1];
                    if (newGrid[neighbourRow][neighbourCol] != grid[neighbourRow][neighbourCol] || newGrid[neighbourRow][neighbourCol] == 1) {
                        c++;
                    }
                    if (newGrid[neighbourRow][neighbourCol] == 1) {
                        b++;
                        h--;
                    }
                    if (newGrid[neighbourRow][neighbourCol] == 0 && visited[neighbourRow][neighbourCol] == true) {
                        e++;
                        h--;
                    }
                }
                //Cx=Bx:  all remaining hidden neighbors of x are empty.
                if (c == b) {
                    e = h;
                    h = 0;
                    for (int[] neighbour : getNeighboursAgent3(canValue.row, canValue.col, newGrid)) {
                        int neighbourRow = neighbour[0];
                        int neighbourCol = neighbour[1];
                        if (newGrid[neighbourRow][neighbourCol] == 0 && visited[neighbourRow][neighbourCol] == false) {
                            visited[neighbourRow][neighbourCol] = true;
                        }
                    }
                }
                //Nx−Cx=Ex:  all remaining hidden neighbors of x are blocked.
                if (n - c == e && h != 0) {
                    b = h;
                    h = 0;
                    for (int[] neighbour : getNeighboursAgent3(canValue.row, canValue.col, newGrid)) {
                        int neighbourRow = neighbour[0];
                        int neighbourCol = neighbour[1];
                        if (newGrid[neighbourRow][neighbourCol] == 0 && visited[neighbourRow][neighbourCol] == false) {
                            visited[neighbourRow][neighbourCol] = true;
                            newGrid[neighbourRow][neighbourCol] = 1;

                        }
                    }
                    for (int j = 0; j < track.size(); j++) {
                        Candidate can = track.get(j);
                        if (newGrid[can.row][can.col] == 1) {
                            Candidate newSource = track.get(i);
                            int ans1 = RFAstar(newGrid, newSource.row, newSource.col, 0);
                            Collections.reverse(track);
                            counter = ans1 + counter;
                            i = -1;
                        }
                    }
                }
                //Hx= 0:  nothing remains to be inferred about cellx.



            }

        }
        int ansLength = RFAstar(newGrid, 0, 0, 1);

        return noOfBumps;


    }

    //AgentFour - our invention
    public int AgentFour(int[][] newGrid, int row, int col, int distanceSoFar, int[][] grid){
        int noOfBumps=0;
        int counter = 0;
        int ans = RFAstar(newGrid, row, col, distanceSoFar);
        counter  = ans+counter;
        int c;
        int b;
        int n;
        int e;
        int h;
        int cx;
        Collections.reverse(track);
        boolean[][] visited = new boolean[newGrid.length][newGrid[0].length];
        for (int i = 0; i < track.size(); i++) {
            c=0;
            b=0;
            e=0;
            h=0;
            cx=0;
            Candidate canValue = track.get(i);
            //visited[canValue.row][canValue.col]=true;
            if (newGrid[canValue.row][canValue.col] != grid[canValue.row][canValue.col]) {

                newGrid[canValue.row][canValue.col] = grid[canValue.row][canValue.col];
                if(!inferenceKnowledge.isEmpty()){
                    updateInferenceKB(new Candidate(canValue.row, canValue.col, 0,0),1);
                    inferFromInferenceKB(newGrid,visited);
                }
                visited[canValue.row][canValue.col]=true;
                Candidate newSource = track.get(i - 1);
                noOfBumps++;
                int ans1 = RFAstar(newGrid, newSource.row, newSource.col, 0);
                counter = ans1+counter;
                Collections.reverse(track);
                i = -1;
            }else {
                visited[canValue.row][canValue.col] = true;

                n = getNeighboursAgent3(canValue.row, canValue.col, newGrid).size();
                h = n;
                List<Candidate> neighbourList = new ArrayList<Candidate>();
                for (int[] neighbour : getNeighboursAgent3(canValue.row, canValue.col, newGrid)) {

                    int neighbourRow = neighbour[0];
                    int neighbourCol = neighbour[1];
                    if (newGrid[neighbourRow][neighbourCol] != grid[neighbourRow][neighbourCol] || newGrid[neighbourRow][neighbourCol] == 1) {
                        c++;
                        cx++;
                    }
                    if (newGrid[neighbourRow][neighbourCol] == 1) {
                        b++;
                        h--;
                        cx--;
                    }
                    if (newGrid[neighbourRow][neighbourCol] == 0 && visited[neighbourRow][neighbourCol] == true) {
                        e++;
                        h--;
                    }

                    if(visited[neighbourRow][neighbourCol]==false){
                        neighbourList.add(new Candidate(neighbourRow, neighbourCol, 0, 0));

                    }

                }
                if(cx!=0) {
                    int inc=0;
                    Equation equation = new Equation(neighbourList, cx);
                    if(inferenceKnowledge.isEmpty()){
                        inferenceKnowledge.add(equation);
                    }else{
                        for(int k=0;k<inferenceKnowledge.size();k++) {
                            Equation other = inferenceKnowledge.get(k);
                            for(int l=0;l<other.candidateList.size();l++){
                                Candidate candidate = other.candidateList.get(l);
                                for(int m=0;m<equation.candidateList.size();m++){
                                    Candidate candidate1 = equation.candidateList.get(m);
                                    if(candidate.row== candidate1.row && candidate1.col== candidate.col){
                                        inc++;
                                    }
                                }
                            }

                        }
                        if(inc==0){
                            inferenceKnowledge.add(equation);
                        }
                    }

                }
                //Cx=Bx:  all remaining hidden neighbors of x are empty.
                if (c == b) {
                    e = h;
                    h = 0;
                    for (int[] neighbour : getNeighboursAgent3(canValue.row, canValue.col, newGrid)) {
                        int neighbourRow = neighbour[0];
                        int neighbourCol = neighbour[1];
                        if (newGrid[neighbourRow][neighbourCol] == 0 && visited[neighbourRow][neighbourCol] == false) {
                            visited[neighbourRow][neighbourCol] = true;
                            updateInferenceKB(new Candidate(neighbourRow, neighbourCol, 0, 0), 0);
                            int change = inferFromInferenceKB(newGrid,visited);
                            if (change != 0) {
                                boolean hasBlocks = trackPathHasAnyBlock(newGrid, i,visited);
                                if (hasBlocks == true) {
                                    i = -1;
                                }

                            }
                        }
                    }
                }

                //Nx−Cx=Ex:  all remaining hidden neighbors of x are blocked.
                if (n - c == e && h != 0 && i!=-1) {
                    b = h;
                    h = 0;
                    for (int[] neighbour : getNeighboursAgent3(canValue.row, canValue.col, newGrid)) {
                        int neighbourRow = neighbour[0];
                        int neighbourCol = neighbour[1];
                        if (newGrid[neighbourRow][neighbourCol] == 0 && visited[neighbourRow][neighbourCol] == false) {
                            visited[neighbourRow][neighbourCol] = true;
                            newGrid[neighbourRow][neighbourCol] = 1;
                            updateInferenceKB(new Candidate(neighbourRow, neighbourCol, 0, 0), 1);
                            int change = inferFromInferenceKB(newGrid,visited);
                            if (change != 0) {
                                boolean hasBlocks = trackPathHasAnyBlock(newGrid, i,visited);
                                if (hasBlocks == true) {
                                    i = -1;
                                }

                            }
                        }
                    }
                    if (i != -1) {
                        boolean hasBlocks = trackPathHasAnyBlock(newGrid, i,visited);
                        if (hasBlocks == true) {
                            i = -1;
                        }
                    }
                }
                //Hx= 0:  nothing remains to be inferred about cellx.

            }
        }
        int ansLength = RFAstar(newGrid, 0, 0, 1);

        return noOfBumps;


    }
}