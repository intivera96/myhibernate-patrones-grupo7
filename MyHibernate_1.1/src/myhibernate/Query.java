package myhibernate;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
		return null;
	}

}
