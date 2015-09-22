import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class HandHistoryConverter {

	private static final String HEADER = "PokerStars Game #45715290124: Tournament #284245586, $2.00+$0.20 USD Hold'em No Limit - Level XIX (50/100) - 2010/06/19 16:32:38 WET [2010/06/19 11:32:38 ET]\nTable '284245586 5' 9-max Seat #%d is the button%nSeat 2: %s (20000 in chips)%nSeat 7: %s (20000 in chips)%n";

	public static void main(String[] args) throws IOException {
		File inputFile = new File("input.txt");
		Scanner reader = new Scanner(inputFile);

		File outputFile = new File("output.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

		Player player1 = null;
		Player player2 = null;
		int buttonSeat = 7;

		// while there's another hand
		while (reader.hasNextLine()) {
			// read in hand
			String line = reader.nextLine();
			Input newHand = new Input(line);
			// set attributes
			HandHistory handHistory = newHand.createHandHistory();
			if (player1 == null && player2 == null) {
				player1 = handHistory.getPlayer1();
				player2 = handHistory.getPlayer2();
			}
			// write to output file
			writeHandToOutputFile(handHistory, writer, player1, player2,
					buttonSeat);
			if (reader.hasNextLine()) {
				writer.newLine();
			}
			buttonSeat = buttonSeat == 7 ? 2 : 7;
		}

		writer.flush();

		reader.close();
	}

	public static void writeHandToOutputFile(HandHistory handHistory,
			BufferedWriter writer, Player player1Seat, Player player2Seat,
			int buttonSeat) throws IOException {

		//initialize player and dealer objects
		Player player1 = handHistory.getPlayer1();
		Player player2 = handHistory.getPlayer2();
		Dealer dealer = handHistory.getDealer();

		//write header line for hand
		writer.write(String.format(HEADER, buttonSeat, player1Seat.getName(),
				player2Seat.getName()));

		//set up blinds
		writeBlind(player2, writer);
		writeBlind(player1, writer);

		//set up hole cards
		writeHoleCards(player1, writer);
		writeHoleCards(player2, writer);

		// PREFLOP ACTION
		writeActions(player1, player1.getPreflopActions(), player2,
				player2.getPreflopActions(), true, writer);

		// print flop
		if (dealer != null) {
			//print flop cards
			writeFlop(dealer, writer);
			// flop actions
			writeActions(player1, player1.getFlopActions(), player2,
					player2.getFlopActions(), false, writer);

			// print turn
			if (dealer.hasTurn()) {
				//print turn card
				writeTurn(dealer, writer);
				// turn actions
				writeActions(player1, player1.getTurnActions(), player2,
						player2.getTurnActions(), false, writer);

				// print river
				if (dealer.hasRiver()) {
					//print river card
					writeRiver(dealer, writer);
					// print river actions
					writeActions(player1, player1.getRiverActions(), player2,
							player2.getRiverActions(), false, writer);
				}
			}
		}
		// write change in stack for winner of hand
		writeChangeInStackSummary(player1, player2, writer);
	}

	//summary line about collecting x from pot (mostly for Holdem Manager)
	public static void writeChangeInStackSummary(Player player1, Player player2, BufferedWriter writer) throws IOException {
		writer.write("*** SHOW DOWN ***\n");
		
		writer.write(player1.getName() + ": shows [" + player1.getHoleCards().substring(0, 2) + " " + player1.getHoleCards().substring(2,4) + "] ()\n");
		writer.write(player2.getName() + ": shows [" + player2.getHoleCards().substring(0, 2) + " " + player1.getHoleCards().substring(2,4) + "] ()\n");
		if (player1.getChangeInStack() > 0) {
			writer.write(player1.getName() + " collected " + player1.getChangeInStack() + " from pot\n");
		} else {
			writer.write(player2.getName() + " collected " + player2.getChangeInStack() + " from pot\n");
		}
	}
	
	//posting blinds
	public static void writeBlind(Player player, BufferedWriter writer)
			throws IOException {
		int bet = player.getCurrentBet();
		writer.write(player.getName() + " posts "
				+ (bet == 50 ? "small blind 50" : "big blind 100"));
		writer.newLine();
	}

	//determine correct arguments for second writeActions function (below)
	public static void writeActions(Player player1,
			List<Action> player1Actions, Player player2,
			List<Action> player2Actions, boolean dealerFirst,
			BufferedWriter writer) throws IOException {
		if (dealerFirst) {
			if (player1.getIsDealer()) {
				writeActions(player1, player1Actions, player2, player2Actions,
						writer);
			} else {
				writeActions(player2, player2Actions, player1, player1Actions,
						writer);
			}
		} else {
			if (player1.getIsDealer()) {
				writeActions(player2, player2Actions, player1, player1Actions,
						writer);
			} else {
				writeActions(player1, player1Actions, player2, player2Actions,
						writer);
			}
		}
	}

	//function to go back and forth between players, writing their actions
	private static void writeActions(Player player1,
			List<Action> player1Actions, Player player2,
			List<Action> player2Actions, BufferedWriter writer)
			throws IOException {
		//as long as player 1 has another action
		while (!player1Actions.isEmpty()) {
			Action next1 = player1Actions.remove(0);
			writeNextAction(player1, next1, writer);
			//if player 2 has another action
			if (!player2Actions.isEmpty()) {
				Action next2 = player2Actions.remove(0);
				writeNextAction(player2, next2, writer);
			}
		}
	}

	//preflop action (dealer first)
	public static void writePreflopAction(Player player1, Player player2,
			BufferedWriter writer) throws IOException {
		//if player 1 is dealer, he acts first
		if (player1.getIsDealer()) {
			while (!player1.getPreflopActions().isEmpty()
					|| !player2.getPreflopActions().isEmpty()) {
				if (!player1.getPreflopActions().isEmpty()) {
					//get next player 1 preflop action
					Action next = player1.getNextPreflopAction();
					writeNextAction(player1, next, writer);
				}
				if (!player2.getPreflopActions().isEmpty()) {
					//get next player 2 preflop action
					Action next = player2.getNextPreflopAction();
					writeNextAction(player2, next, writer);
				}
			}
			//player 2 is dealer
		} else {
			while (!player1.getPreflopActions().isEmpty()
					|| !player2.getPreflopActions().isEmpty()) {
				if (!player2.getPreflopActions().isEmpty()) {
					//get next player 2 preflop action
					Action next = player2.getNextPreflopAction();
					writeNextAction(player2, next, writer);
				}
				if (!player1.getPreflopActions().isEmpty()) {
					//get next player 1 preflop action
					Action next = player1.getNextPreflopAction();
					writeNextAction(player1, next, writer);
				}
			}
		}
	}

	//flop action (dealer acts second)
	public static void writeFlopAction(Player player1, Player player2,
			BufferedWriter writer) throws IOException {
		//player 1 acts first
		if (player2.getIsDealer()) {
			while (!player1.getFlopActions().isEmpty()
					|| !player2.getFlopActions().isEmpty()) {
				if (!player1.getFlopActions().isEmpty()) {
					//get next player 1 preflop action
					Action next = player1.getNextFlopAction();
					writeNextAction(player1, next, writer);
				}
				if (!player2.getFlopActions().isEmpty()) {
					//get next player 2 preflop action
					Action next = player2.getNextFlopAction();
					writeNextAction(player2, next, writer);
				}
			}
			//player 2 acts first
		} else {
			while (!player1.getFlopActions().isEmpty()
					|| !player2.getFlopActions().isEmpty()) {
				if (!player2.getFlopActions().isEmpty()) {
					//get next player 2 preflop action
					Action next = player2.getNextFlopAction();
					writeNextAction(player2, next, writer);
				}
				if (!player1.getFlopActions().isEmpty()) {
					//get next player 1 preflop action
					Action next = player1.getNextFlopAction();
					writeNextAction(player1, next, writer);
				}
			}
		}
	}

	//turn action (dealer acts second)
	public static void writeTurnAction(Player player1, Player player2,
			BufferedWriter writer) throws IOException {
		//player 1 acts first
		if (player2.getIsDealer()) {
			while (!player1.getTurnActions().isEmpty()
					|| !player2.getTurnActions().isEmpty()) {
				if (!player1.getTurnActions().isEmpty()) {
					//get next player 1 preflop action
					Action next = player1.getNextTurnAction();
					writeNextAction(player1, next, writer);
				}
				if (!player2.getTurnActions().isEmpty()) {
					//get next player 2 preflop action
					Action next = player2.getNextTurnAction();
					writeNextAction(player2, next, writer);
				}
			}
			//player 2 acts first
		} else {
			while (!player1.getTurnActions().isEmpty()
					|| !player2.getTurnActions().isEmpty()) {
				if (!player2.getTurnActions().isEmpty()) {
					//get next player 2 preflop action
					Action next = player2.getNextTurnAction();
					writeNextAction(player2, next, writer);
				}
				if (!player1.getTurnActions().isEmpty()) {
					//get next player 1 preflop action
					Action next = player1.getNextTurnAction();
					writeNextAction(player1, next, writer);
				}
			}
		}
	}

	//river action (dealer acts second)
	public static void writeRiverAction(Player player1, Player player2,
			BufferedWriter writer) throws IOException {
		//player 1 acts first
		if (player2.getIsDealer()) {
			while (!player1.getRiverActions().isEmpty()
					|| !player2.getRiverActions().isEmpty()) {
				if (!player1.getRiverActions().isEmpty()) {
					//get next player 1 preflop action
					Action next = player1.getNextRiverAction();
					writeNextAction(player1, next, writer);
				}
				if (!player2.getRiverActions().isEmpty()) {
					//get next player 2 preflop action
					Action next = player2.getNextRiverAction();
					writeNextAction(player2, next, writer);
				}
			}
			//player 2 acts first
		} else {
			while (!player1.getRiverActions().isEmpty()
					|| !player2.getRiverActions().isEmpty()) {
				if (!player2.getRiverActions().isEmpty()) {
					//get next player 2 preflop action
					Action next = player2.getNextRiverAction();
					writeNextAction(player2, next, writer);
				}
				if (!player1.getRiverActions().isEmpty()) {
					//get next player 1 preflop action
					Action next = player1.getNextRiverAction();
					writeNextAction(player1, next, writer);
				}
			}
		}
	}

	//write next action
	public static void writeNextAction(Player player, Action actionObj,
			BufferedWriter writer) throws IOException {
		String action = actionObj.getAction();
		if (action.equals("fold")) {
			writeFold(player, writer);
		} else if (action.equals("check")) {
			writeCheck(player, writer);
		} else if (action.equals("call")) {
			writeCall(player, actionObj.getAmount(), writer);
		} else if (action.equals("bet")) {
			writeBet(player, actionObj.getAmount(), writer);
		} else if (action.equals("raise")) {
			writeRaise(player, actionObj.getAmount(),
					actionObj.getTotalRaiseAmount(), writer);
		}
	}

	/*functions for writing a line for each of fold, check, call, all-in call,
	bet, all-in bet, raise, all-in raise*/

	//fold
	public static void writeFold(Player player, BufferedWriter writer)
			throws IOException {
		writer.write(player.getName() + ": folds\n");
	}

	//check
	public static void writeCheck(Player player, BufferedWriter writer)
			throws IOException {
		writer.write(player.getName() + ": checks\n");
	}

	//call
	public static void writeCall(Player player, int amount,
			BufferedWriter writer) throws IOException {
		writer.write(player.getName() + ": calls " + amount + "\n");
		player.setCurrentBet(amount);
	}

	//all-in call
	public static void writeAllInCall(Player player, int amount,
			BufferedWriter writer) throws IOException {
		writer.write(player.getName() + ": calls " + amount
				+ " and is all-in\n");
		player.setCurrentBet(amount);
	}

	//bet
	public static void writeBet(Player player, int amount, BufferedWriter writer)
			throws IOException {
		writer.write(player.getName() + ": bets " + amount + "\n");
		player.setCurrentBet(amount);
	}

	//all-in bet
	public static void writeAllInBet(Player player, int amount,
			BufferedWriter writer) throws IOException {
		writer.write(player.getName() + ": bets " + amount + " and is all-in\n");
		player.setCurrentBet(amount);
	}

	//uncalled bet
	public static void writeUncalledBet(Player player, int amount,
			BufferedWriter writer) throws IOException {
		writer.write("Uncalled bet (" + amount + ") returned to "
				+ player.getName() + "\n");
	}

	//raise
	public static void writeRaise(Player player, int raiseAmount,
			int raiseTotal, BufferedWriter writer) throws IOException {
		writer.write(player.getName() + ": raises " + raiseAmount + " to "
				+ raiseTotal + "\n");
		player.setCurrentBet(raiseTotal);
	}

	//all-in raise
	public static void writeAllInRaise(Player player, int raiseAmount,
			int raiseTotal, BufferedWriter writer) throws IOException {
		writer.write(player.getName() + ": raises " + raiseAmount + " to "
				+ raiseTotal + " and is all-in\n");
		player.setCurrentBet(raiseTotal);
	}

	//each hole card string will be 4 characters
	public static void writeHoleCards(Player player, BufferedWriter writer)
			throws IOException {
		String holeCards = player.getHoleCards();
		String card1 = holeCards.substring(0, 2);
		String card2 = holeCards.substring(2);
		writer.write("Dealt to " + player.getName() + " [" + card1 + " "
				+ card2 + "]\n");
	}

	//basic functions for writing flop, turn, and river
	
	public static void writeFlop(Dealer dealer, BufferedWriter writer)
			throws IOException {
		String[] flop = dealer.getFlop();
		writer.write("*** FLOP *** [" + flop[0] + " " + flop[1] + " " + flop[2]
				+ "]\n");
	}

	public static void writeTurn(Dealer dealer, BufferedWriter writer)
			throws IOException {
		String[] flop = dealer.getFlop();
		String turn = dealer.getTurn();
		writer.write("*** TURN *** [" + flop[0] + " " + flop[1] + " " + flop[2]
				+ "] [" + turn + "]\n");
	}

	public static void writeRiver(Dealer dealer, BufferedWriter writer)
			throws IOException {
		String[] flop = dealer.getFlop();
		String turn = dealer.getTurn();
		String river = dealer.getRiver();
		writer.write("*** RIVER *** [" + flop[0] + " " + flop[1] + " "
				+ flop[2] + " " + turn + "] [" + river + "]\n");
	}
}