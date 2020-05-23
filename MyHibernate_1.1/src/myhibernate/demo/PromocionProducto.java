package myhibernate.demo;

import myhibernate.ann.Column;
import myhibernate.ann.Entity;
import myhibernate.ann.Id;
import myhibernate.ann.JoinColumn;
import myhibernate.ann.ManyToOne;
import myhibernate.ann.Table;

@Entity
@Table(name = "promocion_producto")
public class PromocionProducto {
	
	@Id
	@Column(name = "id_promocion_producto")
	private int idPromocionProducto;

	@ManyToOne
	@JoinColumn(name = "id_promocion_vigencia")
	private PromocionVigencia promocionVigencia;
	
	@ManyToOne
	@JoinColumn(name = "id_producto")
	private Producto producto;
	
	@Column(name = "descuento")
	private double descuento;

	public PromocionVigencia getPromocionVigencia() {
		return promocionVigencia;
	}

	public void setPromocionVigencia(PromocionVigencia promocion_vigencia) {
		this.promocionVigencia = promocion_vigencia;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}

	public int getIdPromocionProducto()
	{
		return idPromocionProducto;
	}

	public void setIdPromocionProducto(int id_promocion_producto)
	{
		this.idPromocionProducto = id_promocion_producto;
	}
	
	
}
