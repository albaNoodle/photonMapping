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
	
	public static Color sumaColor(Color c, Color cc,Color temp) {
		return limitColor(c.getRed()+cc.getRed(), c.getGreen()+cc.getGreen(), c.getBlue()+cc.getBlue());
	}
	
	public static Color loadColor(Point intersection, KDTree KDT) {
		double h[] = { intersection.getX(), intersection.getY(), intersection.getZ() };
		Object ob[];
		Color temp;
		List<Photon> nearestPhoton = new ArrayList<Photon>();
		int vecinitos = 100;
		ob = KDT.nearest(h, vecinitos);
		for (int i = 0; i < vecinitos; i++) {
			nearestPhoton.add((Photon) ob[i]);
		}
		Color cc;
		int r=0;
		int g=0;
		int b=0;
		cc = nearestPhoton.get(0).getFlujo();
		for (int i = 0; i < vecinitos; i++) {
			r=r+nearestPhoton.get(i).getFlujo().getRed();
			b=b+nearestPhoton.get(i).getFlujo().getBlue();
			g=g+nearestPhoton.get(i).getFlujo().getGreen();
			
			//sumaColor(cc,nearestPhoton.get(i).getFlujo(),temp);
		}
		double radio =Operator.subP(intersection, nearestPhoton.get(vecinitos-1).getPosicion()).module();
		double area = (Math.PI* radio*radio);
		return ColorFunctions.limitColor((int) (r/area), (int)(g/area), (int)(b/area));		
	}
}
