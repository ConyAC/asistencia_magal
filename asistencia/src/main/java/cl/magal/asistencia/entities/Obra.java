package cl.magal.asistencia.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Obra {
	
	@Id
	Long id;
	
	String nombre;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
