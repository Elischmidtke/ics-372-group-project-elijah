
public class CappuccinoFactory extends MenuItemCreator {
	public MenuItem createItem(double basePrice) {
		return new Cappuccino(basePrice);
	}
}
