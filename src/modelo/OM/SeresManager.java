package modelo.OM;

import java.util.ArrayList;
import java.util.List;
import modelo.Menor;
import modelo.TipoPago;

public class SeresManager {

	public List<Menor> getMenores(int cantidad){
		ArrayList<Menor> menores=new ArrayList<>();
		int esperanzaVida=50;
		for (int i = 0; i < cantidad; i++) {
			try {
				menores.add(new Menor(esperanzaVida));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return menores;
	}
}
