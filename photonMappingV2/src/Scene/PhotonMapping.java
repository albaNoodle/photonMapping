package Scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Basics.Direction;
import Basics.Operator;
import Basics.Point;
import Figures.Figure;
import Figures.Triangle;
import net.sf.javaml.core.kdtree.KDTree;

public class PhotonMapping {
	
	KDTree KDT = new KDTree(3);
	private static Direction Nb;
	private static Direction Nt;
	
	public static void sendPhotons( List<Figure> figures, Point light, int N_PHOTONS) {
		Figure f = null;
		double s = 9999999;
		double t = -1;
		Point intersection = null;
		// Intersect figures
		double cosTheta = -1;
		for (int i = 0; i < N_PHOTONS; i++) {
			Direction ray = getSample(cosTheta);
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

			Ray tt = new Ray(light, ray);
			int mC = RussianRoulette.calculate(f);
			// MonteCarlo
			Color lI = new Color(0, 0, 0);

			if (mC == 2) {
//				lI = PathTracer.luzIndirectaEspecular(intersection, light, figures, f, ray, 0);
			} else if (mC == 1) {
				savePhotonDiff(intersection, light, figures, f, ray, 0);
			}
		}
	}
	
	static void savePhotonDiff(Point intersect, Point light, List<Figure> figures, Figure fi, Direction ray,
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
		ray = rayDirectionCast;
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
		}
	}
	
	private static void saveColor(KDTree KDT, Point intersection, Color C) {
		Photon p = new Photon(intersection, C);
		double key[] = { intersection.getX(), intersection.getY(), intersection.getZ() };
		KDT.insert(key, p);
	}
	
	public static Direction getSample(double cosTheta) {
		Random r = new Random();
		double x = -1 + ( 1 + 1 ) * r.nextDouble();
		double z = -1 + ( 1 + 1 ) * r.nextDouble();
		return new Direction(x, -1, z);
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
}
