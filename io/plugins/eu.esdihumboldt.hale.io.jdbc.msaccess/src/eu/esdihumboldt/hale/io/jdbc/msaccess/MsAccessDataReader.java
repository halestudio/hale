package eu.esdihumboldt.hale.io.jdbc.msaccess;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MsAccessDataReader {

	public static String getFirstData() {
		Connection con;
		Statement st;
		ResultSet rs;

		try {
			con = DriverManager.getConnection("jdbc:ucanaccess://example/transform.mdb", null, "123456");

			st = con.createStatement();
			
			System.out.println(new File(".").getAbsolutePath());

			rs = st.executeQuery("select * from emp");
			
			StringBuilder row = new StringBuilder();
			if(rs != null){
				rs.next();
					
				row.append(String.valueOf(rs.getInt(1)));
				row.append("\t");
				row.append(rs.getString(2));
				row.append("\t");
				row.append(String.valueOf(rs.getInt(3)));
				row.append("\t");
				row.append(String.valueOf(rs.getInt(4)));
				
			}

			return row.toString();

		} catch (SQLException e) {
			System.out.println(e.toString());
		}

		return null;
	}

}
//'eu.esdihumboldt.hale.io.jdbc.msaccess',