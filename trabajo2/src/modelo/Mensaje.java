package modelo;

public class Mensaje {
	public String contenido;
	public String k;
	public int orden;			//Decide el mensaje definitivo (Lamport)
	public int numPropuestas;
	public boolean estado;			//PROVISIONAL == FALSE | DEFINITIVO == TRUE
	public int numHilo;
	

	public Mensaje (String x, String a, int f, int c, int d, boolean e){
		this.contenido = x;
		this.k = a;
        this.orden = f;
        this.numPropuestas = c;
        this.numHilo = d;
        this.estado = e;
    }
 

    public String getContenido() {
        return contenido;
    }
    
    public void setContenido(String x) {
    	this.contenido = x;
    }
 
	
    public String getK() {
        return k;
    }
    
    public void setK(String x) {
    	this.k = x;
    }
 
    public int getOrden() {
        return orden;
    }
    
    public void setOrden(int x) {
        this.orden = x;        
    }    
    
    public int getNumPropuestas() {
    	return numPropuestas;
    }
    
    public void setNumPropuestas(int x) {
    	this.numPropuestas = x;
    }
    
    public int getNumHilo() {
    	return numHilo;
    }
    
    public void setNumHilo(int x) {
    	this.numHilo = x;
    }
    
    public boolean getEstado() {
    	return estado;
    }
    
    public void setEstado(boolean x) {
    	this.estado = x;
    }
    
}