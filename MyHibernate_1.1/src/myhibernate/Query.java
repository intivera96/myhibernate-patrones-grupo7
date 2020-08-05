package myhibernate;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.text.html.parser.Entity;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import myhibernate.ann.Column;
import myhibernate.ann.Id;
import myhibernate.ann.JoinColumn;
import myhibernate.ann.Table;
import myhibernate.demo.*;
import myhibernate.interceptor.Interceptor;
import myhibernate.interceptor.ProxyFactory;

public class Query
{
	public String consulta;
	
	public void setConsulta(String consulta){
		this.consulta = consulta;
	}
	
	public String getConsulta(){
		return consulta;
	}
	
	public void setParameter(String pName,Object pValue)
	   {
		   String parametro= new String();
		   
		   if(!pValue.getClass().equals(Date.class))
			   parametro = String.valueOf(pValue);
		   else
		   {
			   Format formatter = new SimpleDateFormat("yyyy-mm-dd");
			   parametro = formatter.format(pValue);
		   }
		   
		   parametro = "\'" + parametro + "\'";
		   
		   for(int i=0;i<consulta.length();i++)
		   {
			   
			   String subconsulta = consulta.substring(i,i + pName.length() + 1);
			   
			   if(subconsulta.equals(":" + pName))
			   {
					  consulta=consulta.substring(0,i) + parametro
							  +consulta.substring(i+pName.length()+1);
					  break;
			   }
		   }
	   }
	
