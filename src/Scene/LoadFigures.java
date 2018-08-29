package Scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import Basics.Direction;
import Basics.Point;
import Figures.Figure;
import Figures.Plane;
import Figures.Sphere;
import Figures.Triangle;

public class LoadFigures {

	public static List<Figure> figures = new ArrayList<Figure>();

	public static List<Figure> load() {
		figures.add(new Plane(new Point(0, 1, 0), new Direction(0, -1, 0), Color.MAGENTA, 40, 0.5, 0.0)); // arriba
		figures.add(new Plane(new Point(0, -1, 0), new Direction(0, 1, 0), Color.MAGENTA, 40, 0.5, 0.0));// abajo
		figures.add(new Plane(new Point(0, 0, 1), new Direction(0, 0, -1), Color.WHITE, 90, 0.5, 0)); // fondo
		figures.add(new Plane(new Point(0, 0, 1), new Direction(0, 0, 1), Color.YELLOW, 40, 0.5, 0)); //
		figures.add(new Plane(new Point(1, 0, 0), new Direction(-1, 0, 0), Color.RED, 0, 0.5, 0)); // izq
		figures.add(new Plane(new Point(-1, 0, 0), new Direction(1, 0, 0), Color.GREEN, 80, 0.5, 0));// drcha
		// figures.add(new Sphere(new Point(-20, -20, 50), 5, Color.BLUE, 0.8));
		figures.add(new Sphere(new Point(-70, -20, 50), 5, Color.BLUE, 0.0, 1));
		figures.add(new Sphere(new Point(-40, -20, 50), 5, Color.BLUE, 0.0, 1));
		// figures.add(new Sphere(new Point(-40, -10, 50), 5, Color.RED, 1, 0));
		// figures.add(new Triangle(new Point(-50, -10, 50),new Point(-30, -10, 40),new
		// Point(-30, -10, 50),new Direction(1, 1, 1), Color.ORANGE,0.5));
		figures.add(new Triangle(new Point(-5, -20, 50), new Point(-10, -20, 50), new Point(-7.5, -20, 60),
				new Direction(0, 1, 0), Color.ORANGE, 0.5, 0.0, false));
		figures.add(new Triangle(new Point(-35, 39.99, 45), new Point(-45, 39.99, 55), new Point(-45, 39.99, 45),
				new Direction(0, 1, 0), Color.WHITE, 0.5, 0.0, true));
		figures.add(new Triangle(new Point(-35, 39.99, 55), new Point(-35, 39.99, 45), new Point(-45, 39.99, 55),
				new Direction(0, 1, 0), Color.WHITE, 0.5, 0.0, true));
		figures.add(new Triangle(new Point(-20, -20, 50), new Point(-30, -20, 50), new Point(-30, -20, 60),
				new Direction(0, 1, 0), Color.ORANGE, 0.5, 0.0, false));
		figures.add(new Triangle(new Point(-20, -20, 50), new Point(-20, -20, 60), new Point(-30, -20, 60),
				new Direction(0, 1, 0), Color.ORANGE, 0.5, 0.0, false));
		// figures.add(new Triangle(new Point(-60, 10, 40),new Point(-60, 10, 50),new
		// Point(-50, 10, 40),new Direction(1, 1, 1), Color.ORANGE,0.5));
		// figures.add(new Sphere(new Point(0, -20, 50), 5, Color.BLUE, 0.8));
		return figures;
	}
}
