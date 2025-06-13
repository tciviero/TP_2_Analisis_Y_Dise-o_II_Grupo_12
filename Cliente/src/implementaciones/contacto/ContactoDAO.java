package implementaciones.contacto;

import java.util.List;

import modelo.Contacto.Contacto;

public interface ContactoDAO {
    void guardarContacto(Contacto contacto);
    List<Contacto> cargarContactos(String miUsuario);
}