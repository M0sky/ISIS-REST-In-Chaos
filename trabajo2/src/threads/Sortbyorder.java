package threads;

import java.util.Comparator;

import modelo.Mensaje;

public class Sortbyorder implements Comparator <Mensaje> {
	
	public int compare(Mensaje a, Mensaje b) {
		int x = ((Integer)a.getOrden()).compareTo(b.getOrden());
		if (x == 0) {
			x = (a.getK()).compareTo(b.getK());
		}
		return x;
	}
}
