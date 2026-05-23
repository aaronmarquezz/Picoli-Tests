package modelo;

import static modelo.TipoPago.anciano;
import static modelo.TipoPago.menor;
import static modelo.TipoPago.parado;
import static modelo.TipoPago.trabajador;

import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

public class Estado {
	// atributos sobre desarrollo
	private double capital = 0;
	private double cantidadProducidaPorTrabajador;
	private final double edadJubilacion = 65;
	private final double edadMadurez = 18;
	private ArrayDeque<Double> historicoIncrementosDemanda;
	private int defuncionesPeridoAnterior;

	// poblacion
	private Sector<Menor> menores;
	private Sector<Adulto> trabajadores;
	private Sector<Adulto> parados;
	private Sector<Ser> ancianos;

	// prduccion
	private double totalDemandado = 0;

	public Estado() {
		super();
		menores = new SectorNoPrioritario<Menor>(menor);
		trabajadores = new SectorPrioritario<Adulto>(trabajador);
		ancianos = new SectorNoPrioritario<Ser>(anciano);
		parados = new SectorPrioritarioParados(parado);
		historicoIncrementosDemanda = new ArrayDeque<Double>(5);
	}

	public void abrirPeriodo(double porcentajeIncrementoDemanda) throws Exception {
		// 1 calcular la cantidad que debe producir el estado segun el incremento (puede
//		// ser una cantidad menor)
		if (porcentajeIncrementoDemanda < -.99 || porcentajeIncrementoDemanda > .99)
			throw new Exception();
		double objetivoProduccion = calcularCantidadAProducir(porcentajeIncrementoDemanda);
		almacenarNuevoPeriodo(porcentajeIncrementoDemanda);
//		// 2 Contratar o despedir a adultos segun sea la necesidad
		gestionarEmpleos(objetivoProduccion);
//		// 3 decidir los nacimientos en funcion de cuantas defunciones, y otras cosas,
//		// hayan pasado en el periodo anterior
		gestionarNacimientos();
	}

