package myhibernate.demo;

import java.util.List;

import myhibernate.MyHibernate;
import myhibernate.Query;

public class Demo
{
   public static void main(String[] args)
   {
      // primer caso: busqueda por id
      PromocionVigencia a = MyHibernate.find(PromocionVigencia.class,10);
      Categoria b = MyHibernate.find(Categoria.class,2);
      Cliente c = MyHibernate.find(Cliente.class,5);
      DetalleOrden d = MyHibernate.find(DetalleOrden.class,4);
      Orden e = MyHibernate.find(Orden.class,1);
      Producto f = MyHibernate.find(Producto.class,14);
      Promocion g = MyHibernate.find(Promocion.class,3);
      PromocionProducto h = MyHibernate.find(PromocionProducto.class,20);
      Proveedor i = MyHibernate.find(Proveedor.class,4);
      ProveedorCategoria j = MyHibernate.find(ProveedorCategoria.class,7);
      TipoCliente k = MyHibernate.find(TipoCliente.class,2);
      Empleado l = MyHibernate.find(Empleado.class,3);
      System.out.println("Hola Mundo");

/*      // segundo caso: recuperar todas las filas
      List<Producto> lst = MyHibernate.findAll(Producto.class);
      for(Producto px:lst)
      {
         System.out.println(px.getDescripcion()+", "+px.getProveedor().getEmpresa());         
      }
      
      // tercer caso: HQL
      String hql="";
      hql+="FROM Producto p ";
      hql+="WHERE p.proveedor.empresa=:emp ";
      Query q = MyHibernate.createQuery(hql);
      q.setParameter("emp","Sony");
      List<Producto> lst2 = q.getResultList();
      for(Producto px:lst2)
      {
         System.out.println(px.getDescripcion()+", "+px.getProveedor().getEmpresa());         
      }
      
      
      */
    
   }
}
