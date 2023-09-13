package openArcanoid;

public class Vector2D {
	private double[] vect;

	public Vector2D() {
		vect = new double[]{0,0};
	}
	public Vector2D(Vector2D v) {
		vect = new double[] {v.getX(),v.getY()};
	}

	public Vector2D(double x, double y) {
		vect = new double[] {x,y};
	}

	public double getX() {
		return vect[0];
	}

	public void setX(double x) {
		vect[0] = x;
	}

	public double getY() {
		return vect[1];
	}

	public void setY(double y) {
		vect[1] = y;
	}

	public void mirror(Side axis) {
		// Mirrors the vector along the horizontal or vertical axis.
		if ((axis == Side.LEFT) || (axis == Side.RIGHT)) {
			vect[0] = -vect[0];
		} else {
			vect[1] = -vect[1];
		}
	}

	public Vector2D multBy(double d) {
		return new Vector2D(vect[0]*d,vect[1]*d);
	}
	public void add(Vector2D vector) {
		 vect[0] = vect[0]+vector.getX();
		 vect[1] = vect[1]+vector.getY();
	}

	public void rotate(double angle) {
		// rotates the vector by angle radians
		double[] tmpVect = new double[2];
		tmpVect[0] = vect[0] * Math.cos(angle) - vect[1] * Math.sin(angle);
		tmpVect[1] = vect[0] * Math.sin(angle) + vect[1] * Math.cos(angle);
		vect = tmpVect;
	}
	@Override
	public String toString() {
		return "("+vect[0]+","+vect[1]+")";
	}
}
