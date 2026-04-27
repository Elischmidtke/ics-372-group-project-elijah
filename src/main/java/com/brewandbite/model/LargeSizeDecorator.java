import com.fasterxml.jackson.annotation.JsonProperty;

public class LargeSizeDecorator extends BeverageAddition {
	@JsonProperty("charge")
	private static double charge = 1.00;
	
	public LargeSizeDecorator(MenuItem beverage) {
		super(beverage, "Large", "Beverage", charge);
	}
	
	@Override
	public String getDescription() {
		if(this.isAvailable()) {
			return beverage.getDescription() + " Large ";
		}
		
		return beverage.getDescription();
		
	}
	
	public void setCharge(double charge) {
		LargeSizeDecorator.charge = charge;
	}
	
	public static double getCharge() {
		return LargeSizeDecorator.charge;
	}
	
	public double getTotal() {
		if(applied) {
			return beverage.getTotal() + charge;
		}
		
		return beverage.getTotal();
	}
}
