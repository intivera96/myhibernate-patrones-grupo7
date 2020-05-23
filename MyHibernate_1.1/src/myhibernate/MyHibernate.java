package myhibernate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import myhibernate.ann.Column;
import myhibernate.ann.Id;
import myhibernate.ann.JoinColumn;
import myhibernate.ann.ManyToOne;
import myhibernate.ann.Table;

public class MyHibernate
{
   public static <T> T find(Class<T> clazz, int id)
   {
	   String tabla = null;
	   ResultSet resultados = null;
	   Field[] campos;
	   
	   int lvl = 0;
	   
	   T t = null;
	   
	   Connection conexion = ConexionBuilder.buildConexion();
	   
	   if(clazz.getAnnotation(Table.class) != null)
	   {
		   tabla = clazz.getAnnotation(Table.class).name();
		   
		   campos = clazz.getDeclaredFields();
		   
		   String query = generarQuery(tabla, campos, lvl) + id;
		   
		   resultados = obtenerResultado(query, conexion);
		   
	   }
	   else
	   {
		   System.out.println("No pude encontrar la tabla");
		   return null;
	   }
	   
	   try
	   {
		   if(resultados.next())
		   {
			   t = instanciar(clazz);
			   t = crearObjeto(clazz,resultados, campos, lvl);
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
	   
	return t;
	   
   }

private static <T> T crearObjeto(Class<T> c, ResultSet rs, Field[] fs, int lvl)
{
	T obj =instanciar(c);
	
	Method set = null;
	
	String columna;
		
		for(Field campo:fs)
		{
			set = getSetter(c.getDeclaredMethods(), campo);
			
			String nCampo = new String();
			
				try
				{
					if(campo.getAnnotation(Column.class)!=null)
						 nCampo = campo.getDeclaredAnnotation(Column.class).name();
					
					columna = nCampo + "_" + c.getAnnotation(Table.class).name() + lvl;
					
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
							if(lvl < 1)
							{
								if(campo.getType().getSimpleName().equals(c.getSimpleName()))lvl ++;
								set.invoke(obj, crearObjeto(campo.getType(), rs, campo.getType().getDeclaredFields(), lvl));
							}
							break;
					}
				}
				catch(IllegalAccessException|IllegalArgumentException|InvocationTargetException|SQLException e)
				{
					e.printStackTrace();
				}
				
				
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

private static <T> T instanciar(Class<T> c)
{
	Constructor<T> ctor;
	T obj = null;
	
	try
		{

			ctor = c.getConstructor();	
			obj = (T)ctor.newInstance();
		}
		catch(InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
		}
	   return obj;
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
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		   
	   }
	   
	   return rs;
	
}

private static <T> List<String> introspeccion(Field[] listacampos, int lvl, String tabla)
{
	List <String> atrib = new ArrayList <String> ();
	Id ID;
	String atributoId = new String();
   
	for (Field campo:listacampos)
	{
		ID = campo.getAnnotation(Id.class);
		if(ID != null)
		{
		   atributoId = "," + tabla + lvl + "." + campo.getAnnotation(Column.class).name() + 
				   " AS " + campo.getAnnotation(Column.class).name() + "_" + tabla + lvl;
		}
		else
		{
		   if(campo.getAnnotation(ManyToOne.class) == null)
		   {
			   atrib.add("," + tabla + lvl + "." + campo.getAnnotation(Column.class).name() + 
					   " AS " + campo.getAnnotation(Column.class).name() + "_" + tabla + lvl);
		   }
		   else
		   {
			   if(lvl < 1)
			   {
				   Field[] listacampos2 = campo.getType().getDeclaredFields();
				   
				   if(campo.getType().getAnnotation(Table.class).name().equals(tabla))lvl ++;
				   
				   atrib.addAll(introspeccion(listacampos2, lvl, campo.getType().getAnnotation(Table.class).name()));
				   
				   
			   }
		   }
		 }
   }
	   
   atrib.add(0,atributoId);
   
   return atrib;
}

private static String generarQuery(String tabla, Field[] listacampos, int lvl)
{
   Id ID;
   String atributoId = new String();
   
   String select = "";
   String from = tabla + " AS " + tabla + lvl;
   String where = "";
   
   for (Field campo:listacampos)
   {
	   ID = campo.getAnnotation(Id.class);
	  
	   if(ID != null)
	   {
		   atributoId = campo.getAnnotation(Column.class).name();
		   select += tabla + lvl + "." + atributoId + " AS " + atributoId + "_" + tabla + lvl;
		   where += tabla + lvl + "." + atributoId + " = ";
	   }
	   else
	   {
		   if(campo.getAnnotation(ManyToOne.class) == null)
		   {
				   select += ", " + tabla + lvl + "." + campo.getAnnotation(Column.class).name() + " AS " + campo.getAnnotation(Column.class).name() + "_" + tabla + lvl;
		   }
		   else
		   {
			   Field[] listacampos2 = campo.getType().getDeclaredFields();
			   
			   if(campo.getType().getAnnotation(Table.class).name().equals(tabla))lvl ++;
			   
			   from += " JOIN " + campo.getType().getAnnotation(Table.class).name() + " AS " + campo.getType().getAnnotation(Table.class).name() + lvl +
							" ON " + tabla + lvl + "." + campo.getAnnotation(JoinColumn.class).name()
							 + " = " + campo.getType().getAnnotation(Table.class).name() + lvl + "." + campo.getAnnotation(JoinColumn.class).name();
			  
			   List <String> tablas = joiner(listacampos2, lvl, campo.getType().getAnnotation(Table.class).name());
			  
				   for(String tabla2:tablas)
					   from += tabla2;
			   
			   List <String> atributos = introspeccion(listacampos2, lvl, campo.getType().getAnnotation(Table.class).name());
			   
			   for(String atributo:atributos)
				   select += atributo;

				   
		   }
	   }
   }
   
   return "select " + select + " from " + from +  " where " + where;
}

private static List<String> joiner(Field[] lc2, int lvl, String tabla){
	
	List <String> tablas = new ArrayList<String>();
	
	int lvlinicial = 0;
	
	if(lvl < 1)
	{
		for(Field campo2:lc2)
		{
			if(campo2.getAnnotation(ManyToOne.class) != null)
			{	
				if(campo2.getType().getAnnotation(Table.class).name().equals(tabla))lvl ++;
				
				tablas.add(" JOIN " + campo2.getType().getAnnotation(Table.class).name() + " AS " + campo2.getType().getAnnotation(Table.class).name() + lvl +
					" ON " + tabla + lvlinicial + "." + campo2.getAnnotation(JoinColumn.class).name()
					+ " = " + campo2.getType().getAnnotation(Table.class).name() + lvl + "." + campo2.getAnnotation(JoinColumn.class).name());
				
				
				tablas.addAll(joiner(campo2.getType().getDeclaredFields(), lvl, campo2.getType().getAnnotation(Table.class).name()));
			}
		}
	}
	
	return tablas;
}

public static <T> List<T> findAll(Class<T> clazz)
   {
      // PROGRAMAR AQUI
      return null;
   }

   public static Query createQuery(String hql)
   {
      // PROGRAMAR AQUI
      return null;
   }

}
