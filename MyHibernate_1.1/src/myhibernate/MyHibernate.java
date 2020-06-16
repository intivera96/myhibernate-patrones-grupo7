package myhibernate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Interceptor.Interceptor;
import Interceptor.ProxyFactory;
import myhibernate.ann.Column;
import myhibernate.ann.Id;
import myhibernate.ann.JoinColumn;
import myhibernate.ann.ManyToOne;
import myhibernate.ann.Table;

public class MyHibernate
{
	//ENREGA 1: FIND
   public static <T> T find(Class<T> clazz, int id)
   {
	   if(id == 0) return null;
	   
	   String tabla = null;
	   ResultSet resultados = null;
	   Field[] campos;
	   
	   Id ID;
	   String query = new String();
	   String where = " where ";
	   String atributoId = new String();
	   
	   T t = null;
	   
	   Connection conexion = ConexionBuilder.buildConexion();
	   
	   if(clazz.getAnnotation(Table.class) != null)
	   {
		   tabla = clazz.getAnnotation(Table.class).name();
		   
		   campos = clazz.getDeclaredFields();
		   
		   for (Field campo:campos)
		   {
			   ID = campo.getAnnotation(Id.class);
			   
			   if(ID != null)
			   {
				   atributoId = campo.getAnnotation(Column.class).name();
				   where += tabla + "." + atributoId + " = ";
			   }
		   }
		   
		   if(id == 1 && tabla.toLowerCase().equals("empleado"))
		   {
			   query = "select ";
			   
			   for(Field campo: campos)
			   {
				   if(campo.getAnnotation(Id.class) == null) query += ", ";
				   if(campo.getAnnotation(Column.class) != null)
				   query += tabla + "." + campo.getAnnotation(Column.class).name() + 
						   " AS " + campo.getAnnotation(Column.class).name() + "_" + tabla;
				   else
					   query += tabla + "." + campo.getAnnotation(JoinColumn.class).name() + 
					   " AS " + atributoId + "_" + tabla + "1"; 
			   }
			   query += " from " + tabla + where + id;
			   
		   }
		   else query = generarQuery(tabla, campos) + where + id;
		   
		   resultados = obtenerResultado(query, conexion);
		   
	   }
	   else
	   {
		   System.out.println("No pude encontrar la tabla");
		   return null;
	   }
	   
	   try
	   {	
		   if(resultados.next())t = crearObjeto(clazz, resultados, campos);
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   finally
	   {
		try    
		{
			if(conexion != null)conexion.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		   
	   }
	   
	return t;
	   
   }

   private static <T> T crearObjeto(Class<T> c, ResultSet rs, Field[] fs)
	{
	   T obj = null;
	   String id = new String();
	   try
	   {
		   obj = c.newInstance();
		   
		   Method set = null;
		   
		   String columna;
		   
		   for(Field campo:fs)
		   {
			   set = getSetter(c.getDeclaredMethods(), campo);
			   
			   String nCampo = new String();
			   
			   if(campo.getAnnotation(Id.class) != null)id = campo.getAnnotation(Column.class).name();
			   
			   if(campo.getAnnotation(Column.class)!=null)
				   nCampo = campo.getDeclaredAnnotation(Column.class).name();
			   else
			   {
				   if(campo.getType().getSimpleName().equals(c.getSimpleName()))nCampo += id;
			   }
					
			   columna = nCampo + "_" + c.getAnnotation(Table.class).name();
			   
			   switch(campo.getType().getSimpleName())
			   {
				   case "int":
					   set.invoke(obj, rs.getInt(columna));
					   break;
					   
				   case "String":
					   String valorstring = rs.getString(columna);
					   if(valorstring != null)set.invoke(obj,valorstring);
					   else set.invoke(obj,"");
					   break;
					   
				   case "double":
					   set.invoke(obj,rs.getDouble(columna));
					   break;
					   
				   case "Date":
					   set.invoke(obj,rs.getDate(columna));
					   break;
					   
				   default:
					   if(campo.getType().getSimpleName().equals(c.getSimpleName()))
					   {
						   T objcampo = null;
						   columna += "1";
						   int var = rs.getInt(columna);
						   
						   if(var != 0)
						   {
							   objcampo = (T)ProxyFactory.newInstance(campo.getType(),Interceptor.class, rs.getInt(columna));
							   set.invoke(obj, objcampo);
						   }
						   else 
						   {
							   set.invoke(obj,objcampo);
						   }
					   }
					   else
					   {
						   set.invoke(obj,crearObjeto(campo.getType(), rs, campo.getType().getDeclaredFields()));
					   }
							break;
			   }
		   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   
	   return obj;
	}

   private static Method getSetter(Method[] dms, Field c)
   {
	   Method setter = null;
	   
	   for(Method metodo:dms)
	   {
		   if(metodo.getName().contains("set") && metodo.getName().substring(4).equals(c.getName().substring(1)))
		   {
			   setter = metodo;
			   break;
		   }		
	   }
	   
	   return setter;
   }

   private static ResultSet obtenerResultado(String q, Connection c)
   {
	   ResultSet rs = null;
	   PreparedStatement prepstate = null;
	   
	   try{   
		   prepstate = c.prepareStatement(q);
		   
		   rs = prepstate.executeQuery();
		   
		   
	   
	   }catch(Exception e){
		   
		   System.out.println("Error con: " + e);
		   
	   }
	   finally
	   {
		try    
		{
			if(prepstate != null) prepstate.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		   
	   }
	   
	   return rs;
	
}

   private static <T> List<String> introspeccion(Field[] listacampos, String tabla, String tablaAnterior)
   {
	   List <String> atrib = new ArrayList <String> ();
	   String atributoId = new String();
	   
	   for (Field campo:listacampos)
	   {
		   if(campo.getAnnotation(Id.class) != null)
		   {
			   if(tablaAnterior.equals(tabla))
			   {
				   atributoId = "," + tabla + "1" + "." + campo.getAnnotation(Column.class).name() +
						   " AS " + campo.getAnnotation(Column.class).name() + "_" + tabla + "1";
				   break;
			   }
			   else
			   {
				   atributoId = "," + tabla + "." + campo.getAnnotation(Column.class).name() + 
					   " AS " + campo.getAnnotation(Column.class).name() + "_" + tabla;
			   }
			   
		   }
		   else
		   {
			   if(campo.getAnnotation(ManyToOne.class) == null)
			   {
				   atrib.add("," + tabla + "." + campo.getAnnotation(Column.class).name() + 
						   " AS " + campo.getAnnotation(Column.class).name() + "_" + tabla);
			   }
			   else
			   {
				   Field[] listacampos2 = campo.getType().getDeclaredFields();
					   				   
				   atrib.addAll(introspeccion(listacampos2, campo.getType().getAnnotation(Table.class).name(), tabla));
			   }
		   }
	   }
	   
	   atrib.add(0,atributoId);
	   
	   return atrib;
   }

   private static String generarQuery(String tabla, Field[] listacampos)
   {  
	   String select = new String();
	   String from = tabla;
	   
	   String atributoId = new String();
	   
	   for (Field campo:listacampos)
	   {
		   if(campo.getAnnotation(Id.class) != null)atributoId = campo.getAnnotation(Column.class).name();
		   
		   if(campo.getAnnotation(ManyToOne.class) == null)
		   	{
			   select += ", " + tabla+ "." + campo.getAnnotation(Column.class).name() + " AS " + campo.getAnnotation(Column.class).name() + "_" + tabla;
		   	}
		   	else
		   	{
		   		if(!campo.getType().getAnnotation(Table.class).name().equals(tabla))
		   		{
		   			from += " JOIN " + campo.getType().getAnnotation(Table.class).name() + " ON "
					   		+ tabla + "." + campo.getAnnotation(JoinColumn.class).name() + " = "
					   		+ campo.getType().getAnnotation(Table.class).name() + "." + campo.getAnnotation(JoinColumn.class).name();
		   		
		   			List <String> tablas = joiner(campo.getType().getDeclaredFields(), campo.getType().getAnnotation(Table.class).name());
		   			
		   			for(String t:tablas)
		   			from += t;
		   		}
		   		else
		   		{
		   			from += " JOIN " + campo.getType().getAnnotation(Table.class).name() + " AS "
		   					+ campo.getType().getAnnotation(Table.class).name() + "1" + " ON "
					   		+ tabla + "." + campo.getAnnotation(JoinColumn.class).name() + " = "
					   		+ campo.getType().getAnnotation(Table.class).name() + "1" + "." + atributoId;
		   		}
		   		
		   		List <String> atributos = introspeccion(campo.getType().getDeclaredFields(), campo.getType().getAnnotation(Table.class).name(), tabla);
		   		
		   			for(String atributo:atributos)
		   				select += atributo;
		   	}
	   	}
   
	   select = select.substring(2);
	   
	   return "select " + select + " from " + from;
   }

   private static List<String> joiner(Field[] lc, String tabla)
   {
	
	   List <String> tablas = new ArrayList<String>();
	   String id = new String();
	   
		   for(Field c:lc)
		   {
			   if(c.getAnnotation(Id.class) != null)id = c.getAnnotation(Column.class).name();
			   if(c.getAnnotation(ManyToOne.class) != null)
			   {
					 if(!c.getType().getAnnotation(Table.class).name().equals(tabla))
					 {  
					   tablas.add(" JOIN " + c.getType().getAnnotation(Table.class).name() + " ON "
							   		+ tabla + "." + c.getAnnotation(JoinColumn.class).name() + " = "
							   		+ c.getType().getAnnotation(Table.class).name() + "." + c.getAnnotation(JoinColumn.class).name());
					   
					 }
					 else
					 {
						 tablas.add(" JOIN " + c.getType().getAnnotation(Table.class).name() + " AS "
								 + c.getType().getAnnotation(Table.class).name() + "1" + " ON "
								 + tabla + "." + c.getAnnotation(JoinColumn.class).name() + " = "
							   	 + c.getType().getAnnotation(Table.class).name() + "1" + "." + id);
						 break;
					 }
					 
					   tablas.addAll(joiner(c.getType().getDeclaredFields(), c.getType().getAnnotation(Table.class).name()));
			   }
		   }
	   return tablas;
   }


   //ENTREGA 2: FIND ALL
   public static <T> List<T> findAll(Class<T> clazz)
   {
	List<T> listat= new ArrayList<T>();
	ResultSet resultados = null;
	Connection conexion = ConexionBuilder.buildConexion();
	T t;
	Field[] campos;
	String tabla = null;
	
	if(clazz.getAnnotation(Table.class) != null)
	   {
		tabla = clazz.getAnnotation(Table.class).name();
		campos = clazz.getDeclaredFields();
		String query = generarQuery(tabla, campos);
		resultados = obtenerResultado(query, conexion);
	   }
	   else
	   {
		   System.out.println("No pude encontrar la tabla");
		   return null;
	   }
	
	try
	   {
		   while(resultados.next())
		   {
			   t = crearObjeto(clazz,resultados, campos);
			   listat.add(t);
		   }
		     
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   finally
	   {
		try    
		{
			if(conexion != null)conexion.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		   
	   }
	
   return listat;
   }

   return listat;
   }

   public static Query createQuery(String hql)
   {
      // PROGRAMAR AQUI
      return null;
   }

}
