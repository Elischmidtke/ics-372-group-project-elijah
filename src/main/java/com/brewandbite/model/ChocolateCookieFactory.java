
public class ChocolateCookieFactory extends MenuItemCreator{
	public MenuItem createItem(double basePrice) {
		return new ChocolateCookie(basePrice);
	}
}
