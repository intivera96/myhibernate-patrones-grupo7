package myhibernate.demo;

import myhibernate.ann.Column;
import myhibernate.ann.Entity;
import myhibernate.ann.Id;
import myhibernate.ann.JoinColumn;
import myhibernate.ann.ManyToOne;
import myhibernate.ann.Table;

@Entity
@Table(name="producto")
public class Producto
{
   @Id
   @Column(name="id_producto")
   private int idProducto;
   
   @Column(name="descripcion")
   private String descripcion;

   @ManyToOne
   @JoinColumn(name="id_proveedor")
   private Proveedor proveedor;
   
   @ManyToOne
   @JoinColumn(name = "id_Categoria")
   private Categoria categoria;
   
   @Column(name = "precio_unitario")
   private int precioUnitario;
   
   @Column(name = "unidades_stock")
   private int unidadesStock;
   
   @Column(name = "unidades_reposicion")
   private int unidadesReposicion;
   
   @Column(name = "flg_discontinuo")
   private int flgDiscontinuo;
   
   public int getIdProducto()
   {
      return idProducto;
   }

   public void setIdProducto(int idProducto)
   {
      this.idProducto=idProducto;
   }

   public String getDescripcion()
   {
      return descripcion;
   }

   public void setDescripcion(String descripcion)
   {
      this.descripcion=descripcion;
   }

   public Proveedor getProveedor()
   {
      return proveedor;
   }

   public void setProveedor(Proveedor proveedor)
   {
      this.proveedor=proveedor;
   }

public Categoria getCategoria() {
	return categoria;
}

public void setCategoria(Categoria categoria) {
	this.categoria = categoria;
}

public int getPrecioUnitario() {
	return precioUnitario;
}

public void setPrecioUnitario(int precioUnitario) {
	this.precioUnitario = precioUnitario;
}

public int getUnidadesStock() {
	return unidadesStock;
}

public void setUnidadesStock(int unidadesStock) {
	this.unidadesStock = unidadesStock;
}

public int getUnidadesReposicion() {
	return unidadesReposicion;
}

public void setUnidadesReposicion(int unidadesReposicion) {
	this.unidadesReposicion = unidadesReposicion;
}

public int getFlgDiscontinuo() {
	return flgDiscontinuo;
}

public void setFlgDiscontinuo(int figDiscontinuo) {
	this.flgDiscontinuo = figDiscontinuo;
}

}
