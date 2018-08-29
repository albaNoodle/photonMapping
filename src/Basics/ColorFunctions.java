package Basics;

import java.awt.Color;

public class ColorFunctions {
	public static Color limitColor(int r, int g, int b) {
		if (r > 255)
			r = 255;
		if (r < 0)
			r = 0;
		if (g > 255)
			g = 255;
		if (g < 0)
			g = 0;
		if (b > 255)
			b = 255;
		if (b < 0)
			b = 0;
		return new Color(r, g, b);
	}
	
	public static Color sumaColor(Color c, Color cc) {
		return new Color(c.getRed()+cc.getRed(), c.getGreen()+cc.getGreen(), c.getBlue()+cc.getBlue());
	}
	
	public static Color brighter(Color color, double percent) {
		int red = (int) (color.getRed() + (color.getRed() * percent));
		if (red > 255)
			red = 255;
		int green = (int) (color.getGreen() + (color.getGreen() * percent));
		if (green > 255)
			green = 255;
		int blue = (int) (color.getBlue() + (color.getBlue() * percent));
		if (blue > 255)
			blue = 255;

		return new Color(red, green, blue);
	}
}
