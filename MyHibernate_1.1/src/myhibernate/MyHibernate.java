package myhibernate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.reflections.Reflections;

import myhibernate.interceptor.Interceptor;
import myhibernate.interceptor.ProxyFactory;
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
	   String tabla = new String();
	   ResultSet resultados = null;
	   Field[] campos;
	   
	   String query = new String();
	   String where = " where ";
	   
	   T t = null;
	   
	   if(id == 0) return t = (T)ProxyFactory.newInstance(clazz,Interceptor.class, 0);
	   
	   Connection conexion = ConexionBuilder.buildConexion();
	   
	   if(clazz.isAnnotationPresent(Table.class))
	   {
		   tabla = clazz.getAnnotation(Table.class).name();
		   
		   campos = clazz.getDeclaredFields();
		   
		   for (Field campo:campos)
		   {			   
			   if(campo.isAnnotationPresent(Id.class))
			   {
				   where += tabla + "." + campo.getAnnotation(Column.class).name() + " = ";
				   break;
			   }
		   }
		   
		   query = generarQuery(tabla, campos) + where + id;
		   
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

   static <T> T crearObjeto(Class<T> c, ResultSet rs, Field[] fs)
	{
	   T obj = null;
	   
	   try
	   {
		   obj = c.newInstance();
		   		   
		   for(Field campo:fs)
		   {
			   Method set = c.getDeclaredMethod("set" + campo.getName().toUpperCase().charAt(0) + campo.getName().substring(1),campo.getType());
			   
			   String nCampo = new String();
			   
			   if(campo.isAnnotationPresent(Column.class))
				   nCampo = campo.getDeclaredAnnotation(Column.class).name();
			   else
			   {
				   if(campo.getType().getSimpleName().equals(c.getSimpleName()))
					   nCampo += campo.getAnnotation(JoinColumn.class).name();
			   }
					
			   String columna = nCampo + "_" + c.getAnnotation(Table.class).name();
			   
			   
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
						   int var = rs.getInt(columna);
						   
						   if(var != 0)
						   {
							   objcampo = (T)ProxyFactory.newInstance(campo.getType(),Interceptor.class, var);
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

   static ResultSet obtenerResultado(String q, Connection c)
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

   public static <T> List<String> introspeccion(Field[] listacampos, String tabla)
   {
	   List <String> atrib = new ArrayList <String> ();
	   
	   for (Field campo:listacampos)
	   {
		   if(!campo.isAnnotationPresent(ManyToOne.class))
		   {
			   atrib.add("," + tabla + "." + campo.getAnnotation(Column.class).name() + 
					   " AS " + campo.getAnnotation(Column.class).name() + "_" + tabla);
		   }
		   else
		   {
			   if(!campo.getType().getAnnotation(Table.class).name().equals(tabla))	
			   {
				   Field[] listacampos2 = campo.getType().getDeclaredFields();
				   				   
				   atrib.addAll(introspeccion(listacampos2, campo.getType().getAnnotation(Table.class).name()));
			   }
			   else
			   {
				   atrib.add("," + tabla + "." + campo.getAnnotation(JoinColumn.class).name() + 
						   " AS " + campo.getAnnotation(JoinColumn.class).name() + "_" + tabla);
			   }
		   }
	   }
	   
	   return atrib;
   }

   private static String generarQuery(String tabla, Field[] listacampos)
   {  
	   String select = new String();
	   String from = tabla;
	   
	   for (Field campo:listacampos)
	   {		   
		   if(campo.isAnnotationPresent(ManyToOne.class))
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
		   		
		   			List <String> atributos = introspeccion(campo.getType().getDeclaredFields(), campo.getType().getAnnotation(Table.class).name());
		   		
		   			for(String atributo:atributos)
		   				select += atributo;
		   		}
		   		else
		   		{
		   			select += "," + tabla + "." + campo.getAnnotation(JoinColumn.class).name() + 
		   					" AS " + campo.getAnnotation(JoinColumn.class).name() + "_" + tabla;
		   		}
		   	}
	   	}
   
	   select = select.substring(2);
	   
	   return "select " + select + " from " + from;
   }

   public static List<String> joiner(Field[] lc, String tabla)
   {
	
	   List <String> tablas = new ArrayList<String>();
	   
		   for(Field c:lc)
		   {
			   if(c.isAnnotationPresent(ManyToOne.class) && !c.getType().getAnnotation(Table.class).name().equals(tabla))
			   {
					   tablas.add(" JOIN " + c.getType().getAnnotation(Table.class).name() + " ON "
							   		+ tabla + "." + c.getAnnotation(JoinColumn.class).name() + " = "
							   		+ c.getType().getAnnotation(Table.class).name() + "." + c.getAnnotation(JoinColumn.class).name());
					   
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
	String tabla = new String();
	
	if(clazz.isAnnotationPresent(Table.class))
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

   //ENTREGA 3: HQL
   public static Query createQuery(String hql)
   {
      Query nuevoQuery = new Query();
      nuevoQuery.setConsulta(hql);
      return nuevoQuery;
      
   }

}
