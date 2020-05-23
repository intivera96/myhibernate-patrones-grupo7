package myhibernate.demo;

import myhibernate.ann.Column;
import myhibernate.ann.Entity;
import myhibernate.ann.Id;
import myhibernate.ann.JoinColumn;
import myhibernate.ann.ManyToOne;
import myhibernate.ann.Table;

import java.util.Date;

@Entity
@Table(name = "orden")
public class Orden {

	@Id
	@Column(name = "id_orden")
	private int idOrden;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name = "id_empleado")
	private Empleado empleado;
	
	@Column(name = "fecha_generada")
	private Date fechaGenerada;
	
	@Column(name = "fecha_entregada")
	private Date fechaEntregada;

	public int getIdOrden() {
		return idOrden;
	}

	public void setIdOrden(int idOrden) {
		this.idOrden = idOrden;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Date getFechaGenerada() {
		return fechaGenerada;
	}

	public void setFechaGenerada(Date fechaGenerada) {
		this.fechaGenerada = new Date(fechaGenerada.getTime());
	}

	public Date getFechaEntregada() {
		return fechaEntregada;
	}

	public void setFechaEntregada(Date fechaEntregada) {
		this.fechaEntregada = new Date(fechaEntregada.getTime());
	}
	
	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado idEmpleado) {
		this.empleado = idEmpleado;
	}
	
}
