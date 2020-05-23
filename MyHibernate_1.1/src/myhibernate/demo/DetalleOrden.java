package myhibernate.demo;

import myhibernate.ann.Column;
import myhibernate.ann.Entity;
import myhibernate.ann.Id;
import myhibernate.ann.JoinColumn;
import myhibernate.ann.ManyToOne;
import myhibernate.ann.Table;

@Entity
@Table(name = "detalle_orden")
public class DetalleOrden
{
	@Id
	@Column(name = "id_detalle_orden")
	private int idDetalleOrden;
	
	@ManyToOne
	@JoinColumn(name = "id_orden")
	private Orden orden;
	
	@ManyToOne
	@JoinColumn(name = "id_producto")
	private Producto producto;
	
	@Column (name = "cantidad")
	private int cantidad;

	public int getIdDetalleOrden() {
		return idDetalleOrden;
	}

	public void setIdDetalleOrden(int idDetalleOrden) {
		this.idDetalleOrden = idDetalleOrden;
	}

	public Orden getOrden() {
		return orden;
	}

	public void setOrden(Orden orden) {
		this.orden = orden;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	
}