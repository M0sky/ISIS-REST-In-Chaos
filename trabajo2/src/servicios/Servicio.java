package servicios;

import java.net.URI;
import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.UriBuilder;

import threads.Hilo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import javax.inject.Singleton;

@Singleton

@Path("hola")	//ruta a la clase
  		
public class Servicio {
	
	Hilo hilo1 = new Hilo();
	Hilo hilo2 = new Hilo();
	
	private String maquina1;
	private String maquina2;
	private String maquina3;
		
	private int pistoletazo = 0;
	
	public static String PATH1;	//Ruta para escribir los ficheros PRIMER HILO
	public static String PATH2; //Ruta para escribir los ficheros SEGUNDO HILO
	
	@Path("start")
	@GET
	public void start(@QueryParam(value = "ip_puerto") String ip_puerto, @QueryParam(value = "server_ID") String id, @QueryParam(value = "multidifusion") String multidifusion) {
			
		synchronized (getClass()) {
						
			hilo1.setMulti(Integer.parseInt(multidifusion));
			hilo2.setMulti(Integer.parseInt(multidifusion));
			
			System.out.printf("Recibido id:%d\n", Integer.parseInt(id));
			
			//PARA DIFERENCIAR LOS HILOS SEGUN LA MAQUINA (1, 2, 3, 4, 5, 6)
			hilo1.setHiloID(Integer.parseInt(id)*2 - 1);
			hilo2.setHiloID(Integer.parseInt(id)*2	  );
			
			int aux1= (Integer.parseInt(id)*2 - 1);
			int aux2= (Integer.parseInt(id)*2 	 );
		
			hilo1.setDireccion(ip_puerto);
			hilo2.setDireccion(ip_puerto);
			
			System.out.printf("Mi maquina es %s\n", ip_puerto);
			
			creaDirectorios();
			
			PATH1 = "/home/oscaruzhoo/"+Integer.toString(aux1)+"/fichero"+Integer.toString(aux1)+".log";
			PATH2 = "/home/oscaruzhoo/"+Integer.toString(aux2)+"/fichero"+Integer.toString(aux2)+".log";
			
			System.out.printf("PATH1 -> %s\nPATH2 -> %s\n", PATH1, PATH2);
			
			hilo1.setPath(PATH1);
			hilo2.setPath(PATH2);
			
			hilo1.start();
			hilo2.start();
			
		}
		
		return;
	}
	
	
	@Path("/configura")
	@GET
	public void configura( @QueryParam(value = "maquina1") String primera, @QueryParam(value = "maquina2") String segunda, @QueryParam(value = "maquina3") String tercera) throws InterruptedException {
		
		maquina1 = primera;
		maquina2 = segunda;
		maquina3 = tercera;
		
		System.out.printf("Me han llegado %s, %s, %s\n", maquina1, maquina2, maquina3);
		
		return;
	}
	
	@GET
	@Path("configMaquinas")
	public void configMaquinas() {
		
		hilo1.setMaquinas(maquina1, maquina2, maquina3);
		hilo2.setMaquinas(maquina1, maquina2, maquina3);
		
		return;
	}
	
	@GET
	@Path("sincroniza")
	public void sincroniza() {
		
		System.out.printf("He llegao al pistoletazo en 10.0.2.15\n");
		synchronized(getClass()) {
			pistoletazo++;
		}
		if(pistoletazo != 6) {
			synchronized(getClass()) {
				try {
					getClass().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			synchronized(getClass()) {
				getClass().notifyAll();
				pistoletazo = 0;
			}
		}
		return;
	}
	
	
	@Path("envios")
	@GET
	public void envios (	
			@QueryParam(value = "contenido") String contenido,
			@QueryParam(value = "k") String k,
			@QueryParam(value = "orden") int orden,
			@QueryParam(value = "numPropuestas") int numPropuestas,
			@QueryParam(value = "hiloEmisor") int hiloEmisor,
			@QueryParam(value = "hiloReceptor") int hiloReceptor,
			@QueryParam(value = "estado") boolean estado,
			@QueryParam(value = "maquina") int maquinaReceptora
			) throws InterruptedException {		

						
		switch (maquinaReceptora) {
			case 1:
				if (hiloReceptor == 1) {
					hilo1.envios(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				} else {
					hilo2.envios(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				}
				break;
			case 2:
				if (hiloReceptor == 3) {
					hilo1.envios(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				} else {
					hilo2.envios(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				}
				break;
			case 3:
				if (hiloReceptor == 5) {
					hilo1.envios(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				} else {
					hilo2.envios(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				}
				break;
		}
	}

	
	@Path("propuestas")
	@GET
	public void propuestas (	
			@QueryParam(value = "elQuePropone") String elQuePropone,
			@QueryParam(value = "contenido") String contenido,
			@QueryParam(value = "k") String k,
			@QueryParam(value = "orden") int orden,
			@QueryParam(value = "numPropuestas") int numPropuestas,
			@QueryParam(value = "hiloEmisor") int hiloEmisor,
			@QueryParam(value = "hiloReceptor") int hiloReceptor,
			@QueryParam(value = "estado") boolean estado,
			@QueryParam(value = "maquina") int maquinaReceptora) {
	
		
		switch (maquinaReceptora) {
			case 1:
				if (hiloReceptor == 1) {
					hilo1.propuestas(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				} else {
					hilo2.propuestas(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				}
				break;
			case 2:
				if (hiloReceptor == 3) {
					hilo1.propuestas(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				} else {
					hilo2.propuestas(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				}
				break;
			case 3:
				if (hiloReceptor == 5) {
					hilo1.propuestas(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				} else {
					hilo2.propuestas(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				}
				break;
		}
		return;
	}
	
	@Path("acuerdos")
	@GET
	public void acuerdos (	
			@QueryParam(value = "elQuePropone") String elQuePropone,
			@QueryParam(value = "contenido") String contenido,
			@QueryParam(value = "k") String k,
			@QueryParam(value = "orden") int orden,
			@QueryParam(value = "numPropuestas") int numPropuestas,
			@QueryParam(value = "hiloEmisor") int hiloEmisor,
			@QueryParam(value = "hiloReceptor") int hiloReceptor,
			@QueryParam(value = "estado") boolean estado,
			@QueryParam(value = "maquina") int maquinaReceptora) throws IOException {
	
		
		switch (maquinaReceptora) {
			case 1:
				if (hiloReceptor == 1) {
					hilo1.acuerdos(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				} else if (hiloReceptor == 2){
					hilo2.acuerdos(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				}
				break;
			case 2:
				 if (hiloReceptor == 3) {
					hilo1.acuerdos(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				} else if(hiloReceptor == 4) {
					hilo2.acuerdos(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				}
				break;
			case 3:
				if (hiloReceptor == 5) {
					hilo1.acuerdos(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				} else if (hiloReceptor == 6) {
					hilo2.acuerdos(contenido, k, orden, numPropuestas, hiloEmisor, hiloReceptor, estado, maquinaReceptora);
				}
				break;
		}
		return;
	}
	
	public static WebTarget crearWT(String servidor) {
		Client client = ClientBuilder.newClient();
		URI uri = UriBuilder.fromUri("http://"+servidor+"/trabajo2/rest/hola").build();
		return client.target(uri);
	}	
	
    public static void creaDirectorios(){
    	for (int x=1; x<=6; x++) {
	        File directorios = new File("/home/oscaruzhoo/"+x);
	        if (!directorios.exists()) {
	            if (directorios.mkdirs()) {
	                System.out.println("Multiples directorios fueron creados");
	            } else {
	                System.out.println("Error al crear directorios");
	            }
	        }
    	}
    }
	
}