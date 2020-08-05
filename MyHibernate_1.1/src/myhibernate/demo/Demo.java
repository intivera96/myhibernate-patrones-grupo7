package myhibernate.demo;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import myhibernate.MyHibernate;
import myhibernate.Query;

public class Demo
{
   public static void main(String[] args)
   {
     // primer caso: busqueda por id
	  DetalleOrden d = MyHibernate.find(DetalleOrden.class,4);
      PromocionVigencia a = MyHibernate.find(PromocionVigencia.class,10);
      Categoria b = MyHibernate.find(Categoria.class,2);
      Cliente c = MyHibernate.find(Cliente.class,5);
      Orden e = MyHibernate.find(Orden.class,1);
      Producto f = MyHibernate.find(Producto.class,14);
      Promocion g = MyHibernate.find(Promocion.class,3);
      PromocionProducto h = MyHibernate.find(PromocionProducto.class,20);
      Proveedor i = MyHibernate.find(Proveedor.class,4);
      ProveedorCategoria j = MyHibernate.find(ProveedorCategoria.class,7);
      TipoCliente k = MyHibernate.find(TipoCliente.class,2);
      Empleado l = MyHibernate.find(Empleado.class,0);
      
     // segundo caso: recuperar todas las filas
      List<Producto> lst = MyHibernate.findAll(Producto.class);
      for(Producto px:lst)
      {
         System.out.println(px.getDescripcion()+", "+px.getProveedor().getEmpresa());         
      }
      
      List<DetalleOrden> lst2 = MyHibernate.findAll(DetalleOrden.class);
      for(DetalleOrden px:lst2)
      {
         System.out.println(px.getIdDetalleOrden());  
      }
      
	  List<Proveedor> lst3 = MyHibernate.findAll(Proveedor.class);
	  for(Proveedor px:lst3)
	  {
	     System.out.println(px.getIdProveedor()+", "+px.getEmpresa());  
	  }
	     
     // tercer caso: HQL
      String hql="";
      hql+="FROM Producto p JOIN p.proveedor pv ";
      hql+="WHERE p.id_producto=:id OR (p.pv.empresa = :emp AND p.pv.id_proveedor = :idp)";
      Query q = MyHibernate.createQuery(hql);
      q.setParameter("id", 7);
      q.setParameter("emp","Sony");
      q.setParameter("idp", 5);
      List<Producto> lst4 = q.getResultList();
      for(Producto px:lst4)
      {
         System.out.println(px.getDescripcion()+", "+px.getProveedor().getEmpresa());         
      }
    
   }
}
