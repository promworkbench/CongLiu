package congliu.processmining.svgicon;
/**
 * this class helps to load icons
 */
import java.io.InputStream;

public class LoadIconClass 
{
	public static InputStream getIcon(String iconLable) {
		//return LoadIconClass.class.getResourceAsStream("icon.svg");
		return LoadIconClass.class.getResourceAsStream(iconLable);
	}
}
