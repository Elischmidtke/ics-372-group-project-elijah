
public class BlueberryMuffinFactory extends MenuItemCreator{
	public MenuItem createItem(double basePrice) {
		return new BlueberryMuffin(basePrice);
	}
}
