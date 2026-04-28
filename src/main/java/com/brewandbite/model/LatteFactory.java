
public class LatteFactory extends MenuItemCreator{
	public MenuItem createItem(double basePrice) {
		return new Latte(basePrice);
	}
}
