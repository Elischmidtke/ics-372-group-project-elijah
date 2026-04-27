
public class ChocolateMuffinFactory extends MenuItemCreator {
	public MenuItem createItem(double basePrice) {
		return new ChocolateMuffin(basePrice);
	}
}
