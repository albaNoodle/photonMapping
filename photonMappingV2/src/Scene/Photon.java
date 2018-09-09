package Scene;

import java.awt.Color;

import Basics.Point;

public class Photon {
	private Point posicion;
	//private  direccion;
	private Color flujo;
	
	
	public Photon(Point p, Color f) {
		this.posicion = p;
		this.flujo = f;
	}
	
	public Point getPosicion() {
		return posicion;
	}
	
	public Color getFlujo() {
		return flujo;
	}
}
