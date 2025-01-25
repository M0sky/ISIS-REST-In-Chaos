package threads;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import modelo.Mensaje;

public class Hilo extends Thread {
	
	private int receptor = 0, j, HiloId, multi, counter = 0;
	
	private int orden = 0;

	private String direccion, maquina1, maquina2, maquina3, path;
	
	private Semaphore semaforoAux, semaforoBuzon, semaforoEnvios, semaforoPropuestas, semaforoAcuerdos;
	
	private ArrayList <Mensaje> buzon = new ArrayList<Mensaje>();

	
	public Hilo() {
		semaforoAux = new Semaphore(1);
		semaforoBuzon = new Semaphore(1);
		semaforoEnvios = new Semaphore(1);
		semaforoPropuestas = new Semaphore(1);
		semaforoAcuerdos = new Semaphore(1);
	}
	
	
	public void run() {
		
		long espera;
		
		String aux = null;
		
		WebTarget nodo = crearWT(direccion);
		nodo.path("configMaquinas").request().get();
		
		System.out.printf("Mis maquinas son %s | %s | %s | Soy Hilo %d\n", maquina1, maquina2, maquina3, HiloId);
		
		WebTarget sincronizame = crearWT(maquina1);
		sincronizame.path("sincroniza").request().get();
		
		System.out.printf("Hilo %d ha empezado\n", HiloId);
		
		for (int x=1; x<=100; x++) {
			
			try {
				semaforoBuzon.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			
			String k = String.format("P%02d%03d", HiloId, x);
			
			Mensaje mensaje = new Mensaje("vacio", k, orden, 0, HiloId, false);
			buzon.add(mensaje);			
			semaforoBuzon.release();
			
			try {
				semaforoEnvios.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			j=1;
			while (j<=6) {	//PONER A 6 LUEGO
							
				if (j <= 2) {
					aux = maquina1; receptor = 1;
				} else if (j > 2 && j <= 4) {
					aux = maquina2; receptor = 2;
				} else if (j > 4 && j <= 6) {
					aux = maquina3; receptor = 3;
				}
				
				WebTarget destino = crearWT(aux);
								
				destino.path("envios")
				.queryParam("contenido", mensaje.contenido)
				.queryParam("k", mensaje.getK())
				.queryParam("orden", mensaje.getOrden())
				.queryParam("numPropuestas", mensaje.getNumPropuestas())
				.queryParam("hiloEmisor", HiloId)
				.queryParam("hiloReceptor", j)
				.queryParam("estado", mensaje.getEstado())
				.queryParam("maquina", receptor)
				.request(MediaType.TEXT_PLAIN).get(String.class);
				
				espera = (long)((Math.random() * (500 - 200) + 200));
				try { Thread.sleep(espera); } catch (Exception e1) { System.out.print("Fallo waitTime1"); continue; }
					
				j++;
			}   
						
			semaforoEnvios.release();
			
			espera = (long)((Math.random() * (1500 - 1000) + 1000));
			try { Thread.sleep(espera); } catch (Exception e2) { System.out.print("Fallo waitTime2"); continue; }
					
			
			if (multi == 1) {
				System.out.printf("El hilo %d se va a sincronizar\n", HiloId);
				sincronizame.path("sincroniza").request().get();
			}
			
			System.out.printf("Temina la iteraccion numero %d en hilo %d\n", x, HiloId);
			
			counter = 0;
			buzon.clear();
			
		}	
				
		System.out.printf("Hilo %d ha terminado\n", HiloId);
		return;
	}

	
	public void setHiloID(int i) {
		this.HiloId = i;
		return;
	}
	
	
	public void setDireccion(String x) {
		this.direccion = x;
	}
	
	
	public void setMaquinas (String x1, String x2, String x3) {
		this.maquina1 = x1;
		this.maquina2 = x2;
		this.maquina3 = x3;
		return;
	}
	
	
	public static WebTarget crearWT(String servidor) {
		Client client = ClientBuilder.newClient();
		URI uri = UriBuilder.fromUri("http://"+servidor+"/trabajo2/rest/hola").build();
		return client.target(uri);
	}	
	
	
	public void setPath(String x) {
		this.path = x;
	}
	

	public void setMulti(int x) {
		this.multi = x;
	}
	
	
	private void escribeFichero(String string, String PATH) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATH, true), StandardCharsets.UTF_8 ));
		    bw.write(string + "\n");
		} finally {
		    bw.close();
		}
		return;
	}
	
	//NOS LLEGAN TODOS LOS MENSAJES AL HILO
	public void envios(String contenido, String k, int orden, int numPropuestas, int hiloEmisor, int hiloReceptor, boolean estado, int maquina) {		
		
		if (multi == 0) {
			try {
				escribeFichero(k, path);
			} catch (Exception e) {
				System.out.println("Error escritura path");
			}
		} else {		
			
			String host = null;
			int condicion = 0;
			
			//LC1
			try {
				semaforoAux.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			this.orden++;
			
			semaforoAux.release();
	
			Mensaje mensaje = new Mensaje (contenido, k, this.orden, numPropuestas, hiloEmisor, estado);
					
			//AÑADIMOS AL BUZON (SI LO HEMOS AÑADIDO ANTES NO HACE FALTA ACTUALIZAR EL ORDEN PORQUE LO VAMOS A HACER EN PROPUESTAS)
			try {
				semaforoBuzon.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (HiloId != mensaje.getNumHilo()) {
				//COMPROBAMOS SI EL MENSAJE QUE NOS HA LLEGADO EXISTE YA
				for (int i=0; i<buzon.size(); i++) {
					Mensaje m = buzon.get(i);
					if (m.getK().equals(mensaje.getK())) {
						condicion = 1;
						if (this.orden > mensaje.getOrden()) {
							mensaje.setOrden(this.orden);
						}
					}
				}
				//SI NO EXISTE LO AÑADIMOS
				if (condicion == 0) {
					buzon.add(mensaje);
				}
			}
									
			semaforoBuzon.release();
			
			//MAQUINA DESTINO (DEVOLVEMOS LA PROPUESTA AL HILO QUE NOS HA MANDADO EL MENSAJE)
			if (hiloEmisor <= 2) {
				host = maquina1; receptor = 1;
			} else if(hiloEmisor > 2 && hiloEmisor <= 4) {
				host = maquina2; receptor = 2;
			} else if(hiloEmisor > 4 && hiloEmisor <= 6) {
				host = maquina3; receptor = 3;
			}
			 
			WebTarget wt = crearWT(host);
			
			//DECIMOS DE QUIEN VIENE LA PROPUESTA	
			wt.path("propuestas")
			.queryParam("contenido", mensaje.contenido)
			.queryParam("k", mensaje.getK())
			.queryParam("orden", mensaje.getOrden())
			.queryParam("numPropuestas", mensaje.getNumPropuestas())
			.queryParam("hiloEmisor", HiloId)
			.queryParam("hiloReceptor", mensaje.getNumHilo())
			.queryParam("estado", mensaje.getEstado())
			.queryParam("maquina", receptor)
			.request(MediaType.TEXT_PLAIN).get(String.class);
		}
	}

	
	public void propuestas(String contenido, String k, int orden, int numPropuestas, int hiloEmisor, int hiloReceptor, boolean estado, int maquinaReceptora) {		
		
		try {
			semaforoPropuestas.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Mensaje mensaje = null;
		String ma = null;
		int p = 1;
		
		//LC2
		try {
			semaforoAux.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (this.orden >= orden) {
			this.orden = this.orden + 1;
		} else {
			this.orden = orden + 1;
		}
		
		semaforoAux.release();
		
		//Mensaje k del buzon
		try {
			semaforoBuzon.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for(int i=0;i<buzon.size();i++){
			if (buzon.get(i).getK().equals(k)){
				mensaje=buzon.get(i);
			}
		}
		
		if (mensaje == null) {
			System.out.printf("El mensaje %s ha llegado a propuestas antes que a envios\n", k);
			mensaje = new Mensaje (contenido, k, orden, numPropuestas, hiloEmisor, estado);
			buzon.add(mensaje);
		}		
				
		//Orden mayor
		if (orden > mensaje.getOrden()){
			mensaje.setOrden(orden);
		}
		//Aumentamos numero de propuestas
		mensaje.setNumPropuestas(mensaje.getNumPropuestas()+1);
		
		semaforoBuzon.release();
		
		if (mensaje.getNumPropuestas() == 6) {		//LUEGO IGUALAR A 6
					
			while (p<=6) {							//PONER A 6 LUEGO
									
				if (p <= 2) {
					ma = maquina1; receptor = 1;
				} else if (p > 2 && p <= 4) {
					ma = maquina2; receptor = 2;
				} else if (p > 4 && p <= 6) {
					ma = maquina3; receptor = 3;
				}
				
				WebTarget wt = crearWT(ma);
					
				wt.path("acuerdos")
				.queryParam("contenido", mensaje.contenido)
				.queryParam("k", mensaje.getK())
				.queryParam("orden", mensaje.getOrden())
				.queryParam("numPropuestas", mensaje.getNumPropuestas())
				.queryParam("hiloEmisor", hiloEmisor)
				.queryParam("hiloReceptor", p)
				.queryParam("estado", mensaje.getEstado())
				.queryParam("maquina", receptor)
				.request(MediaType.TEXT_PLAIN).get(String.class);
					
				p++;
			}  
		}
		semaforoPropuestas.release();
		
	}

	
	public synchronized void acuerdos(String contenido, String k, int orden, int numPropuestas, int hiloEmisor, int hiloReceptor, boolean estado, int maquinaReceptora) throws IOException {
						
		try {
			semaforoAcuerdos.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Mensaje mensaje = null;
				
		//LC2
		try {
			semaforoAux.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		if (this.orden >= orden) {
			this.orden = this.orden + 1;
		} else {
			this.orden = orden + 1;
		}
				
		semaforoAux.release();
	
		//Mensaje k del buzon
		try {
			semaforoBuzon.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		for	(int x = 0 ; x < buzon.size(); x++ ){
			if (buzon.get(x).getK().equals(k)){
				mensaje = buzon.get(x);
			}
		}
		
		if (mensaje != null) {
			mensaje.setOrden(orden);
			mensaje.setEstado(true);
		} else {
			System.out.printf("El mensaje %s ha llegado antes a acuerdos antes que a envios %d\n", k, HiloId);
			mensaje = new Mensaje (contenido, k, orden, numPropuestas, hiloEmisor, estado);
			buzon.add(mensaje);
		}
		
		counter++;			
		semaforoBuzon.release();
		
		if (counter == 6) {
		
			//ESPERAR POR TODOS LOS ACUERDOS
			Collections.sort(buzon, new Sortbyorder());
			buzon = reverseArrayList(buzon);
			mensaje = buzon.get(0);
			escribeFichero(mensaje.getK(), path);
			System.out.printf("Hilo %d escribe %s\n", HiloId, mensaje.getK());
		}
		
		semaforoAcuerdos.release();
	}

	public ArrayList <Mensaje> reverseArrayList(ArrayList<Mensaje> alist)
    {
        // Arraylist for storing reversed elements
        ArrayList <Mensaje> revArrayList = new ArrayList <Mensaje>();
        for (int i = alist.size() - 1; i >= 0; i--) {
 
            // Append the elements in reverse order
            revArrayList.add(alist.get(i));
        }
 
        // Return the reversed arraylist
        return revArrayList;
    }
	
}
