package modelo;

public class Ser {
	protected int edadActual;
	protected double esperanzaVida;
	protected double necesidadVital;
	private static final int esperanzaVidaMaxima = 120;
	private static final double necesidadVitalBase = 100;

	public Ser(double esperanzaVida) throws Exception {
		if(esperanzaVida>esperanzaVidaMaxima) throw new Exception();
		this.edadActual = 0;
		this.esperanzaVida = esperanzaVida;
		this.necesidadVital = TipoPago.anciano.getNecesidadVital();
	}

	public Ser(int edadActual, double esperanzaVida, double necesidadVital) throws Exception {
		this(esperanzaVida);
		this.edadActual = edadActual;
		this.necesidadVital=necesidadVital;
	}

	public Ser(Adulto adulto) throws Exception {
		this(adulto.getEdadActual(), adulto.getEsperanzaVida(),adulto.getNecesidadVital());
	}

	public void envejecer() {
		edadActual++;
	}

	public void alimentar(double cantidadEntregada) {
		double proporcion = cantidadEntregada / necesidadVital;

		if (proporcion < 1.0) {
			if (proporcion >= 0.30) {
				// Entre 99% y 30%, pierde hasta un periodo de forma proporcional a su déficit
				// en ese tramo
				esperanzaVida -= (1.0 - proporcion);
			} else {
				// Del 29% al 0%, pierde hasta 2 periodos vitales extra si baja de ese umbral
				double proporcionEnTramo = 1.0 - (proporcion / 0.29);
				esperanzaVida -= (1.0 + proporcionEnTramo);
			}
		}
	};

	public int getEdadActual() {
		return edadActual;
	}

	public double getEsperanzaVida() {
		return esperanzaVida;
	}

	public double getNecesidadVital() {
		return necesidadVital;
	}

	public boolean isVivo() {
		return edadActual < esperanzaVida;
	}

	public static int getEsperanzavidamaxima() {
		return esperanzaVidaMaxima;
	}

	public double entregarAlEstado() {
		return 0;
	}
}
