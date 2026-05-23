package modelo;

//Comparable es la comparacion del objeto con otro objeto
public class Adulto extends Ser implements Comparable<Adulto> {
	private double ahorros;
	//antiguedad
	private int periodosEnEstado=0;

	// Constructor para transición desde etapa Menor
	public Adulto(Menor menor) throws Exception {
		super(menor.getEdadActual(), menor.getEsperanzaVida(),TipoPago.parado.getNecesidadVital());
		this.ahorros = 0;
	}

	// Constructor general / directo
	public Adulto(int edadActual, double esperanzaVida) throws Exception {
		super(edadActual, esperanzaVida, TipoPago.parado.getNecesidadVital());
		this.ahorros = 0;
	}

	@Override
	public void alimentar(double cantidadEntregada) {
		this.ahorros -= necesidadVital - cantidadEntregada;
	}
	
	@Override
	public void envejecer() {
		super.envejecer();
		incrementaPeriodosEnEstado();
	}
	
	public double getNecesidad(){
	    return necesidadVital - Math.min(ahorros, necesidadVital);
	}

	public double entregarAhorros() {
        double ahorrosEntregados = ahorros;
        this.ahorros = 0;
        return ahorrosEntregados;
    }

	public double getAhorros() {
        return ahorros;
    }

	@Override
	public int compareTo(Adulto o) {
		return periodosEnEstado-o.periodosEnEstado;
	}
	
	public int getPeriodosEnEstado() {
		return periodosEnEstado;
	}

	public void setPeriodosEnEstado(int periodosEnEstado) {
		this.periodosEnEstado = periodosEnEstado;
	}
	public void incrementaPeriodosEnEstado() {
		this.periodosEnEstado++;
	}

	public void inicializaPeriodoEnEstado() {
		periodosEnEstado=0;
	}
	
	@Override
	public double entregarAlEstado() {
		return entregarAhorros();
	}
}
