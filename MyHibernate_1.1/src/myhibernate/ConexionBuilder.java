package myhibernate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;


public class ConexionBuilder
{
	
	private static final String driver = "org.hsqldb.jdbc.JDBCDriver" ;
	private static String url = new String();
	
	public static Connection buildConexion() {
		try{
			Scanner s = new Scanner(System.in);
			
			System.out.println("Enter url of server or press enter for default url(jdbc:hsqldb:hsql://localhost:9001/xdb):");
			url = s.nextLine();
			
			Class.forName(driver);
			
			if(url.isEmpty())url = "jdbc:hsqldb:hsql://localhost:9001/xdb";
			
			return DriverManager.getConnection(url);
			
			
		} 
		catch(Exception e){
			System.out.println("Error al conectar: " + e);
		}
		return null;
	}

}
