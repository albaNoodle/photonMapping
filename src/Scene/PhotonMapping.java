package Scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import Basics.ColorFunctions;
//import Basics.ColorFunctions;
import Basics.Direction;
import Basics.Operator;
import Basics.Point;
import Figures.Figure;
import Figures.Plane;
import Figures.Sphere;
import Figures.Triangle;
import net.sf.javaml.core.kdtree.KDTree;

public class PhotonMapping {

	static KDTree KDT = new KDTree(3);
	private static Direction Nb;
	private static Direction Nt;
	private static int MAX_REBOTES = 6;
	public static double profImagen = 10;
	public static int antialiasing;
	public static List<Figure> figures = new ArrayList<Figure>();
	private static int percent = 0;
	private static int POTENCIA = 120;
	private static String imageName;
	private static int N_PHOTONS = 50000;

	public static void main(String[] args) {
		antialiasing = Integer.parseInt(args[0]);
		// Resolution photo
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		imageName = args[3] + ".png";

		// Variables Path
		BufferedImage bI = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
		File h = new File(imageName);

		// Camera
		Point center = new Point(-40, 0, -10);
		Camera camera = new Camera(center);

		// RAY
		// inicial point image
		Point p = new Point(-20, -20, 0); // pixel abajo IZQ
		double[][] cbm = Operator.getMatrix(camera.getX(), camera.getY(), camera.getZ(), center);// cambiamos
																									// coordenadas de
																									// la camara
		p = Operator.changeBase(cbm, p);

		// IntersectionRay iR = new IntersectionRay();

		// Resolucion tama�o pixel
		double sx = (-20.0 * 2) / x;
		System.out.println(-40.0 / x);
		System.out.println(sx);
		System.out.printf("%f\n", sx);
		double sy = (-20.0 * 2) / y;

		/*
		 * LISTA DE FIGURAS, CREACI�N DE LA ESCENA
		 */
		figures.add(new Plane(new Point(0, 1, 0), new Direction(0, -1, 0), Color.GRAY, 40, 0.3, 0.0, 0.0)); // arriba
		figures.add(new Plane(new Point(0, -1, 0), new Direction(0, 1, 0), Color.GRAY, 40, 0.3, 0.0, 0.0));// abajo
		figures.add(new Plane(new Point(0, 0, 1), new Direction(0, 0, -1), Color.GRAY, 90, 0.3, 0, 0.0)); // fondo
//		figures.add(new Plane(new Point(0, 0, 1), new Direction(0, 0, 1), Color.YELLOW, 40, 0.5, 0, 0.0)); //
		figures.add(new Plane(new Point(1, 0, 0), new Direction(-1, 0, 0), Color.RED, 0, 0.3, 0, 0.0)); // izq
		figures.add(new Plane(new Point(-1, 0, 0), new Direction(1, 0, 0), Color.GREEN, 80, 0.3, 0, 0.0));// drcha
		// figures.add(new Sphere(new Point(-20, -20, 50), 5, Color.BLUE, 0.8));
		figures.add(new Sphere(new Point(-65, -25, 70), 13, Color.MAGENTA, 0.0, 1.0, 0));
		figures.add(new Sphere(new Point(-15, -25, 60), 13, Color.BLUE, 0.6, 0.0, 0.0));
//		figures.add(new Sphere(new Point(-40, 0, 60), 13, Color.YELLOW, 0.2, 0.1, 0));
		// figures.add(new Triangle(new Point(-50, -10, 50),new Point(-30, -10, 40),new
		// Point(-30, -10, 50),new Direction(1, 1, 1), Color.ORANGE,0.5));
//		figures.add(new Triangle(new Point(-5, -20, 50), new Point(-10, -20, 50), new Point(-7.5, -20, 60),
//				new Direction(0, 1, 0), Color.ORANGE, 0.5, 0.0, 0.0, false));
		/*Triangle TLight1 = new Triangle(new Point(-35, 39.99, 45), new Point(-45, 39.99, 55), new Point(-45, 39.99, 45),
				new Direction(0, 1, 0), Color.WHITE, 0.5, 0.0, 0.0, true);
		figures.add(TLight1);
		TLight1.setIs_light(true);
		Triangle TLight2 = new Triangle(new Point(-35, 39.99, 55), new Point(-35, 39.99, 45), new Point(-45, 39.99, 55),
				new Direction(0, 1, 0), Color.WHITE, 0.5, 0.0, 0.0, true);
		figures.add(TLight2);
		TLight2.setIs_light(true);
		ArrayList<Triangle> TLights = new ArrayList<>();
		TLights.add(TLight1);
		TLights.add(TLight2);*/
		ArrayList<Point> lightPoints = new ArrayList<>();
		Point light1 = new Point(-41, 39.0, 50);
		lightPoints.add(light1);
//		Point light2 = new Point(-40, 39.99, 52);
//		Point light3 = new Point(-40, 39.99, 70);
//		Point light4 = new Point(-40, 39.99, 35);
		Color finalColor;
		sendPhotons(figures, light1, N_PHOTONS);
		// Bucle Pixels Image/
		for (int py = 0; py < y; py++) {
			for (int px = 0; px < x; px++) {
				finalColor = render(p, sx, px, sy, py, cbm, camera.getC(), lightPoints);
				bI.setRGB(px, py, finalColor.getRGB());
				p.setX(p.getX() + sx);
			}
			percent(py, y);
			p.setY(p.getY() + sy);
			p.setX(-20);
		}
		try {
			ImageIO.write(bI, "png", h);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void percent(int py, int y) {
		if ((py * 100) / y > percent) {
			percent = (py * 100) / y;
			System.out.println((percent + 1) + "%");
		}
	}

	public static Color render(Point p, double sx, double px, double sy, double py, double[][] cbm, Point origin,
			ArrayList<Point> lightPoints) {
		// Point intersection=null;
		Random r = new Random();
		int rA = 0;
		int gA = 0;
		int bA = 0;
		Point pA;
//		Point light = new Point(-35, 39.98, 45);

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

			// MonteCarlo
			Color lI = new Color(0, 0, 0);
			Color ld = new Color(0, 0, 0);
			for (int i = 0; i < lightPoints.size(); i++) {
				if (!f.is_light) { 
					if (f.getKs()>0) {
						ld = luzDirectaEspecular(intersection, lightPoints.get(i), figures, f, ray, 0);
					}
					/*else if(f.getKr()>0) {
						lI = luzIndirectaRefractada(intersection, lightPoints.get(i), figures, f, rayPath, 0,1,true);
					}*/
					else {
						ld = luzDirecta(intersection, lightPoints.get(i), figures, f, rayPath.getD(), POTENCIA);
					}
				}
					else
					ld = Color.WHITE;

				lI = ColorFunctions.loadColor(intersection, KDT);
//				 LUZ INDIRECTA + DIRECTA
				rA = rA + ((ld.getRed() + lI.getRed()) / 2) / lightPoints.size();
				bA = bA + ((ld.getBlue() + lI.getBlue()) / 2) / lightPoints.size();
				gA = gA + ((ld.getGreen() + lI.getGreen()) / 2) / lightPoints.size();
//				 PARA PROBAR INDIRECTA SOLO DESCOMENTAR
//				rA =rA + lI.getRed();
//				bA =bA +  lI.getBlue();
//				gA = gA +  lI.getGreen();
			}
		}
		Color endColor = ColorFunctions.limitColor(rA/antialiasing, gA/antialiasing, bA/antialiasing);
		return endColor;

	}

	public static void sendPhotons(List<Figure> figures, Point light, int N_PHOTONS) {
		Figure f = null;
		double s = 9999999;
		double t = -1;
		Point intersection = null;
		double cosTheta = -1;
		//double cosTheta = Math.random();
		

		for (int i = 0; i < N_PHOTONS; i++) {
			
			Direction rayDirection = getSample2(cosTheta);
			Direction n = new Direction(0,-1,0);
			createCoordinateSystem(n);
			
			Direction rayDirectionCast = new Direction(
					rayDirection.getX() * Nb.getX() + rayDirection.getY() * n.getX()
							+ rayDirection.getZ() * Nt.getX(),
					rayDirection.getX() * Nb.getY() + rayDirection.getY() * n.getY()
							+ rayDirection.getZ() * Nt.getY(),
					rayDirection.getX() * Nb.getZ() + rayDirection.getY() * n.getZ()
							+ rayDirection.getZ() * Nt.getZ());
			
			
			
			Photon p = new Photon(light, Color.WHITE);
			//Direction ray = getSample2(cosTheta);
			Direction ray = rayDirectionCast;
			ray.normalize();
			for (Figure sf : figures) {
				t = sf.intersect(light, ray);
				if (s > t && t > 0) {
					s = t;
					f = sf;
					// Punto de interseccion
					intersection = Operator.addD(light, ray.scale(t - 0.000001));
				}
			}
			/*int mC = RussianRoulette.calculate(f);
			if (mC == 2) {
				savePhotonEspecular(intersection, light, figures, f, ray, 0, p);
			} else if (mC == 1) {
				savePhotonDiff(intersection, light, figures, f, ray, 0, p);
			}
			*/
			/*System.out.println("F: " + f.getKr());
			if(f.getKr()>0) {
				rayRefractada(intersection, light, figures, f, ray, 0, 1, true);	
			}
			else {
				savePhotonDiff(intersection, light, figures, f, ray, 0, p);
			}*/
			savePhotonDiff(intersection, light, figures, f, ray, 0, p);
		}
	}

	static void savePhotonDiff(Point intersect, Point light, List<Figure> figures, Figure fi, Direction ray, int bounds,
			Photon p) {
		
		Color figureColor=Color.BLACK;

		if (fi != null) {
			figureColor = fi.getColor();
			int r = (int) (p.getFlujo().getRed() + (figureColor.getRed() * fi.getKd())) / 2;
			int b = (int) (p.getFlujo().getBlue() + (figureColor.getBlue() * fi.getKd())) / 2;
			int g = (int) (p.getFlujo().getGreen() + (figureColor.getGreen() * fi.getKd())) / 2;
			p.setFlujo(new Color(r, g, b));
			if (bounds > 0) { // no es primer rebote
				saveColor(intersect, p.getFlujo());
			}
		}
		
		
		
		
		double cosTheta = Math.random();
		Direction rayDirection = getSample(cosTheta);
		createCoordinateSystem(fi.getNormal(intersect));

		Direction rayDirectionCast = new Direction(
				rayDirection.getX() * Nb.getX() + rayDirection.getY() * fi.getNormal(intersect).getX()
						+ rayDirection.getZ() * Nt.getX(),
				rayDirection.getX() * Nb.getY() + rayDirection.getY() * fi.getNormal(intersect).getY()
						+ rayDirection.getZ() * Nt.getY(),
				rayDirection.getX() * Nb.getZ() + rayDirection.getY() * fi.getNormal(intersect).getZ()
						+ rayDirection.getZ() * Nt.getZ());
		Figure f = null;
		double s = 9999999;
		double t = -1;
		Point intersection = null;

		for (Figure sf : figures) {
			t = sf.intersect(intersect, rayDirectionCast);
			if (s > t && t > 0) {
				s = t;
				f = sf;
				// direct light
				intersection = Operator.addD(intersect, rayDirectionCast.scale(t - 0.000001));
				// Light of direct Light
			}
		}

		

		if (bounds < MAX_REBOTES && !f.is_light && intersection != null && 
				(p.getFlujo().getBlue()+p.getFlujo().getRed()+p.getFlujo().getGreen()>40)) {
			int russianRoulette = RussianRoulette.calculate(f);
			if (f.getKr() > 0) {
				savePhotonRefractada(intersection, light, figures, f, ray, bounds + 1, 1, true, p);
			} else if (russianRoulette == 1 && !f.is_light) {
				savePhotonDiff(intersection, light, figures, f, ray, bounds + 1, p);

			} else if (russianRoulette == 2 && !f.is_light) {
				savePhotonEspecular(intersection, light, figures, f, ray, bounds + 1, p);
			}
		}
	}

	private static void savePhotonRefractada(Point intersect, Point light, List<Figure> figures, Figure fi,
			Direction ray, int bounds, double indexRefraction, boolean isInside, Photon p) {
		Direction Ti = ray;// .scale(-1);
		double cosI = Operator.dotProduct(Ti, fi.getNormal(intersect).scale(-1));
		double indexNext;

		if (isInside) {
			indexNext = 1.0;/// fi.getKr();
		} else {
			indexNext = fi.getKr();
		}
		double n = indexRefraction / indexNext;
		double cosT2 = Math.sqrt(1.0 - indexNext * indexNext * (1.0 - cosI * cosI));
		Direction T;
		if (cosT2 <= 0) {
			T = Operator.subD(fi.getNormal(intersect).scale(indexNext * cosI - cosT2), ray.scale(indexNext));
		} else {
			T = Operator.subD(ray.scale(n), fi.getNormal(intersect).scale(n * cosI - cosT2));
		}
		Ray rayR = new Ray(Operator.addD(intersect, T.scale(0.0000001)), T);
		Figure f = null;
		double s = 9999999;
		double t = -1;
		Point intersection = null;

		for (Figure sf : figures) {
			t = sf.intersect(Operator.addD(intersect, rayR.getD().scale(0.0001)), rayR.getD());
			if (s > t && t > 0) {
				s = t;
				f = sf;
				intersection = Operator.addD(intersect, rayR.getD().scale(t - 0.000001));
				// Light of direct Light
			}
		}
		if (f != null) {
			if (bounds < MAX_REBOTES && !f.is_light && intersection != null && 
					(p.getFlujo().getBlue()+p.getFlujo().getRed()+p.getFlujo().getGreen()>40)) {
				int russianRoulette = RussianRoulette.calculate(f);
				if (f.getKr() > 0) {
					savePhotonRefractada(intersection, light, figures, f, ray, bounds + 1, f.getKr(), false, p);
				}
				if (russianRoulette == 1) {
					savePhotonDiff(intersection, light, figures, f, ray, bounds + 1, p);
				} else if (russianRoulette == 2) {
					savePhotonEspecular(intersection, light, figures, f, ray, bounds + 1, p);
				}
			}
		}
	}

	static void savePhotonEspecular(Point intersect, Point light, List<Figure> figures, Figure fi, Direction ray,
			int bounds, Photon p) {
		// new specular
		/******************************/
		double i = Operator.dotProduct(ray, fi.getNormal(intersect));
		ray = (Operator.subD(ray, fi.getNormal(intersect).scale(2 * i)));
		/******************************/
		Figure f = null;
		double s = 9999999;
		double t = -1;
		Point intersection = null;

		for (Figure sf : figures) {
			t = sf.intersect(intersect, ray);
			if (s > t && t > 0) {
				s = t;
				f = sf;
				// //direct light
				intersection = Operator.addD(intersect, ray.scale(t - 0.000001));
				// // Light of direct Light
			}
		}
		if (bounds < MAX_REBOTES && !f.is_light && intersection != null && 
				(p.getFlujo().getBlue()+p.getFlujo().getRed()+p.getFlujo().getGreen()>40)) {
			int russianRoulette = RussianRoulette.calculate(f);
			if (f.getKr() > 0) {
				savePhotonRefractada(intersection, light, figures, f, ray, bounds + 1, 1, true, p);
			} else {
				if (russianRoulette == 1) {
					savePhotonDiff(intersection, light, figures, f, ray, bounds + 1, p);
				}
				if (russianRoulette == 2) {
					savePhotonEspecular(intersection, light, figures, f, ray, bounds + 1, p);
				}
			}
		}
	}

	private static void saveColor(Point intersection, Color C) {
		Photon po = new Photon(intersection, C);
		System.out.println(C);
		double key[] = { intersection.getX(), intersection.getY(), intersection.getZ() };
		KDT.insert(key, po);
	}

//	public static Direction getSample(double cosTheta) {
//		Random r = new Random();
//		double x = -1 + (1 + 1) * r.nextDouble();
//		double z = -1 + (1 + 1) * r.nextDouble();
//		return new Direction(x, -1, z);
//	}
	public static Direction getSample(double cosTheta) {
		double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
		double phi = Math.PI * 2 * Math.random();
		return new Direction(sinTheta * Math.cos(phi), cosTheta, sinTheta * Math.sin(phi));
	}
	
	public static Direction getSample2(double cosTheta) {
		double a1 = Math.random()*((-1)-1)+1;
		double a2 = Math.random()*((-1)-1)+1;
		return new Direction(a1, cosTheta, a2);
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

	public static Color luzDirecta(Point intersect, Point light, List<Figure> f, Figure fi, Direction ray,
			double potencia) {
		// direction de intersection a ligth
		if (intersect != null) {
			Direction cx = Operator.subP(light, intersect);
			cx.normalize(); // normalizada
			Ray r = new Ray(intersect, cx);
			if (block(f, r, light)) {
				return Color.BLACK;
			} else {
				int rr = (int) colour(255, light, intersect, fi, fi.getColor().getRed(), ray, potencia);
				int gg = (int) colour(255, light, intersect, fi, fi.getColor().getGreen(), ray, potencia);
				int bb = (int) colour(255, light, intersect, fi, fi.getColor().getBlue(), ray, potencia);
				Color c = ColorFunctions.limitColor(rr, gg, bb);
				return c;
			}
		} else {
			return Color.WHITE;
		}

	}

	public static double colour(int colorLuz, Point light, Point intersect, Figure fi, int fcolor, Direction ray,
			double potencia) {
		double a = ((potencia)
				/ ((Operator.subP(light, intersect).module()) * (Operator.subP(light, intersect).module()))) * colorLuz;
		double bDifusa = 0;
		double bEspecular = 0;
		if (fi.getKd() > 0) {
			bDifusa = fcolor * (fi.getKd() / Math.PI);
		}

		if (fi.getKs() > 0) {
			Direction middle = Operator.subD(Operator.subP(intersect, light), ray.scale(-1));
			bEspecular = fi.getKs() * Math.pow(Math.max(0.0, Operator.dotProduct(fi.getNormal(intersect), middle)), 0);
		}
		double b = bDifusa + bEspecular;
		double c = Math.abs(Operator.dotProduct(fi.getNormal(intersect),
				Operator.subP(light, intersect).scale(1 / Operator.subP(light, intersect).module())));
		a = a * b * c;
		return a;
	}

	public static boolean block(List<Figure> f, Ray r, Point light) {
		double s = 9999999;
		double t = -1;
		Point intersection = null;
		Figure fi = null;
		// Intersect figures
		for (Figure sf : figures) {
			t = sf.intersect(r.getO(), r.getD());
			if (s > t && t > 0) {
				s = t;
				fi = sf;
				intersection = Operator.addD(r.getO(), r.getD().scale(t));
			}
		}

		if (intersection == (null) || fi.getKr() > 0.0) {
			return false;
		}

		if (r.getD().scale(s + 0.000001).module() < Operator.subP(light, r.getO()).module()) {
			return true;
		}
		return false;
	}
	
	private static Color luzDirectaEspecular(Point intersect, Point light, List<Figure> figures, Figure fi, Direction ray,
			int bounds) {
		// new specular
		/******************************/
		double i = Operator.dotProduct(ray, fi.getNormal(intersect));
		ray=(Operator.subD(ray, fi.getNormal(intersect).scale(2 * i)));
		/******************************/
		Figure f = null;
		double s = 9999999;
		double t = -1;
		Point intersection = null;

		for (Figure sf : figures) {
			t = sf.intersect(intersect, ray);
			if (s > t && t > 0) {
				s = t;
				f = sf;
				// //direct light
				intersection = Operator.addD(intersect, ray.scale(t - 0.000001));
				// // Light of direct Light
			}
		}

		Color lE = Color.WHITE;
		int mC = RussianRoulette.calculate(f);
		if (mC == 2 && !f.is_light) {
			lE = luzDirectaEspecular(intersection, light, figures, f, ray, bounds+1);
		}
		else if(!f.is_light) lE = luzDirecta(intersection, light, figures, f,ray, POTENCIA);
		return lE;
	}
	
	/*
	private static void rayRefractada(Point intersect, Point light, List<Figure> figures, Figure fi, Direction ray,
			int bounds, double indexRefraction, boolean isInside) {
		System.out.println("HOLAAAA");
		Direction Ti = ray;//.scale(-1);
		double cosI = Operator.dotProduct(Ti, fi.getNormal(intersect).scale(-1));
		double indexNext;
		
		if (isInside) {
			indexNext = 1.0;/// fi.getKr();
		}
		else {
			indexNext = fi.getKr();
		}
		double n = indexRefraction / indexNext;
		double cosT2 = Math.sqrt(1.0 - indexNext*indexNext* (1.0 - cosI*cosI));
		Direction T;
		if(cosT2<=0){
			T = Operator.subD(fi.getNormal(intersect).scale(indexNext*cosI-cosT2),
					ray.scale(indexNext));
		}else {
			T=Operator.subD(ray.scale(n),
					fi.getNormal(intersect).scale(n*cosI-cosT2)
					);
		}
			Ray rayR = new Ray(Operator.addD(intersect,T.scale(0.0000001)), T);
			Figure f = null;
			double s = 9999999;
			double t = -1;
			Point intersection = null;

			for (Figure sf : figures) {
				t = sf.intersect(Operator.addD(intersect,rayR.getD().scale(0.0001)), rayR.getD());
				if (s > t && t > 0) {
					s = t;
					f = sf;
					// direct light
					intersection = Operator.addD(intersect, rayR.getD().scale(t - 0.000001));
					// Light of direct Light
				}
			}
		
			if(!isInside) {
				Photon po = new Photon(intersection, f.getColor());
				System.out.println("WIII");
				savePhotonDiff(intersection, light, figures, f, ray, bounds + 1, po);
			}
			
			
			if(f!=null) {
				if (bounds < MAX_REBOTES && !f.is_light && intersection!=null) {
					if(f.getKr()>0) {
						rayRefractada(intersection, light, figures, f, ray, bounds + 1,f.getKr(), false);
					}
				}
			}
	}
	
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
*/	
	
}
