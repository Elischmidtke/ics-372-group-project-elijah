
public class GreenTeaFactory extends MenuItemCreator{
	public MenuItem createItem(double basePrice) {
		return new GreenTea(basePrice);
	}
}
