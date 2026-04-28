
public class ChocolateCroissantFactory extends MenuItemCreator{
	public MenuItem createItem(double basePrice) {
		return new ChocolateCroissant(basePrice);
	}
}
