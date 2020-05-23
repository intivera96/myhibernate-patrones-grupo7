package myhibernate.demo;

import myhibernate.ann.Column;
import myhibernate.ann.Entity;
import myhibernate.ann.Id;
import myhibernate.ann.Table;

@Entity
@Table(name = "tipo_cliente")
public class TipoCliente {

	@Id
	@Column(name = "id_tipo_cliente")
	private int idTipoCliente;
	
	@Column(name = "descripcion")
	private String descripcion;

	public int getIdTipoCliente() {
		return idTipoCliente;
	}

	public void setIdTipoCliente(int idCliente) {
		this.idTipoCliente = idCliente;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
