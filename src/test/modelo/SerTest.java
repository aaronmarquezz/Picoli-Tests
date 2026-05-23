package test.modelo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import modelo.Adulto;
import modelo.Menor;
import modelo.Ser;

//sin lauch , no trabja con los tests

public class SerTest {

    @Test
    public void testMenorCrecerSaludable() {
        double necesidadVital = 100.0;
        Menor menor = null;
		try {
			menor = new Menor(80);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        for (int i = 0; i < 10; i++) {
            menor.alimentar(necesidadVital);
            menor.envejecer();
        }

        assertEquals(10, menor.getEdadActual(), 0, "Edad menor saludable");
        assertEquals(55.5, menor.getFactorDesarrollo(), 0.01, "Factor de desarrollo saludable");
        assertEquals(80.0, menor.getEsperanzaVida(), 0.01, "Esperanza de vida intacta");
    }

    @Test
    public void testMenorMuertePorDeficit() {
        double necesidadVital = 100.0;
        Menor menor = null;
		try {
			menor = new Menor(80);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        for (int i = 0; i < 18; i++) {
            menor.alimentar(necesidadVital / 2.0);
            menor.envejecer();
        }

        double factorEsperado = 18 * (5.55 * 0.5);
        assertEquals(factorEsperado, menor.getFactorDesarrollo(), 0.01, "Factor deficiente");
        assertFalse(menor.getFactorDesarrollo() >= 55, "No debería ser adulto");
    }

    @Test
    public void testAdultoEmpleado() {
        Adulto adulto = null;
		try {
			adulto = new Adulto(18, 80);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        adulto.alimentar(200.0);

        assertEquals(100.0, adulto.getAhorros(), 0.01, "Ahorro correcto");
        assertEquals(80.0, adulto.getEsperanzaVida(), 0.01, "Esperanza intacta empleado");
    }

    @Test
    public void testAdultoParoConAyudaDelEstado() {
        Adulto adulto = null;
		try {
			adulto = new Adulto(18, 80);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        adulto.alimentar(50.0);

        assertEquals(0.0, adulto.getAhorros(), 0.01, "Ahorro consumido por estado");
        assertEquals(80.0, adulto.getEsperanzaVida(), 0.01, "Esperanza intacta paro state");
    }

    @Test
    public void testAdultoParoVidaDecrecePorDeficit() {
        Adulto adulto = null;
		try {
			adulto = new Adulto(18, 80.0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        adulto.alimentar(20.0);

        assertEquals(0.0, adulto.getAhorros(), 0.01, "Consumido todo ahorro");
        assertEquals(79.7, adulto.getEsperanzaVida(), 0.01, "Esperanza decrecida 30%");
    }

    @Test
    public void testAncianoPierdeHastaUnPeriodo() {
        Ser anciano = null;
		try {
			anciano = new Ser(65, 80.0, 50.0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertEquals(50.0, anciano.getNecesidadVital(), 0.01, "Necesidad anciano");

        anciano.alimentar(25.0);
        assertEquals(79.5, anciano.getEsperanzaVida(), 0.01, "Esperanza bajada 50%");
    }

    @Test
    public void testSerPierdeHastaDosPeriodos() {
        Ser anciano = null;
		try {
			anciano = new Ser(65, 80.0, 100.0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        anciano.alimentar(5.0);

        assertEquals(78.34, anciano.getEsperanzaVida(), 0.21, "Esperanza bajó casi doble");
    }

    @Test
    public void testTransicionesDeEtapas() {
        Menor menor = null;
		try {
			menor = new Menor(80);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for (int i = 0; i < 18; i++)
            menor.envejecer();
        menor.setFactorDesarrollo(60.0);

        Adulto adulto = null;
		try {
			adulto = new Adulto(menor);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertEquals(18, adulto.getEdadActual(), 0, "Edad transicion 1");
        assertEquals(80.0, adulto.getEsperanzaVida(), 0.01, "Esperanza transicion 1");

        adulto.alimentar(300.0);
        assertEquals(200.0, adulto.getAhorros(), 0.01, "Ahorro al transicionar");

        Ser anciano = null;
		try {
			anciano = new Ser(adulto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertEquals(18, anciano.getEdadActual(), 0, "Edad tr 2");
        assertEquals(50.0, anciano.getNecesidadVital(), 0.01, "Necesidad Vital tr 2");
    }
}
