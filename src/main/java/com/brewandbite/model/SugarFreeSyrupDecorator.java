import com.fasterxml.jackson.annotation.JsonProperty;


public class SugarFreeSyrupDecorator extends BeverageAddition{
	@JsonProperty("charge")
	private static double charge = 0.25;
	
	public SugarFreeSyrupDecorator(MenuItem beverage) {
		super(beverage, "Sugar Free", "Beverage", charge);
	}
	
	@Override
	public String getDescription() {
		if(applied) {
			return getDescription() + " Sugar Free ";
		}
		
		return getDescription();
		
	}
	
	public void setCharge(double charge) {
		SugarFreeSyrupDecorator.charge = charge;
	}
	
	public static double getCharge() {
		return SugarFreeSyrupDecorator.charge;
	}
	
	public double getTotal() {
		if(applied) {
			return beverage.getTotal() + charge;
		}
		
		return beverage.getTotal();
	}
}
