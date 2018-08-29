package Scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import Basics.*;
import Figures.*;
import net.sf.javaml.classification.KDtreeKNN;
import net.sf.javaml.core.kdtree.KDTree;

public class PhotonMApping {

	final static int MAX_REBOTES = 3;
	public static double profImagen = 10;
	public static int antialiasing = 30;
	public static List<Figure> figures = new ArrayList<Figure>();
	private static Direction Nb;
	private static Direction Nt;
	private static int percent = 0;

	public static void main(String[] args) {
		// Resolution photo
		int y = 1080;
		int x = 1080;

		// Variables Path
		BufferedImage bI = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
		File h = new File("sombraTr3.bmp");
		KDTree KDE = new KDTree(3);

		// Camera
		Point centerI = new Point(0, 0, 0);
		Point center = new Point(-40, 0, -10);
		Camera camera = new Camera(center);

		// RAY
		// inicial point image
		Point p = new Point(-20, -20, 0); // pixel abajo IZQ
		double[][] cbm = Operator.getMatrix(camera.getX(), camera.getY(), camera.getZ(), center);// cambiamos
																									// coordenadas de
																									// la camara
		p = Operator.changeBase(cbm, p);

		// new rayo camara
		Direction d = Operator.subP(p, center);

		// IntersectionRay iR = new IntersectionRay();

		// Resolucion tama�o pixel
		double sx = (-20.0 * 2) / x;
		System.out.println(-40.0 / x);
		System.out.println(sx);
		System.out.printf("%f\n", sx);
		double sy = (-20.0 * 2) / y;

		// List of figures
		figures = LoadFigures.load();

		Color finalColor;

		// Bucle Pixels Image/
		for (int py = 0; py < y; py++) {
			for (int px = 0; px < x; px++) {
				// System.out.println(p+" "+ sx +" "+ px +" "+ sy +" "+ py +" "+ cbm + " "+
				// camera.getC());
				finalColor = render(p, 0, px, 0, py, cbm, camera.getC(), KDE);
				// Point light= new Point (0,0,0);
				bI.setRGB(px, py, finalColor.getRGB());
				p.setX(p.getX() + sx);
			}
			percent(py, y);
			p.setY(p.getY() + sy);
			p.setX(-20);
		}
		try {
			ImageIO.write(bI, "bmp", h);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private static void percent(int py, int y) {
		if ((py * 100) / y > percent) {
			percent = (py * 100) / y;
			System.out.println((percent + 1) + "%");
		}
	}

	public static Color render(Point p, double sx, double px, double sy, double py, double[][] cbm, Point origin,KDTree KDE) {
		// Point intersection=null;
		Random r = new Random();
		int rA = 0;
		int gA = 0;
		int bA = 0;
		Point pA;
//		Point light = new Point(-35, 39.98, 45);
		Triangle lightT1=new Triangle(new Point(-35, 39.98, 45), new Point(-45, 39.98, 55), new Point(-45, 39.98, 45),
				new Direction(0, 1, 0), Color.WHITE, 0.5, 0.2, true);
		lightT1.setIs_light(true);
		Triangle lightT2=new Triangle(new Point(-35, 39.98, 55), new Point(-35, 39.98, 45), new Point(-45, 39.98, 55),
				new Direction(0, 1, 0), Color.WHITE, 0.5, 0.2, true);
		lightT2.setIs_light(true);
		Point light = lightT1.getRnd();
		Point light2 = lightT2.getRnd();
		light.setX((light.getX() + light2.getX()) / 2);
		light.setY((light.getY() + light2.getY()) / 2);
		light.setZ((light.getZ() + light2.getZ()) / 2);
		// Rays - pixels
		for (int aS = 0; aS < antialiasing; aS++) {
			double x = p.getX() + (sx * r.nextDouble());
			double y = p.getY() + (sy * r.nextDouble());
			pA = new Point(x, y, profImagen);
			Figure f = null;
			double s = 9999999;
			double t = -1;

			Direction ray = Operator.subP(pA, origin);
			ray.normalize();

			Ray rayPath = new Ray(origin, ray);// ray listo -> camara - pixel
			Point intersection = null;
			// Intersect figures
			for (Figure sf : figures) {
				t = sf.intersect(origin, rayPath.getD());
				if (s > t && t > 0) {
					s = t;
					f = sf;
					// Punto de interseccion
					intersection = Operator.addD(origin, rayPath.getD().scale(t - 0.000001));
				}
			}
			// Direct light Blanco si choca en luz
			Color ld;
			if (!f.is_light)
				ld = luzDirecta(intersection, light, figures, f,80);
			else
				ld = Color.WHITE;
			
			int mC = RussianRoulette.calculate(f);
			// MonteCarlo
			Color lI = new Color(0,0,0);

			Ray tt = rayPath;
			if (mC == 2) {
				lI = luzIndirectaEspecular(intersection, light, figures, f, tt,0);
			}
			else if(mC == 1) {
				lI = loadColor(intersection, KDE); 
				//lI = luzIndirectaDifusa(intersection, light, figures, f, tt,0);
			}
			// end ray

				rA =rA + (ld.getRed() + lI.getRed())/2;
				bA =bA + (ld.getBlue() + lI.getBlue())/2;
				gA = gA + (ld.getGreen() + lI.getGreen())/2;
				
				//PARA PROBAR INDIRECTA SOLO DESCOMENTAR
//			rA =rA + lI.getRed();
//			bA =bA +  lI.getBlue();
//			gA = gA +  lI.getGreen();
		}
		
		rA = rA / (antialiasing);
		bA = bA / (antialiasing);
		gA = gA / (antialiasing);
		Color endColor = ColorFunctions.limitColor((int) rA, (int) gA, (int) bA);
		return ( endColor);

	}

	private static void createCoordinateSystem(Direction n1) {
		float x, y, z;
		if (Math.abs(n1.getX()) > Math.abs(n1.getY())) {
			x = (float) (n1.getZ() / Math.sqrt(n1.getX() * n1.getX() + n1.getZ() * n1.getZ()));
			y = 0;
			z = (float) (-n1.getX() / Math.sqrt(n1.getX() * n1.getX() + n1.getZ() * n1.getZ()));
		} else {
			x = 0;
			y = (float) (-n1.getZ() / Math.sqrt(n1.getY() * n1.getY() + n1.getZ() * n1.getZ()));
			z = (float) (n1.getY() / Math.sqrt(n1.getY() * n1.getY() + n1.getZ() * n1.getZ()));
		}
		Nt = new Direction(x, y, z);
		Nb = Operator.crossProduct(n1, Nt);
	}

	private static Color loadColor(Point intersection, KDTree KDE) {
		double h[] = { intersection.getX(), intersection.getY(), intersection.getZ() };
		Object ob[];

		List<Photon> nearestPhoton = new ArrayList<Photon>();
		ob = KDE.nearest(h, 100);
		for (int i = 0; i < 100; i++) {
			nearestPhoton.add((Photon) ob[i]);
		}

		Color cc;
		cc = nearestPhoton.get(0).getFlujo();
		for (int i = 1; i < 100; i++) {
			cc = ColorFunctions.sumaColor(cc,nearestPhoton.get(i).getFlujo());
		}
		return cc;
	}
	

	

	private static Color luzIndirectaDifusa(Point intersect, Point light, List<Figure> figures, Figure fi, Ray ray,
			int bounds) {
		// new ray
		/******************************/
		double cosTheta = Math.random();
		double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
		Direction rayDirection = getSample(cosTheta);
		createCoordinateSystem(fi.getNormal(intersect));

		Direction rayDirectionCast = new Direction(
				rayDirection.getX() * Nb.getX() + rayDirection.getY() * fi.getNormal(intersect).getX()
						+ rayDirection.getZ() * Nt.getX(),
				rayDirection.getX() * Nb.getY() + rayDirection.getY() * fi.getNormal(intersect).getY()
						+ rayDirection.getZ() * Nt.getY(),
				rayDirection.getX() * Nb.getZ() + rayDirection.getY() * fi.getNormal(intersect).getZ()
						+ rayDirection.getZ() * Nt.getZ());
		Ray r = new Ray(Operator.addD(intersect, rayDirectionCast), rayDirectionCast);
		ray = r;
		/******************************/
		Figure f = null;
		double s = 9999999;
		double t = -1;
		Point intersection = null;

		for (Figure sf : figures) {
			t = sf.intersect(intersect, ray.getD());
			if (s > t && t > 0) {
				s = t;
				f = sf;
				// direct light
				intersection = Operator.addD(intersect, ray.getD().scale(t - 0.000001));
				// Light of direct Light
			}
		}
		Color lD = luzDirecta(intersection, light, figures, f, 80);
		Color lD2 = new Color(0);
		if (bounds < MAX_REBOTES && !f.is_light) {
			int russianRoulette = RussianRoulette.calculate(f);
			if (russianRoulette == 1 && !f.is_light) {
				lD2 = luzIndirectaDifusa(intersection, light, figures, f, ray, bounds + 1);
////				lD = new Color((int)255/(bounds+1), 0 ,(int)255/(bounds+1));

			} else if (russianRoulette == 2 && !f.is_light) {
				lD2 = luzIndirectaEspecular(intersection, light, figures, f, ray, bounds + 1);
//				lD = new Color(((lD2.getRed() + lD.getRed()) / 2), ((lD2.getGreen() + lD.getGreen()) / 2),
//				((lD2.getBlue() + lD.getBlue()) / 2));
			}
			double rr = lD2.getRed() * f.getKd() / (bounds + 1);// (2*sinTheta*cosTheta);//Operator.subP(intersection,
																// intersect).module();//
			double bb = lD2.getBlue() * f.getKd() / (bounds + 1);// (2*sinTheta*cosTheta);//Operator.subP(intersection,
																	// intersect).module();//(bounds+1);
			double gg = lD2.getGreen() * f.getKd() / (bounds + 1);// (2*sinTheta*cosTheta);//Operator.subP(intersection,
																	// intersect).module();//(bounds+1);

			double rFinal = (lD.getRed() + rr) / 2;
			double bFinal = (lD.getBlue() + bb) / 2;
			double gFinal = (lD.getGreen() + gg) / 2;

			lD = ColorFunctions.limitColor((int) rFinal, (int) gFinal, (int) bFinal);
		}
		return lD;
	}


	private static Color luzIndirectaEspecular(Point intersect, Point light, List<Figure> figures, Figure fi, Ray ray,
			int bounds) {
		// new specular
		/******************************/
		double i = Operator.dotProduct(ray.getD(), fi.getNormal(intersect));
		ray.setD(Operator.subD(ray.getD(), fi.getNormal(intersect).scale(2 * i)));
		/******************************/
		Figure f = null;
		double s = 9999999;
		double t = -1;
		Point intersection = null;

		for (Figure sf : figures) {
			t = sf.intersect(intersect, ray.getD());
			if (s > t && t > 0) {
				s = t;
				f = sf;
				// //direct light
				intersection = Operator.addD(intersect, ray.getD().scale(t - 0.000001));
				// // Light of direct Light
			}
		}
		Color lE = luzDirecta(intersection, light, figures, f, 80);
		Color lE2 = new Color(0);
		// while(!f.is_light) {
		// RussianRoullete
		if (bounds < MAX_REBOTES && !f.is_light) {
			int russianRoulette = RussianRoulette.calculate(f);
			if (russianRoulette == 1) {
				lE2 = luzIndirectaDifusa(intersection, light, figures, f, ray, bounds + 1);
			}
			if (russianRoulette == 2) {
				lE2 = luzIndirectaEspecular(intersection, light, figures, f, ray, bounds + 1);
			}
			lE = new Color(((lE2.getRed() + lE.getRed()) / 2), ((lE2.getGreen() + lE.getGreen()) / 2),
					((lE2.getBlue() + lE.getBlue()) / 2));
		}
		return lE;
	}

	public static Color luzDirecta(Point intersect, Point light, List<Figure> f, Figure fi, double potencia) {
		// direction de intersection a ligth
		Direction cx = Operator.subP(light, intersect);
		cx.normalize(); // normalizada
		Ray r = new Ray(intersect, cx);
		if (block(f, r, light)) {
			return Color.BLACK;
		} else {
			int rr = (int) colour(255, light, intersect, fi, fi.getColor().getRed(), potencia);
			int gg = (int) colour(255, light, intersect, fi, fi.getColor().getGreen(), potencia);
			int bb = (int) colour(255, light, intersect, fi, fi.getColor().getBlue(), potencia);
			Color c = ColorFunctions.limitColor((int) rr, (int) gg, (int) bb);
			return c;
		}
	}

	public static boolean block(List<Figure> f, Ray r, Point light) {
		double s = 9999999;
		double t = -1;
		Point intersection = null;
		// Intersect figures
		for (Figure sf : figures) {
			t = sf.intersect(r.getO(), r.getD());
			if (s > t && t > 0) {
				s = t;
				intersection = Operator.addD(r.getO(), r.getD().scale(t));
			}
		}

		if (intersection == (null)) {
			return false;
		}
		//
		if (r.getD().scale(s + 0.000001).module() < Operator.subP(light, r.getO()).module()) {
			return true;
		}
		return false;
	}

	public static double colour(int colorLuz, Point light, Point intersect, Figure fi, int fcolor, double potencia) {
		double a = ((potencia)
				/ ((Operator.subP(light, intersect).module()) * (Operator.subP(light, intersect).module()))) * colorLuz;

		double b = fcolor * (fi.getKd() / Math.PI);
		// difuseComponent(fi.getKd(),fi.getNormal(intersect),Operator.subP(light,intersect));
		// double c=Operator.dotProduct(fi.getNormal(intersect),
		// Operator.subP(light,intersect).scale(Operator.subP(light,intersect).module()));
		double c = Math.abs(Operator.dotProduct(fi.getNormal(intersect),
				Operator.subP(light, intersect).scale(1 / Operator.subP(light, intersect).module())));
		a = a * b * c;
		return a;
	}

	public static double difuseComponent(double kd, Direction normal, Direction toLight) {
		return kd * Operator.dotProduct(normal, toLight);
	}

	public static double monteCarlo() {
		return Math.toRadians(Math.random() * 180);
	}

	public static Direction getSample(double cosTheta) {
		double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
		double phi = Math.PI * 2 * Math.random();
		return new Direction(sinTheta * Math.cos(phi), cosTheta, sinTheta * Math.sin(phi));
	}
}
