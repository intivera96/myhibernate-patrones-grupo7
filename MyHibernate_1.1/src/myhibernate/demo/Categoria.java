package myhibernate.demo;

import myhibernate.ann.Column;
import myhibernate.ann.Entity;
import myhibernate.ann.Id;
import myhibernate.ann.Table;

@Entity
@Table(name = "categoria")
public class Categoria {

	@Id
	@Column(name = "id_categoria")
	private int idCategoria;
	
	@Column(name = "descripcion")
	private String descripcion;

	public int getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(int categoria) {
		this.idCategoria = categoria;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
}
