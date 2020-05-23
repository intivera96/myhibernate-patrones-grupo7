package myhibernate.demo;

import myhibernate.ann.Column;
import myhibernate.ann.Entity;
import myhibernate.ann.Id;
import myhibernate.ann.Table;

@Entity
@Table (name = "promocion")
public class Promocion {

	@Id
	@Column(name = "id_promocion")
	private int id_promocion;
	
	@Column(name = "descripcion")
	private String descripcion;

	public int getId_promocion() {
		return id_promocion;
	}

	public void setId_promocion(int id_promocion) {
		this.id_promocion = id_promocion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
}
