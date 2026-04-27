
public class ButterCroissantFactory extends MenuItemCreator{
	public MenuItem createItem(double basePrice) {
		return new ButterCroissant(basePrice);
	}
}
