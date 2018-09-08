package Basics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import Scene.Photon;
import net.sf.javaml.core.kdtree.KDTree;

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
		return (limitColor(c.getRed()+cc.getRed(), c.getGreen()+cc.getGreen(), c.getBlue()+cc.getBlue()));
		
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
	
	public static Color loadColor(Point intersection, KDTree KDE) {
		double h[] = { intersection.getX(), intersection.getY(), intersection.getZ() };
		Object ob[];

		List<Photon> nearestPhoton = new ArrayList<Photon>();
		ob = KDE.nearest(h, 100);
		for (int i = 0; i < 100; i++) {
			nearestPhoton.add((Photon) ob[i]);
		}

		Color cc;
		cc = nearestPhoton.get(0).getFlujo();
		for (int i = 1; i < 99; i++) {
			cc = ColorFunctions.sumaColor(cc,nearestPhoton.get(i).getFlujo());
		}
		double area = 4* Math.PI* Operator.subP(intersection, nearestPhoton.get(99).getPosicion()).module();
		return new Color((int) (cc.getRed()/area), (int)(cc.getGreen()/area), (int)(cc.getBlue()/area));
	}
}
