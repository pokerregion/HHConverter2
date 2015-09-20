public class Dealer {
	private String[] flop;
	private String turn;
	private String river;

	//basic constructor for Dealer object
	public Dealer(String[] flop, String turn, String river) {
		setFlop(flop);
		setTurn(turn);
		setRiver(river);
	}

	//basic getters / setters for dealer object
	
	public String[] getFlop() {
		return this.flop;
	}

	public void setFlop(String[] flop) {
		this.flop = flop;
	}

	public String getTurn() {
		return this.turn;
	}

	public void setTurn(String turn) {
		this.turn = turn;
	}

	public String getRiver() {
		return this.river;
	}

	public void setRiver(String river) {
		this.river = river;
	}

	public boolean hasTurn() {
		return !turn.isEmpty();
	}

	public boolean hasRiver() {
		return !river.isEmpty();
	}
}
