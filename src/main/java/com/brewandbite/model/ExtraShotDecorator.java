import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtraShotDecorator extends BeverageAddition{
	@JsonProperty("charge")
	private static double charge = 0.75;
	public ExtraShotDecorator(MenuItem beverage) {
		super(beverage, "Extra Shot", "Beverage", charge);
	}

	@Override
	public String getDescription() {
		if(this.isAvailable()) {
			return beverage.getDescription() + " Extra Shot ";
		}
		
		return beverage.getDescription();
		
	}
	
	public void setCharge(double charge) {
		ExtraShotDecorator.charge = charge;
	}
	
	public static double getCharge() {
		return ExtraShotDecorator.charge;
	}
	
	public double getTotal() {
		if(applied) {
			return beverage.getTotal() + charge;
		}
		
		return beverage.getTotal();
	}
	
	
	

}
