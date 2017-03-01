package co.edu.uniandes.experiments;

public class Rule {
	private int Xo;
	private int Xe;
	private int Yo;
	private int Ye;
	
	public Rule(int Xo, int Xe, int Yo, int Ye){
		this.Xo = Xo;
		this.Xe = Xe;
		this.Yo = Yo;
		this.Ye = Ye;
	}
	
	public int getXo() {
		return Xo;
	}

	public void setXo(int xo) {
		Xo = xo;
	}

	public int getXe() {
		return Xe;
	}

	public void setXe(int xe) {
		Xe = xe;
	}

	public int getYo() {
		return Yo;
	}

	public void setYo(int yo) {
		Yo = yo;
	}

	public int getYe() {
		return Ye;
	}

	public void setYe(int ye) {
		Ye = ye;
	}
}
