package myhibernate.demo;

import myhibernate.ann.Column;
import myhibernate.ann.Entity;
import myhibernate.ann.Id;
import myhibernate.ann.Table;

@Entity
@Table(name="proveedor")
public class Proveedor
{
   @Id
   @Column(name="id_proveedor")
   private int idProveedor;
   
   @Column(name="empresa")
   private String empresa;
   
   @Column(name ="contacto")
   private String contacto;
   
   @Column(name = "direccion")
   private String direccion;

   public int getIdProveedor()
   {
      return idProveedor;
   }

   public void setIdProveedor(int idProveedor)
   {
      this.idProveedor=idProveedor;
   }

   public String getEmpresa()
   {
      return empresa;
   }

   public void setEmpresa(String empresa)
   {
	   this.empresa=empresa;
   }
   
   public String getContacto()
   {
	   return contacto;
   }
	
   public void setContacto(String contacto)
   {
	   this.contacto = contacto;
   }
	
   public String getDireccion()
   {
	   return direccion;
   }
	
   public void setDireccion(String direccion)
   {
	   this.direccion = direccion;
   }
}