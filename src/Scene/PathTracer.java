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

public class PathTracer {

	final static int MAX_REBOTES = 3;
	public static double profImagen = 10;
	public static int antialiasing;
	public static List<Figure> figures = new ArrayList<Figure>();
	private static Direction Nb;
	private static Direction Nt;
	private static int percent = 0;
	private static int POTENCIA = 120;
	private static String imageName;

	public static void main(String[] args) {
		antialiasing = Integer. parseInt(args[0]);
		// Resolution photo
		int x = Integer. parseInt(args[1]);
		int y = Integer. parseInt(args[2]);
		imageName= args[3]+".png";
		

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
		figures.add(new Sphere(new Point(-65, -25, 70), 13, Color.MAGENTA, 0.0, 0.0, 1.2));
		figures.add(new Sphere(new Point(-15, -25, 60), 13, Color.BLUE, 0.0, 1, 0));
//		figures.add(new Sphere(new Point(-40, 0, 60), 13, Color.YELLOW, 0.2, 0.1, 0));
		// figures.add(new Triangle(new Point(-50, -10, 50),new Point(-30, -10, 40),new
		// Point(-30, -10, 50),new Direction(1, 1, 1), Color.ORANGE,0.5));
//		figures.add(new Triangle(new Point(-5, -20, 50), new Point(-10, -20, 50), new Point(-7.5, -20, 60),
//				new Direction(0, 1, 0), Color.ORANGE, 0.5, 0.0, 0.0, false));
		Triangle TLight1 = new Triangle(new Point(-35, 39.99, 45), new Point(-45, 39.99, 55), new Point(-45, 39.99, 45),
				new Direction(0, 1, 0), Color.WHITE, 0.5, 0.0, 0.0, true);
		figures.add(TLight1);
		TLight1.setIs_light(true);
		Triangle TLight2 = new Triangle(new Point(-35, 39.99, 55), new Point(-35, 39.99, 45), new Point(-45, 39.99, 55),
				new Direction(0, 1, 0), Color.WHITE, 0.5, 0.0, 0.0, true);
		figures.add(TLight2);
		TLight2.setIs_light(true);
		ArrayList<Triangle> TLights = new ArrayList<>();
		TLights.add(TLight1);
		TLights.add(TLight2);
		ArrayList<Point> lightPoints= new ArrayList<>(); 
		Point light1 = new Point(-41, 39.99, 50);
//		Point light1 = new Point(-70, 39.99, 45);
		lightPoints.add(light1);
		Point light2 = new Point(-40, 39.99, 52);
//		Point light2 = new Point(-10, 39.99, 45);
//		lightPoints.add(light2);
		Point light3 = new Point(-40, 39.99, 70);
//		lightPoints.add(light3);
		Point light4 = new Point(-40, 39.99, 35);
//		lightPoints.add(light4);
//		figures.add(new Triangle(new Point(-20, -20, 50), new Point(-30, -20, 50), new Point(-30, -20, 60),
//				new Direction(0, 1, 0), Color.ORANGE, 0.5, 0.0, 0.0, false));
//		figures.add(new Triangle(new Point(-20, -20, 50), new Point(-20, -20, 60), new Point(-30, -20, 60),
//				new Direction(0, 1, 0), Color.ORANGE, 0.5, 0.0, 0.0, false));
		// figures.add(new Triangle(new Point(-60, 10, 40),new Point(-60, 10, 50),new
		// Point(-50, 10, 40),new Direction(1, 1, 1), Color.ORANGE,0.5));
		// figures.add(new Sphere(new Point(0, -20, 50), 5, Color.BLUE, 0.8));
		Color finalColor;

		// Bucle Pixels Image/
		for (int py = 0; py < y; py++) {
			for (int px = 0; px < x; px++) {
				finalColor = render(p, sx, px, sy, py, cbm, camera.getC(),lightPoints);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	private static void percent(int py, int y) {
		if ((py * 100) / y > percent) {
			percent = (py * 100) / y;
			System.out.println((percent + 1) + "%");
		}
	}

	public static Color render(Point p, double sx, double px, double sy, double py, double[][] cbm, Point origin, ArrayList<Point> lightPoints) {
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
			Color ld;
			

			int mC = RussianRoulette.calculate(f);
			// MonteCarlo
			Color lI = new Color(0, 0, 0);

			/*
			 * if (mC == 1) { double i = Operator.dotProduct(rayPath.getD(),
			 * f.getNormal(intersection)); rayPath.setD(Operator.subD(rayPath.getD(),
			 * f.getNormal(intersection).scale(2*i))); lI = render(intersection, sx, px, sy,
			 * py, cbm, origin); }
			 */
			Ray tt = rayPath;
			for(int i=0;i<lightPoints.size();i++) {
				if (!f.is_light)
					ld = luzDirecta(intersection, lightPoints.get(i), figures, f, rayPath.getD(), POTENCIA);
				else
					ld = Color.WHITE;
				if(f.getKr()>0) {
					lI = luzIndirectaRefractada(intersection, lightPoints.get(i), figures, f, tt, 0,1,true);
				}
	//			else {
					
				else if (mC == 2) {
					lI = luzIndirectaEspecular(intersection, lightPoints.get(i), figures, f, tt, 0);
				} else if (mC == 1) {
					lI = luzIndirectaDifusa(intersection, lightPoints.get(i), figures, f, tt, 0);
				}
				//SOLO LUZ DIRECTA
//				rA = rA + (ld.getRed()/lightPoints.size());
//				bA = bA + (ld.getBlue()/lightPoints.size());
//				gA = gA + (ld.getGreen()/lightPoints.size());
				
//				//LUZ INDIRECTA + DIRECTA
				rA = rA + ((ld.getRed() + lI.getRed()) / 2)/lightPoints.size();
				bA = bA + ((ld.getBlue() + lI.getBlue()) / 2)/lightPoints.size();
				gA = gA + ((ld.getGreen() + lI.getGreen()) / 2)/lightPoints.size();
				
				//LUZ INDIRECTA
//				rA =rA + lI.getRed()/lightPoints.size();
//				bA =bA +  lI.getBlue()/lightPoints.size();
//				gA = gA +  lI.getGreen()/lightPoints.size();
			}

			// PARA PROBAR INDIRECTA SOLO DESCOMENTAR
//			rA =rA + lI.getRed();
//			bA =bA +  lI.getBlue();
//			gA = gA +  lI.getGreen();
		}

		rA = rA / (antialiasing);
		bA = bA / (antialiasing);
		gA = gA / (antialiasing);

		Color endColor = limitColor(rA, gA, bA);
		return (endColor);

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
		Color lD = luzDirecta(intersection, light, figures, f,ray.getD(), POTENCIA);
		Color lD2 = new Color(0);
		if (f!=null) {
		if (bounds < MAX_REBOTES && !f.is_light && intersection!=null ) {
			int russianRoulette = RussianRoulette.calculate(f);
			if(f.getKr()>0) {
				lD2=luzIndirectaRefractada(intersection, light, figures, f, ray, bounds + 1,1,true);
			}
			else if (russianRoulette == 1 && !f.is_light) {
				lD2 = luzIndirectaDifusa(intersection, light, figures, f, ray, bounds + 1);
//				lD = new Color((int)255/(bounds+1), 0 ,(int)255/(bounds+1));

			} else if (russianRoulette == 2 && !f.is_light) {
				lD2 = luzIndirectaEspecular(intersection, light, figures, f, ray, bounds + 1);
			}
			double rr = lD2.getRed() * f.getKd() / (2*sinTheta*cosTheta);//Operator.subP(intersection,
																// intersect).module();//(bounds + 1);// 
			double bb = lD2.getBlue() * f.getKd() / (2*sinTheta*cosTheta);//Operator.subP(intersection,
																	// intersect).module();//(bounds+1);
			double gg = lD2.getGreen() * f.getKd() / (2*sinTheta*cosTheta);//Operator.subP(intersection,
																	// intersect).module();//(bounds+1);

			double rFinal = (lD.getRed() + rr) / 2;
			double bFinal = (lD.getBlue() + bb) / 2;
			double gFinal = (lD.getGreen() + gg) / 2;

			lD = limitColor((int) rFinal, (int) gFinal, (int) bFinal);
		}
		return lD;
		}
		return Color.black;
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
		Color lE = luzDirecta(intersection, light, figures, f,ray.getD(), POTENCIA);
		Color lE2 = new Color(0);
		// while(!f.is_light) {
		// RussianRoullete
		if (bounds < MAX_REBOTES && f!=null &&!f.is_light && intersection!=null) {
			int russianRoulette = RussianRoulette.calculate(f);
			if(f.getKr()>0) {
				lE2=luzIndirectaRefractada(intersection, light, figures, f, ray, bounds + 1,1,true);
			}
			else{
				if (russianRoulette == 1) {
					lE2 = luzIndirectaDifusa(intersection, light, figures, f, ray, bounds + 1);
				}
				if (russianRoulette == 2) {
					lE2 = luzIndirectaEspecular(intersection, light, figures, f, ray, bounds + 1);
				}
			}
			lE = new Color(((lE2.getRed() + lE.getRed()) / 2), ((lE2.getGreen() + lE.getGreen()) / 2),
					((lE2.getBlue() + lE.getBlue()) / 2));
		}
		return lE;
	}
	
	private static Color luzIndirectaRefractada(Point intersect, Point light, List<Figure> figures, Figure fi, Ray ray,
			int bounds, double indexRefraction, boolean isInside) {
		Direction Ti = ray.getD();//.scale(-1);
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
			//VecF T = n * (ray.getDirection() + N * cosI) - N * sqrtf( cosT2 );
//			Direction T = Operator.subD(
//			Operator.addD(ray.getD(), fi.getNormal(intersect).scale(cosI)).scale(n),
//			fi.getNormal(intersect).scale(Math.sqrt(cosT2)));
//			Vector wt = Operaciones.restaVmenosV(Operaciones.multVporEscalar(nor, nint*cos-cost),
//					Operaciones.multVporEscalar(dir, nint));
			T = Operator.subD(fi.getNormal(intersect).scale(indexNext*cosI-cosT2),
					ray.getD().scale(indexNext));
		}else {
			T=Operator.subD(ray.getD().scale(n),
					fi.getNormal(intersect).scale(n*cosI-cosT2)
					);
		}
					//Operator.subD(f.getNormal(intersect).scale(n*cosI-cosT2),Ti.scale(n));
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
			Color lR = luzDirecta(intersection, light, figures, f, rayR.getD(), POTENCIA);
			Color lR2 = new Color(0);
//			if(isInside && f!=null) {
//				lR2=luzIndirectaRefractada(intersection, light, figures, f, rayR, bounds,1, false);
//			}
		/******************************/
			if(f!=null) {
				if (bounds < MAX_REBOTES && !f.is_light && intersection!=null) {
					int russianRoulette = RussianRoulette.calculate(f);
					if(f.getKr()>0) {
						lR2=luzIndirectaRefractada(intersection, light, figures, f, ray, bounds + 1,f.getKr(), false);
					}
					if (russianRoulette == 1) {
						lR2 = luzIndirectaDifusa(intersection, light, figures, f, ray, bounds + 1);
					}
					else if (russianRoulette == 2) {
						lR2 = luzIndirectaEspecular(intersection, light, figures, f, ray, bounds + 1);
					}
				}
			}
			lR = limitColor(
					((lR2.getRed() + lR.getRed()) ), 
					((lR2.getGreen() + lR.getGreen()) ),
				(lR2.getBlue() + lR.getBlue()));
			return lR;
	}

	public static Color luzDirecta(Point intersect, Point light, List<Figure> f, Figure fi, Direction ray, double potencia) {
		// direction de intersection a ligth
		if(intersect!=null) {
			Direction cx = Operator.subP(light, intersect);
			cx.normalize(); // normalizada
			Ray r = new Ray(intersect, cx);
			if (block(f, r, light)) {
				return Color.BLACK;
			} else {
				int rr = (int) colour(255, light, intersect, fi, fi.getColor().getRed(), ray,  potencia);
				int gg = (int) colour(255, light, intersect, fi, fi.getColor().getGreen(), ray, potencia);
				int bb = (int) colour(255, light, intersect, fi, fi.getColor().getBlue(), ray, potencia);
				Color c = limitColor(rr, gg, bb);
				return c;
			}
		}
		else {
			return Color.WHITE;
		}
		
	}

	public static boolean block(List<Figure> f, Ray r, Point light) {
		double s = 9999999;
		double t = -1;
		Point intersection = null;
		Figure fi =null;
		// Intersect figures
		for (Figure sf : figures) {
			t = sf.intersect(r.getO(), r.getD());
			if (s > t && t > 0) {
				s = t;
				fi=sf;
				intersection = Operator.addD(r.getO(), r.getD().scale(t));
			}
		}

		if (intersection == (null) || fi.getKr()>0.0) {
			return false;
		}
		
		if (r.getD().scale(s + 0.000001).module() < Operator.subP(light, r.getO()).module()) {
			return true;
		}
		return false;
	}

	public static double colour(int colorLuz, Point light, Point intersect, Figure fi, int fcolor, Direction ray,  double potencia) {
		double a = ((potencia)
				/ ((Operator.subP(light, intersect).module()) * (Operator.subP(light, intersect).module()))) * colorLuz;
		double bDifusa = 0;
		double bEspecular = 0;
		if(fi.getKd()>0) {
			bDifusa = fcolor * (fi.getKd() / Math.PI);
		}
		// difuseComponent(fi.getKd(),fi.getNormal(intersect),Operator.subP(light,intersect));
		// double c=Operator.dotProduct(fi.getNormal(intersect),
		// Operator.subP(light,intersect).scale(Operator.subP(light,intersect).module()));
//		Direction alba = Operator.subD(Operator.subP(light, intersect), ray);
//		alba.normalize();
		if(fi.getKs()>0) {
			Direction middle =Operator.subD(Operator.subP(intersect, light),ray.scale(-1));
		bEspecular = fi.getKs()* Math.pow(Math.max(0.0, Operator.dotProduct(fi.getNormal(intersect), middle)), 
				0);
				//fi.getKs()*((0.2+2)/2*Math.PI)*Math.pow(Operator.dotProduct(ray, Operator.subP(light, intersect)),0.2);
				/*
				 * Vector halfway = Operaciones.halfway(rayoLuz.getDireccion(), Operaciones.inversoV(hit.getRayo().getDireccion()));
			componenteEspecular = hit.getObjeto().getPropiedades().getEspecular() * 
					Math.pow(Math.max(0.0, Operaciones.dotProduct(hit.getNormal(), halfway)), 
					hit.getObjeto().getPropiedades().getBrillo());
				 */
		}
		double b = bDifusa + bEspecular;
		double c = Math.abs(Operator.dotProduct(fi.getNormal(intersect),
				Operator.subP(light, intersect).scale(1 / Operator.subP(light, intersect).module())));
		a = a * b* c;
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