import com.fasterxml.jackson.annotation.JsonProperty;

public class AlmondMilkDecorator extends BeverageAddition{
	@JsonProperty("charge")
	private static double charge = 1.00;
	
	public AlmondMilkDecorator(MenuItem beverage) {
		super(beverage, "Almond Milk", "Beverage", charge);
		
	}
	
	@Override
	public String getDescription() {
		if(applied) {
			return getDescription() + " Almond Milk ";
		}
		
		return getDescription();
		
	}
	
	public void setCharge(double charge) {
		AlmondMilkDecorator.charge = charge;
	}
	
	public static double getCharge() {
		return AlmondMilkDecorator.charge;
	}
	
	@Override
	public double getTotal() {
		if(applied) {
			return beverage.getTotal() + charge;
		}
		
		return beverage.getTotal();
	}
	
}
