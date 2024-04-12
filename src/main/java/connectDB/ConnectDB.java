package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	private static Connection con=null;
	private static ConnectDB instance = new ConnectDB();
	
	public static ConnectDB getInstance()
	{
		return instance;
	}
	
	public void connect() throws SQLException
	{
		String url = "jdbc:sqlserver://localhost:1433;databasename=QuanLyBanSachDB;encrypt=true;trustServerCertificate=true";
		String user = "sa";
		String pass = "sapassword";
		con = DriverManager.getConnection(url, user, pass);
	}
	
	public void disconnect()
	{
		if(con != null)
		{
			try {
				con.close();
			} catch (SQLException e) {
                            // TODO: handle exception
                            
			}
		}
	}
	
	public static Connection getConnection()
	{
		return con;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection con = ConnectDB.getInstance().getConnection();
		if(con != null)
		{
			System.out.println("Ket noi thanh cong");
		}
		else
		{
			System.out.println("Ket noi that bai");
		}
	}
}