	private void gestionarNacimientos() {
		double calcularMediaIncrementos = calcularMediaIncrementos();
		int nacimientos = (int) (this.defuncionesPeridoAnterior * (1 - calcularMediaIncrementos));
		Random random = new Random();
		for (int i = 0; i < nacimientos; i++) {
			try {
				menores.addLast(new Menor());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private double calcularMediaIncrementos() {
		double acumulador = 0;
		for (Double periodo : historicoIncrementosDemanda) {
			acumulador += periodo;
		}
		return acumulador / historicoIncrementosDemanda.size();

	}

	private void almacenarNuevoPeriodo(double porcentajeIncrementoDemanda) {
		historicoIncrementosDemanda.poll();
		historicoIncrementosDemanda.offer(porcentajeIncrementoDemanda);
	}

	private void gestionarEmpleos(double objetivoProduccion) {
		double totalProducidoPeriodoActual = (trabajadores.size() * cantidadProducidaPorTrabajador);
		double diferenciaProduccionProximoPeriodo = objetivoProduccion - totalProducidoPeriodoActual;
		int diferenciaTrabajadoresNecesarios = (int) (diferenciaProduccionProximoPeriodo
				/ cantidadProducidaPorTrabajador);
		// El truco para no usar if-else
		// Parto de una premisa (condicion inicial)
		// digo que diferenciaProduccionProximoPeriodo<=0 por lo tanto debo despedir
		// trabajadores
		// por eso el sector fuente (la cola de donde saco seres) debe ser trabajadores
		// y el sector que los gana, destino, son los parados.
		Sector<Adulto> fuente = trabajadores;
		Sector<Adulto> destino = parados;
		// si no es asi, entonces intercambio quien es la fuente y quien es el destino
		// Con esto respeto DRY porque si no repetiria el codigo de gestion de sectores
		// tendria que poner dos veces lo que hay dentro de intercambioSeres
		if (diferenciaProduccionProximoPeriodo > 0) {
			fuente = parados;
			destino = trabajadores;
		}
		intercambioSeres(fuente, destino, Math.abs(diferenciaTrabajadoresNecesarios));
	}

	private void intercambioSeres(Sector<Adulto> fuente, Sector<Adulto> destino, int cantidad) {
		for (int i = 0; i < cantidad; i++) {
			Adulto first = fuente.getFirst();
			destino.addLast(first);
			first.inicializaPeriodoEnEstado();
		}
	}

	private double calcularCantidadAProducir(double porcentajeIncrementoDemanda) {
		return totalDemandado *= 1 + porcentajeIncrementoDemanda;
	}

	////////////////////////////////////////////////////
	/**
	 * 1º se calcula cuanto ha producido el conjunto de los trabajadores 2º se paga
	 * a todos los seres 3º se envejece a todos los seres 4º se jubila a los adultos
	 * que han llegado a la edad de jubilación y se les quita los ahorros 5º se
	 * eliminan los seres que han muerto y se les quita los ahorros (si son adultos)
	 */
	public void cerrarPeriodo() {
		// 1 Calcular el capital
		double totalProducido = trabajadores.size() * cantidadProducidaPorTrabajador;
		this.capital += totalProducido;
		// 2 pagar a los seres
		pagar(menores, ancianos, trabajadores, parados);
		// Tendria que preguntarme si puedo pagarlo
		ArrayList<Ser> poblacion = new ArrayList<Ser>();
		poblacion.addAll(menores.getMiembros());
		poblacion.addAll(trabajadores.getMiembros());
		poblacion.addAll(parados.getMiembros());
		poblacion.addAll(ancianos.getMiembros());
		envejecer(poblacion);
		jubila(parados.getMiembros(), trabajadores.getMiembros());
		enterrar(menores.getMiembros(), parados.getMiembros(), trabajadores.getMiembros(), ancianos.getMiembros());
	}

	private void pagar(Sector<? extends Ser>... sector) {
		double deficit = 0;
		for (Sector<? extends Ser> poblacion : sector) {
			double presupuestoMaximo = poblacion.getTotalPago();
			deficit = capital - presupuestoMaximo;
			double pagoReal = poblacion.pago(deficit);
			capital -= pagoReal;
			deficit += presupuestoMaximo - pagoReal;
		}
		capital += deficit;
	}

	private boolean hayDeficit(double presupuesto) {
		return capital < presupuesto;
	}

	private double obtenerDeficit(double presupuesto) {
		return capital - presupuesto;
	}

	private double calcularPresupuesto() {
		double presupuestoMenores = menores.size() * menor.getPago();
		double presupuestoAncianos = ancianos.size() * anciano.getPago();
		double prespuestoParados = parados.size() * parado.getPago();
		double presupuestoTrabajadores = trabajadores.size() * trabajador.getPago();
		return prespuestoParados + presupuestoAncianos + presupuestoMenores + presupuestoTrabajadores;
	}

	// Pendiente para el lunes 13 abril robar a los muertos
	private void enterrar(AbstractCollection<? extends Ser>... listas) {
		this.defuncionesPeridoAnterior = 0;
		for (AbstractCollection<? extends Ser> poblacion : listas) {
			Iterator<? extends Ser> iterator = poblacion.iterator();
			while (iterator.hasNext()) {
				Ser ser = iterator.next();
				if (!ser.isVivo()) {
					iterator.remove();
					this.capital+=ser.entregarAlEstado();
					defuncionesPeridoAnterior++;
				}
			}
		}
	}

	private void jubila(AbstractCollection<Adulto>... listas) {
		for (AbstractCollection<Adulto> lista : listas) {
			Iterator<Adulto> iterator = lista.iterator();
			while (iterator.hasNext()) {
				Adulto adulto = iterator.next();
				if (isAnciano(adulto)) {
					this.capital += adulto.getAhorros();
					iterator.remove();
					try {
						ancianos.getMiembros().add(new Ser(adulto));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private boolean isAnciano(Adulto adulto) {
		return adulto.getEdadActual() >= edadJubilacion;
	}

	private void envejecer(ArrayList<? extends Ser> lista) {
		for (Ser ser : lista) {
			ser.envejecer();
		}
	}

	public AbstractCollection<Menor> getMenores() {
		return menores.getMiembros();
	}

	public AbstractCollection<Adulto> getTrabajadores() {
		return trabajadores.getMiembros();
	}

	public AbstractCollection<Adulto> getParados() {
		return parados.getMiembros();
	}

	public AbstractCollection<Ser> getAncianos() {
		return ancianos.getMiembros();
	}

	public double getCapital() {
		return capital;
	}

	public void setCapital(double capital) {
		this.capital = capital;
	}
}
