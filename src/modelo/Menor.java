package modelo;

import java.util.Random;

public class Menor extends Ser {
    private double factorDesarrollo;

    public Menor(int esperanzaVida, double necesidadVital) throws Exception  {
        super(0,esperanzaVida, necesidadVital);
        this.factorDesarrollo = 0;
    }
   
    public Menor() throws Exception  {
		this(new Random().nextInt(Ser.getEsperanzavidamaxima()),TipoPago.menor.getNecesidadVital());
	}

	public Menor(int esperanzaVida) throws Exception {
		this(esperanzaVida,TipoPago.menor.getNecesidadVital());
	}

	@Override
    public void alimentar(double cantidadEntregada) {
        if (cantidadEntregada >= necesidadVital) {
            factorDesarrollo += 5.55;
        } else if (cantidadEntregada > 0) {
            factorDesarrollo += 5.55 * (cantidadEntregada / necesidadVital);
        }
    }

    public double getFactorDesarrollo() {
        return factorDesarrollo;
    }

    public void setFactorDesarrollo(double factorDesarrollo) {
        this.factorDesarrollo = factorDesarrollo;
    }
}
