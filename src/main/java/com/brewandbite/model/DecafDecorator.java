import com.fasterxml.jackson.annotation.JsonProperty;

public class DecafDecorator extends BeverageAddition{
	@JsonProperty("charge")
	private static double charge = 0.75;
	
	public DecafDecorator(MenuItem beverage) {
		super(beverage, "Decaf", "Beverage", charge);
	}
	
	@Override
	public String getDescription() {
		if(applied) {
			return beverage.getDescription() + " Decaf ";
		}
		
		return beverage.getDescription();
		
	}
	
	public void setCharge(double charge) {
		DecafDecorator.charge = charge;
	}
	
	public static double getCharge() {
		return DecafDecorator.charge;
	}
	
	public double getTotal() {
		if(applied) {
			return beverage.getTotal() + charge;
		}
		
		return beverage.getTotal();
	}
	
	
}
