import com.fasterxml.jackson.annotation.JsonProperty;

public class OatMilkDecorator extends BeverageAddition {
	@JsonProperty("charge")
	private static double charge = 1.00;
	
	public OatMilkDecorator(MenuItem beverage) {
		super(beverage, "Oat Milk", "Beverage", charge);
	}
	
	@Override
	public String getDescription() {
		if(this.isAvailable()) {
			return beverage.getDescription() + " Oat Milk ";
		}
		
		return beverage.getDescription();
		
	}
	
	public void setCharge(double charge) {
		OatMilkDecorator.charge = charge;
	}
	
	public static double getCharge() {
		return OatMilkDecorator.charge;
	}
	
	public double getTotal() {
		if(applied) {
			return beverage.getTotal() + charge;
		}
		
		return beverage.getTotal();
	}
	
}
