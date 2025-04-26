package modelo.Contacto;

public class Contacto  {
	private String nickName;
	
	public Contacto(String nombre){
		this.nickName = nombre;
	}
	

	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nuevo) {
		this.nickName = nuevo;
	}
	
	@Override
	public String toString() {
	    return this.nickName; 
	}
}
