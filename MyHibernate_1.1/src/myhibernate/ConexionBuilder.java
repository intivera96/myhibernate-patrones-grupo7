package myhibernate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;


public class ConexionBuilder
{
	
	private static final String driver = "org.hsqldb.jdbc.JDBCDriver" ;
	private static String url = "jdbc:hsqldb:hsql://localhost:9001/xdb";
	
	public static Connection buildConexion() {
		try{
			Scanner s = new Scanner(System.in);
			
			System.out.println("Enter url of server:");
			url = s.nextLine();
			
			Class.forName(driver);
			return DriverManager.getConnection(url);
		} 
		catch(Exception e){
			System.out.println("Error al conectar: " + e);
		}
		return null;
	}

}