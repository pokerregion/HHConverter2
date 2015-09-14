
public class HandHistory {
	private Player player1, player2;
	private Dealer dealer;
	private int handNumber;
	
	HandHistory() {
		setPlayer1(null);
		setPlayer2(null);
		setDealer(null);
		setHandNumber(-1);
	}
	
	public Player getPlayer1() {
		return this.player1;
	}
	
	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}
	
	public Player getPlayer2() {
		return this.player2;
	}
	
	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}
	
	public Dealer getDealer() {
		return this.dealer;
	}
	
	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
	}
	
	public int getHandNumber() {
		return this.handNumber;
	}
	
	public void setHandNumber(int handNumber) {
		this.handNumber = handNumber;
	}
}
