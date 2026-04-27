
public class OatmealCookieFactory extends MenuItemCreator{
	public MenuItem createItem(double basePrice) {
		return new OatmealCookie(basePrice);
	}
}