	public <T> List<T> getResultList()
	{
		ResultSet resultado = null;
		List<T> listaResultados = new ArrayList<T>();
		try
		{
			//No supe como usar la libreria
			ArrayList<Class<T>> clasesCargadas = new ArrayList <Class<T>>();
			
			clasesCargadas.add((Class<T>)DetalleOrden.class);
			clasesCargadas.add((Class<T>)PromocionVigencia.class);
			clasesCargadas.add((Class<T>)Categoria.class);
			clasesCargadas.add((Class<T>)Cliente.class);
			clasesCargadas.add((Class<T>)Orden.class);
			clasesCargadas.add((Class<T>)Producto.class);
			clasesCargadas.add((Class<T>)Promocion.class);
			clasesCargadas.add((Class<T>)PromocionProducto.class);
			clasesCargadas.add((Class<T>)Empleado.class);
			clasesCargadas.add((Class<T>)Proveedor.class);
			clasesCargadas.add((Class<T>)ProveedorCategoria.class);
			clasesCargadas.add((Class<T>)TipoCliente.class);
			//
			
			List<String> from = new ArrayList<String>();
			
			HashMap<String, String> alias = new HashMap<String, String>();
			
			List<String> where = new ArrayList<String>();
			
			String palabra = new String();
			String pClave = new String();
			
			while(consulta.indexOf(" ") != -1)
			{
				palabra = consulta.substring(0,consulta.indexOf(" "));
				consulta = consulta.substring(consulta.indexOf(" ") + 1, consulta.length());
				int taminicialfrom = from.size();
								
				if(palabra.equalsIgnoreCase("FROM") || palabra.equalsIgnoreCase("JOIN") || palabra.equalsIgnoreCase("WHERE")
						|| palabra.equalsIgnoreCase("AS"))
				{
					pClave = palabra;
				}
				else
				{
					switch(pClave)
					{
						case "FROM":
							
							for(Class<?> c : clasesCargadas)
							{
								if((c.getAnnotation(Table.class).name().equalsIgnoreCase(palabra)))
								{
									from.add(palabra);
									break;
								}
							}
							if(from.size() == taminicialfrom)
							{
								alias.put(palabra,from.get(from.size() - 1));
							}
							break;
						
						case "JOIN":
							
								palabra = palabra.substring(palabra.lastIndexOf(".") + 1);
								from.add(0,palabra);
							
								palabra = consulta.substring(0,consulta.indexOf(" "));
								consulta = consulta.substring(consulta.indexOf(" ") + 1, consulta.length());
							
								if(palabra.equalsIgnoreCase("FROM") || palabra.equalsIgnoreCase("JOIN") || palabra.equalsIgnoreCase("WHERE")
										|| palabra.equalsIgnoreCase("AS"))
								{
									pClave = palabra;
								}
								else
								{
									alias.put(palabra,from.get(0));
								}
							break;
							
						case "AS":
							alias.put(palabra,from.get(from.size() - 1));
							break;
							
						case "WHERE":
							consulta = palabra.replace(" ","") + consulta.replace(" ","");
							
							while((consulta.indexOf("AND") != -1) || (consulta.indexOf("OR") != -1))
							{
								if((consulta.indexOf("AND") != -1) && (consulta.indexOf("OR") != -1) )
								{
									if(consulta.indexOf("AND") < consulta.indexOf("OR"))
									{
										where.add((consulta.substring(0, consulta.indexOf("AND"))));
										where.add("AND");
										consulta = consulta.substring(consulta.indexOf("AND") + 3);
										break;
									}
									else
									{
										where.add((consulta.substring(0, consulta.indexOf("OR"))));
										where.add("OR");
										consulta = consulta.substring(consulta.indexOf("OR") + 2);
										break;
									}
								}
								else
								{
									if((consulta.indexOf("AND") != -1))
									{
										where.add(consulta.substring(0, consulta.indexOf("AND")));
										where.add("AND");
										consulta = consulta.substring(consulta.indexOf("AND") + 3);
										break;
									}
									else
									{
										if((consulta.indexOf("OR") != -1))
										{
											where.add(consulta.substring(0, consulta.indexOf("OR")));
											where.add("OR");
											consulta = consulta.substring(consulta.indexOf("OR") + 2);
											break;
										}											
									}
							
								}
								
							}
							
							where.add(consulta);
							consulta = "";
							break;
							
						default:
							break;
							
					}
				}
				
				if(consulta.indexOf(" ") == -1) consulta += " ";
				if(consulta.indexOf(" ") == 0)consulta = "";
			}
			
			consulta = generarSQL(from.get(from.size() - 1), clasesCargadas, where, alias);
		
			Connection conexion = ConexionBuilder.buildConexion();
			 resultado = MyHibernate.obtenerResultado(consulta, conexion);

			
			while(resultado.next())
			{
				T instanciaGenerica = (T)crearObjeto(Class.forName("myhibernate.demo." + from.get(from.size() - 1)),
																resultado,
																Class.forName("myhibernate.demo." + from.get(from.size() - 1)).getDeclaredFields(),
																from
																);
				
				listaResultados.add(instanciaGenerica);
				
			}
		if(listaResultados.isEmpty())
		{
			try
			{
				throw new Exception("No se pudo obtener ningún resultado para la consulta.");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		resultado.close();
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
		
		}
		return listaResultados;
	}

	private <T> String generarSQL(String from, ArrayList<Class<T>> clasesCargadas, List<String> where, HashMap<String, String> alias)
	{
		String selectsql = "SELECT ";
		String fromsql = " FROM ";
		
		String wheresql = " WHERE ";
		String parentesis = new String();
		
			for(Class<?> c : clasesCargadas)
			{
				if(c.getAnnotation(Table.class).name().equalsIgnoreCase(from))
				{
					selectsql += selectSQL(c);
					fromsql += c.getSimpleName() + fromSQL(c);
					break;
				}
			}
			
			for(String palabra: where)
			{
				if(palabra.equalsIgnoreCase("OR") || palabra.equalsIgnoreCase("AND"))
				{
					wheresql += " " + palabra + " ";
				}
				else
				{
					if(palabra.startsWith("("))parentesis = "(";
					while(palabra.indexOf(".") != palabra.lastIndexOf("."))
					{
						palabra = palabra.substring(palabra.indexOf(".") + 1);
					}
					
					String al = palabra.substring(0, palabra.indexOf("."));
					
					if(alias.containsKey(al))
					{
						String aux = palabra.substring(0, palabra.indexOf("."));
						aux = alias.get(aux);
						palabra = aux + palabra.substring(palabra.indexOf("."));
					}
					
					wheresql += parentesis + palabra;
				}
			}
			
		return selectsql + fromsql + wheresql;
	}
	
	private <T> String fromSQL(Class<T> c)
	{		
		String from = new String();
		List<String> joins = MyHibernate.joiner(c.getDeclaredFields(),c.getAnnotation(Table.class).name());
		
		for(String join: joins)from += join;
		
		return from;
	}

	public <T> String selectSQL(Class<T> c)
	{
		String select = new String();
		List<String> camposSelect = MyHibernate.introspeccion(c.getDeclaredFields(),c.getAnnotation(Table.class).name());
		
		for(String campo: camposSelect)select += campo;
		
		select = select.substring(1);
		
		return select;
	}
	
	   static <T> T crearObjeto(Class<T> c, ResultSet rs, Field[] fs, List<String> from)
		{
		   T obj = null;
		   
		   from.remove(from.size() - 1);
		   
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
						   if(!from.contains(campo.getType().getSimpleName().toLowerCase()))
						   {
							   Field[] fields = campo.getType().getDeclaredFields();
							   
							   for(Field f: fields)if(f.isAnnotationPresent(Id.class))
								   columna = f.getAnnotation(Column.class).name() + "_" + campo.getType().getSimpleName().toLowerCase();
							   
							   set.invoke(obj, ProxyFactory.newInstance(campo.getType(),Interceptor.class, rs.getInt(columna)));
							   break;
						   }
						   if(campo.getType().getSimpleName().equals(c.getSimpleName()))
						   {
								   set.invoke(obj, ProxyFactory.newInstance(campo.getType(),Interceptor.class, rs.getInt(columna)));
						   }
						   else
						   {
							   set.invoke(obj,crearObjeto(campo.getType(), rs, campo.getType().getDeclaredFields(), from));
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

}
