package myhibernate.demo;

import myhibernate.ann.Column;
import myhibernate.ann.Entity;
import myhibernate.ann.Id;
import myhibernate.ann.JoinColumn;
import myhibernate.ann.ManyToOne;
import myhibernate.ann.Table;

@Entity
@Table(name = "proveedor_categoria")
public class ProveedorCategoria {

	@Id
	@Column(name = "id_proveedor_categoria")
	private int idProveedorCategoria;
	
	@ManyToOne
	@JoinColumn(name = "id_proveedor")
	private Proveedor proveedor;
	
	@ManyToOne
	@JoinColumn(name = "id_categoria")
	private Categoria categoria;

	public int getIdProveedorCategoria() {
		return idProveedorCategoria;
	}

	public void setIdProveedorCategoria(int idProveedorCategoria) {
		this.idProveedorCategoria = idProveedorCategoria;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor idProveedor) {
		this.proveedor = idProveedor;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria idCategoria) {
		this.categoria = idCategoria;
	}
}
