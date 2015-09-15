import java.util.ArrayList;

public class Player {
	public static final int SMALL_BLIND = 50;
	public static final int BIG_BLIND = 100;

	private String name;
	private boolean isDealer;
	private int changeInStack;
	private String holeCards;
	private int currentBet;
	private ArrayList<Action> preflopActions;
	private ArrayList<Action> flopActions;
	private ArrayList<Action> turnActions;
	private ArrayList<Action> riverActions;

	public Player(String name, boolean isDealer, String holeCards) {
		setName(name);
		setIsDealer(isDealer);
		if (this.isDealer) {
			setCurrentBet(SMALL_BLIND);
		} else {
			setCurrentBet(BIG_BLIND);
		}
		setHoleCards(holeCards);
		preflopActions = new ArrayList<>();
		flopActions = new ArrayList<>();
		turnActions = new ArrayList<>();
		riverActions = new ArrayList<>();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getIsDealer() {
		return this.isDealer;
	}

	public void setIsDealer(boolean isDealer) {
		this.isDealer = isDealer;
	}

	public int getChangeInStack() {
		return this.changeInStack;
	}

	public void setChangeInStack(int changeInStack) {
		this.changeInStack = changeInStack;
	}

	public int getCurrentBet() {
		return this.currentBet;
	}

	public void setCurrentBet(int currentBet) {
		this.currentBet = currentBet;
	}

	public String getHoleCards() {
		return this.holeCards;
	}

	public void setHoleCards(String holeCards) {
		this.holeCards = holeCards;
	}

	// preflop
	public ArrayList<Action> getPreflopActions() {
		return this.preflopActions;
	}

	public Action getNextPreflopAction() {
		Action next = this.preflopActions.get(0);
		this.preflopActions.remove(0);
		return next;
	}

	public void setPreflopActions(ArrayList<Action> preflopActions) {
		this.preflopActions = preflopActions;
	}

	// flop
	public ArrayList<Action> getFlopActions() {
		return this.flopActions;
	}

	public Action getNextFlopAction() {
		Action next = this.flopActions.get(0);
		this.flopActions.remove(0);
		return next;
	}

	public void setFlopActions(ArrayList<Action> flopActions) {
		this.flopActions = flopActions;
	}

	// turn
	public ArrayList<Action> getTurnActions() {
		return this.turnActions;
	}

	public Action getNextTurnAction() {
		Action next = this.turnActions.get(0);
		this.turnActions.remove(0);
		return next;
	}

	public void setTurnActions(ArrayList<Action> turnActions) {
		this.turnActions = turnActions;
	}

	// river
	public ArrayList<Action> getRiverActions() {
		return this.riverActions;
	}

	public Action getNextRiverAction() {
		Action next = this.riverActions.get(0);
		this.riverActions.remove(0);
		return next;
	}

	public void setRiverActions(ArrayList<Action> riverActions) {
		this.riverActions = riverActions;
	}
}
