import com.fasterxml.jackson.annotation.JsonProperty;

public class MediumSizeDecorator extends BeverageAddition{
	@JsonProperty("charge")
	private static double charge = 1.00;
	
	public MediumSizeDecorator(MenuItem beverage) {
		super(beverage, "Medium", "Beverage", charge);
	}
	
	@Override
	public String getDescription() {
		if(this.isAvailable()) {
			return beverage.getDescription() + " Medium ";
		}
		
		return beverage.getDescription();
		
	}
	
	public void setCharge(double charge) {
		MediumSizeDecorator.charge = charge;
	}
	
	public static double getCharge() {
		return MediumSizeDecorator.charge;
	}
	
	public double getTotal() {
		if(applied) {
			return beverage.getTotal() + charge;
		}
		
		return beverage.getTotal();
	}
}
