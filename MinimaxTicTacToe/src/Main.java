import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {

    String[] board = new String[36];
    List<Integer[]> winning_combinationsList = new ArrayList<>();
    String player1 = "X";
    String player2 = "O";
//    String otherPlayer = " ";
    String currentPlayer = "X";
    double GValue;
    int mMove = -1;
    int countPerMove = 0;

    public static void main(String[] args) {
        System.out.println("Minimax Tic Tac Toe");
        Main main = new Main();
//        main.print_board(main.board);

        // loop to run the tournament. Comment out the following lines when you don't want to run the tournament
        for (int i=1;i<21;i++) {
            main.init();
            main.currentPlayer = main.player2;
            System.out.println("Game "+i);
            main.engageAlgorithm();
        }

        // uncomment this line to run simple algo - and uncomment the code in the engageAlgorithm function
        main.engageAlgorithm();

    }

    void engageAlgorithm() {

//        print_board(board);
        while (!isTerminalState(board)) {
//                System.out.println(main.currentPlayer + "'s turn");

            // trigger minimax to get the move and make the move
            int move = minimax(board, currentPlayer, 2);
            makeMove(board, currentPlayer, move);

            // print move
//            main.print_board(main.board);

            //reset count
//            System.out.println("The number of search nodes generated for this move is : "+main.countPerMove);

            countPerMove = 0;
            currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
//            System.out.println("--------------------------------\n");
        }
        if (checkForWinner(board, player1)) {
            System.out.println("Result: "+player1 + " wins !!!!");
        } else if (checkForWinner(board, player2)) {
            System.out.println("Result: "+player2 + " wins !!!!");
        } else {
            System.out.println("Result: It's a tie");
        }
    }

    int minimax(String[] state, String player, int depth) {
        if (player.equalsIgnoreCase(player1)) {
            maximize_value(state, player, depth);
        } else if (player.equalsIgnoreCase(player2)) {
            minimize_value(state,player, depth);
        }
        int move = mMove;
        mMove = -1;
        return move;
    }

    double maximize_value(String[] state, String player, int depth) {
        if (depth != 2) {
            player = player.equalsIgnoreCase(player1)? player2: player1;
        }
        if (isTerminalState(state)) return getUtility(state,player);
        double bestValue = Double.NEGATIVE_INFINITY;
        if (depth != 0) {
            List<Integer> availableMoves = getAvailableMoves(state);
            if (availableMoves.size() == 36) {
                Random random = new Random();
                mMove = random.nextInt(36);
                return 0;
            } else {
                for (Integer move : availableMoves) {
                    countPerMove++;
                    String[] simulatedState = makeMove(state, player, move);
                    double value = minimize_value(simulatedState, player, depth - 1);
                    unmakeMove(state,player,move);
                    bestValue = Math.max(bestValue, value);
                    if (bestValue == value) mMove = move;
                }
            }
        } else {
            if (currentPlayer.equalsIgnoreCase(player1)) {
                GValue = player1NonTerminalStateEval(state, player);
            } else {
                GValue = player2NonTerminalStateEval(state, player);
            }
            return GValue;
        }
        return bestValue;
    }

    double minimize_value(String[] state, String player, int depth) {
        if (depth != 2) {
            player = player.equalsIgnoreCase(player1)? player2: player1;
        }
        if (isTerminalState(state)) return getUtility(state,player);
        double bestValue = Double.POSITIVE_INFINITY;
        if (depth != 0) {
            List<Integer> availableMoves = getAvailableMoves(state);
            if (availableMoves.size() == 36) {
                Random random = new Random();
                mMove = random.nextInt(36);
                return 0;
            } else {
                for (Integer move : availableMoves) {
                    countPerMove++;
                    String[] simulatedState = makeMove(state, player, move);
                    double value = maximize_value(simulatedState, player, depth - 1);
                    unmakeMove(state,player,move);
                    bestValue = Math.min(bestValue, value);
                    if (bestValue == value) mMove = move;
                }
            }
        } else {
            if (currentPlayer.equalsIgnoreCase(player1)) {
                GValue = player1NonTerminalStateEval(state, player);
            } else {
                GValue = player2NonTerminalStateEval(state, player);
            }
            return GValue;
        }
        return bestValue;
    }

    boolean isTerminalState(String[] state) {

        if (checkForWinner(state,player1) || checkForWinner(state,player2)) {
            return true;
        }

        // check if all tiles are filled out
        for (int i = 0; i < 36; i++) {
            if (board[i].equalsIgnoreCase(" ")) {
                return false;
            }
        }
        return true;
    }

    double getUtility(String[] state, String player){
        //terminal state if winner +1 or -1
        if (checkForWinner(state,player)) {
            if (player.equalsIgnoreCase(player1))
                return +1.0;
            else if (player.equalsIgnoreCase(player2))
                return -1.0;
        }
        // 0
        return 0;
    }

    String[] makeMove(String[] state, String player, Integer move) {
        state[move] = player;
        return state;
    }

    String[] unmakeMove(String[] state, String player, Integer move) {
        state[move] = " ";
        return state;
    }

    double player1NonTerminalStateEval(String[] state, String player) {
        int noOfMovesInSequence = getNoOfMovesInSequence(state, player);
        return noOfMovesInSequence*0.2;
    }

    double player2NonTerminalStateEval(String[] state, String player) {
        List<Integer> player_moves = getPlayerMoves(state, player);

        int moves = 0;
        Integer[] v_combo;
        List<Integer> possibleList = new ArrayList<>();
        int possibleMoves = 0;
        for (Integer[] combo: winning_combinationsList) {
            List<Boolean> movesCombo = Arrays.asList(player_moves.contains(combo[0]), player_moves.contains(combo[1]), player_moves.contains(combo[2]), player_moves.contains(combo[3]));
            int count = 0;
            for (int i=0;i<4;i++) {
                if (movesCombo.get(i)) {
                    count++;
                } else {
                    possibleList.add(i);
                }
            }
            if (count < 4) {
                for (Integer p: possibleList) {
                    if (state[p].equalsIgnoreCase(" ")) {
                        possibleMoves++;
                    } else {
                        possibleMoves = 0;
                        break;
                    }
                }
                possibleList.clear();
            }
            if (possibleMoves+count == 4) {
                moves = Math.max(moves,count);
                break;
            }
//            moves = Math.max(moves,count);
//            v_combo = combo;
        }

        double heuristic;
        heuristic = moves*0.2 + possibleMoves*0.05;




        return heuristic;
    }

    boolean checkForWinner(String[] state, String player) {
        List<Integer> player_moves = getPlayerMoves(state,player);

        if (player_moves.isEmpty()) {
            return false;
        } else if (patternMatch(player_moves)){
            return true;
        }
        return false;
    }

    boolean patternMatch(List<Integer> moves) {
        if (moves.size() < 4) return false;

        for (Integer[] combo: winning_combinationsList) {
            if (moves.contains(combo[0]) && moves.contains(combo[1]) && moves.contains(combo[2]) && moves.contains(combo[3])) {
                return true;
            }
        }
        return false;
    }

    int getNoOfMovesInSequence(String[] state, String player){
        List<Integer> player_moves = getPlayerMoves(state, player);

        int moves = 0;
        for (Integer[] combo: winning_combinationsList) {
            List<Boolean> movesCombo = Arrays.asList(player_moves.contains(combo[0]), player_moves.contains(combo[1]), player_moves.contains(combo[2]), player_moves.contains(combo[3]));
            int count = 0;
            for (Boolean move: movesCombo) {
                if (move) {
                    count++;
                } else if (count > 0) {
                    break;
                }
            }
            moves = Math.max(moves,count);
        }
        return moves;
        /*List<Integer> horizontalSeq = new ArrayList<>();
        List<Integer> verticalSeq = new ArrayList<>();
        List<Integer> right_directed_diagonalSeq = new ArrayList<>();
        List<Integer> left_directed_diagonalSeq = new ArrayList<>();
        int hIndex = 0, vIndex = 0, rdIndex = 0, ldIndex = 0;
        for (Integer move: player_moves){
            if (horizontalSeq.isEmpty()) {
                horizontalSeq.add(move);
                verticalSeq.add(move);
                right_directed_diagonalSeq.add(move);
                left_directed_diagonalSeq.add(move);
            } else if (horizontalSeq.get(hIndex)+1 == move) {
                horizontalSeq.add(move);
                hIndex++;
            } else if (verticalSeq.get(vIndex)+6 == move) {
                verticalSeq.add(move);
                vIndex++;
            } else if (right_directed_diagonalSeq.get(rdIndex)+7 == move) {
                right_directed_diagonalSeq.add(move);
                rdIndex++;
            } else if (left_directed_diagonalSeq.get(ldIndex)+5 == move) {
                left_directed_diagonalSeq.add(move);
                ldIndex++;
            }
        }
        return Math.max(Math.max(hIndex,vIndex),Math.max(rdIndex,ldIndex));*/
    }

    List<Integer> getPlayerMoves(String[] state, String player){
        List<Integer> player_moves = new ArrayList<>();
        for (int position = 0; position < 36; position++) {
            if (state[position].equalsIgnoreCase(player)) {
                player_moves.add(position+1);
            }
        }
        return player_moves;
    }

    List<Integer> getAvailableMoves(String[] state) {
        List<Integer> listOfMoves = new ArrayList<>();
        for (int move = 0; move < state.length; move++) {
            if (state[move].equalsIgnoreCase(" ")) {
                listOfMoves.add(move);
            }
        }
        return listOfMoves;
    }

    void print_board(String[] state) {
        for (int i = 0; i < 36; i++) {
            int j = i+1;
//            if (j < 10) System.out.print(" ");
//            if (board[i].equals(" ")) {
//                System.out.print("("+j+")");
//            } else {
                System.out.print(board[i]);
//            }
            if (j % 6 == 0) System.out.println();
            else System.out.print(" | ");
        }
    }

    void init() {
        //init the board
        for (int i = 0; i < 36; i++) {
            board[i] = " ";
        }

        Integer[][] winning_combinations = {{1,2,3,4}, {2,3,4,5}, {3,4,5,6}, {7,8,9,10}, {8,9,10,11}, {9,10,11,12},
                {13,14,15,16}, {14,15,16,17}, {15,16,17,18}, {19,20,21,22}, {20,21,22,23}, {21,22,23,24},
                {25,26,27,28}, {26,27,28,29}, {27,28,29,30}, {31,32,33,34}, {32,33,34,35}, {33,34,35,36},
                {1,7,13,19}, {7,13,19,25}, {13,19,25,31}, {2,8,14,20}, {8,14,20,26}, {14,20,26,32},
                {3,9,15,21}, {9,15,21,27}, {15,21,27,33}, {4,10,16,22}, {10,16,22,28}, {16,22,28,34},
                {5,11,17,23}, {11,17,23,29}, {17,23,29,35}, {6,12,18,24}, {12,18,24,30}, {18,24,30,36},
                {13,20,27,34}, {7,14,21,28}, {14,21,28,35}, {1,8,15,22}, {8,15,22,29}, {15,22,29,36},
                {2,9,16,23}, {9,16,23,30}, {3,10,17,24}, {4,9,14,19}, {5,10,15,20}, {10,15,20,25},
                {6,11,16,21}, {11,16,21,26}, {16,21,26,31}, {12,17,22,27}, {17,22,27,32}, {18,23,28,33}
        };
        winning_combinationsList.addAll(Arrays.asList(winning_combinations));
    }
}
