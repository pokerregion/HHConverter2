import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Input {

	private static final Pattern ACTION_PATTERN = Pattern
			.compile("([cf])|(r[1-9]+[0-9]*)");

	private String inputHand;

	public Input(String inputHand) {
		setInputHand(inputHand);
	}

	public String getInputHand() {
		return this.inputHand;
	}

	public void setInputHand(String inputHand) {
		this.inputHand = inputHand;
	}

	// need to finish creating hand history object
	public HandHistory createHandHistory() {
		String full = inputHand;
		// get rid of "STATE:" at start
		String startsWithHandNumber = removeStartingInfo(full);
		String[] chunks = startsWithHandNumber.split(":");
		int handNumber = Integer.parseInt(chunks[0]);

		// split into flop, turn, and river actions
		String allActions[] = chunks[1].split("/");
		String preflopActionsString = allActions[0];

		// further split flop, turn, and river into individual strings
		// representing player actions
		List<String> arrayOfPreflopActions = convertStringIntoStringActions(preflopActionsString);

		// deal with hands and board
		String[] handsAndBoard = chunks[2].split("/");
		String[] holeCards = handsAndBoard[0].split("\\|");
		Dealer dealer = null;
		if (handsAndBoard.length > 1) {
			String[] board = new String[handsAndBoard.length - 1];
			for (int i = 1; i < handsAndBoard.length; i++) {
				board[i - 1] = handsAndBoard[i];
			}
			dealer = createDealer(board);
		}

		// other information contained in hand string
		String[] changesInStack = chunks[3].split("\\|");
		String[] playerNames = chunks[4].split("\\|");

		// set player hands
		String player1Hand = holeCards[0];
		String player2Hand = holeCards[1];

		// set player names
		String player1Name = playerNames[0];
		String player2Name = playerNames[1];
		
		//set changes in stack
		int player1ChangeInStack = Integer.parseInt(changesInStack[0]);
		int player2ChangeInStack = Integer.parseInt(changesInStack[1]);

		Player player1 = new Player(player1Name, false, player1Hand);
		Player player2 = new Player(player2Name, true, player2Hand);
		
		player1.setChangeInStack(player1ChangeInStack);
		player2.setChangeInStack(player2ChangeInStack);

		ArrayList<ArrayList<Action>> preflopActions = getActions(
				arrayOfPreflopActions, 1);
		player1.setPreflopActions(preflopActions.get(0));
		player2.setPreflopActions(preflopActions.get(1));

		if (allActions.length > 1) {
			ArrayList<ArrayList<Action>> flopActions = getActions(
					convertStringIntoStringActions(allActions[1]), 0);
			player1.setFlopActions(flopActions.get(0));
			player2.setFlopActions(flopActions.get(1));
			if (allActions.length > 2) {
				ArrayList<ArrayList<Action>> turnActions = getActions(
						convertStringIntoStringActions(allActions[2]), 0);
				player1.setTurnActions(turnActions.get(0));
				player2.setTurnActions(turnActions.get(1));
				if (allActions.length > 3) {
					ArrayList<ArrayList<Action>> riverActions = getActions(
							convertStringIntoStringActions(allActions[3]), 0);
					player1.setRiverActions(riverActions.get(0));
					player2.setRiverActions(riverActions.get(1));
				}
			}
		}

		// set fields of hand history object
		HandHistory handHistory = new HandHistory();
		handHistory.setDealer(dealer);
		handHistory.setHandNumber(handNumber);
		handHistory.setPlayer1(player1);
		handHistory.setPlayer2(player2);

		// set player1 and player2 for hand history

		return handHistory;

	}

	// helper functions for creating handHistory object

	private ArrayList<ArrayList<Action>> getActions(List<String> actions,
			int firstPlayer) {
		ArrayList<ArrayList<Action>> actionsByPlayer = new ArrayList<>();
		actionsByPlayer.add(new ArrayList<Action>());
		actionsByPlayer.add(new ArrayList<Action>());
		int currentPlayer = firstPlayer;
		String previousAction = firstPlayer == 0 ? "" : "r";
		int[] playerCoins = new int[2];
		if (firstPlayer == 1) {
			playerCoins[firstPlayer] = 50;
			playerCoins[1 - firstPlayer] = 100;
		}
		for (String action : actions) {
			if (action.startsWith("f")) {
				actionsByPlayer.get(currentPlayer).add(new Action("fold", 0));
				break;
			} else if (previousAction.isEmpty() || previousAction.equals("c")) {
				if (action.startsWith("c")) {
					actionsByPlayer.get(currentPlayer).add(
							new Action("check", 0));
					previousAction = "c";
				} else {
					int amount = Integer.parseInt(action.substring(1));
					actionsByPlayer.get(currentPlayer).add(
							new Action("bet", amount));
					playerCoins[currentPlayer] += amount;
					previousAction = "r";
				}
			} else if (previousAction.equals("r")) {
				if (action.startsWith("c")) {
					actionsByPlayer.get(currentPlayer).add(
							new Action("call", playerCoins[1 - currentPlayer]
									- playerCoins[currentPlayer]));
					previousAction = "c";
					playerCoins[currentPlayer] = playerCoins[1 - currentPlayer];
				} else {
					int amount = Integer.parseInt(action.substring(1));
					actionsByPlayer
							.get(currentPlayer)
							.add(new Action(
									"raise",
									amount
											- (playerCoins[1 - currentPlayer] - playerCoins[currentPlayer]),
									amount));
					previousAction = "r";
					playerCoins[currentPlayer] = amount;
				}
			}
			currentPlayer = 1 - currentPlayer;
		}
		return actionsByPlayer;
	}

	// helper to split string into individual strings representing actions
	public static List<String> convertStringIntoStringActions(String actions) {
		Matcher actionMatcher = ACTION_PATTERN.matcher(actions);
		List<String> actionStrings = new LinkedList<String>();
		while (actionMatcher.find()) {
			actionStrings.add(actionMatcher.group());
		}
		return actionStrings;
	}

	// helper to create dealer object
	public static Dealer createDealer(String[] board) {
		int boardLength = board.length;

		String flop = "", turn = "", river = "";

		// set as many streets as there are in the given hand (flop, turn,
		// river)
		if (boardLength == 3) {
			flop = board[0];
			turn = board[1];
			river = board[2];
		} else if (boardLength == 2) {
			flop = board[0];
			turn = board[1];
		} else if (boardLength == 1) {
			flop = board[0];
		}
		// separate flop into array of strings for each card
		String firstFlopCard = flop.substring(0, 2);
		String secondFlopCard = flop.substring(2, 4);
		String thirdFlopCard = flop.substring(4, 6);
		String[] flopArray = { firstFlopCard, secondFlopCard, thirdFlopCard };

		// create Dealer object
		return new Dealer(flopArray, turn, river);
	}

	public static String removeStartingInfo(String hand) {
		return hand.substring(6);
	}
}
