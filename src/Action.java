
public class Action {
	private String action;
	private int amount;
	private int totalRaiseAmount;
	
	//constructor without totalRaiseAmount
	public Action(String action, int amount) {
		setAction(action);
		setAmount(amount);
		setTotalRaiseAmount(0);
	}
	
	//constructor with totalRaiseAmount
	public Action(String action, int amount, int totalRaiseAmount) {
		this(action, amount);
		setTotalRaiseAmount(totalRaiseAmount);
	}
	
	public String getAction() {
		return this.action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int getTotalRaiseAmount() {
		return this.totalRaiseAmount;
	}
	
	public void setTotalRaiseAmount(int totalRaiseAmount) {
		this.totalRaiseAmount = totalRaiseAmount;
	}
	
	
}
