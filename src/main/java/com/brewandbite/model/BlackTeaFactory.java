
public class BlackTeaFactory extends MenuItemCreator{
	public MenuItem createItem(double basePrice) {
		return new BlackTea(basePrice);
	}
}
