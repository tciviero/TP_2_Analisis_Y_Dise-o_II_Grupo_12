package modelo;

public class Solicitud {
	
	private int Id_Solicitud;
	
	private boolean Atendiento; //El servidor primario está atendiendo esta solicitud?
	
	private String SolicitudOriginal;
	
	public Solicitud(int id, boolean atendiendo,String solicitud){
		this.Id_Solicitud=id;
		this.Atendiento=atendiendo;
		this.SolicitudOriginal=solicitud;
		//Se podria poner fecha de arribo a servidor secundario
	}

	public int getID() {
		return this.Id_Solicitud;
	}

	public void setAtendiento(boolean atendiento) {
		Atendiento = atendiento;
	}
	
	

}
