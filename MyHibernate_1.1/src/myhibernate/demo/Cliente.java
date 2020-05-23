package myhibernate.demo;

import myhibernate.ann.Column;
import myhibernate.ann.Entity;
import myhibernate.ann.Id;
import myhibernate.ann.JoinColumn;
import myhibernate.ann.ManyToOne;
import myhibernate.ann.Table;

@Entity
@Table(name = "cliente")
public class Cliente {

	@Id
	@Column(name = "id_cliente")
	private int idCliente;
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "direccion")
	private String direccion;
	
	@ManyToOne
	@JoinColumn(name = "id_tipo_cliente")
	private TipoCliente tipoCliente;

	public int getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public TipoCliente getTipoCliente()
	{
		return tipoCliente;
	}

	public void setTipoCliente(TipoCliente tipoCliente)
	{
		this.tipoCliente = tipoCliente;
	}

	
	
}
