package Scene;

import java.awt.Color;

import Basics.Point;

public class Photon {
	private Point posicion;
	//private  direccion;
	private Color flujo;
	
	
	public void setPosicion(Point posicion) {
		this.posicion = posicion;
	}

	public void setFlujo(Color flujo) {
		this.flujo = flujo;
	}

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
