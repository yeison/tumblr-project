package tumblstats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import tumblib.Post;

/**
 * Abstract class that should be subclassed by types that are looking to
 * accumulate statistics on posts. 
 * @author Yeison Rodriguez
 *
 */
public abstract class Stats {

	protected String tableName; 
	protected String keyName;
	protected Post[] postArray;
	protected Statement stat = null;
	protected Connection conn = null;
	protected PreparedStatement prep = null;
	protected boolean accumulate = false;

	/**
	 * Creates a Stats object that generates stats on the posts in postArray. 
	 * @param postArray An array of Post objects containing the data to be
	 * analyzed.
	 */
	public Stats(Post[] postArray){
		setPostArray(postArray);
	}

	/**
	 * Creates a sqlite table to accumulate number of posts per "key".
	 * @param tableName The name that should be given to the sqlite table.
	 * @param keyName The name of the primary key's category.
	 */
	public void createTable(String tableName, String keyName){
		setTableName(tableName);
		setKeyName(keyName);
		/* Open a connection to the database, drop the old table if we're not 
		 * accumulating, create a table if it doesn't exist, prepare a statement 
		 * that will be executed several times.*/
		try{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:tumblr.db");
			stat = conn.createStatement();
			if(!accumulate)
				stat.executeUpdate("drop table if exists " + tableName + ";");
			stat.executeUpdate("create table if not exists " + tableName + 
					" (" + keyName + " primary key, posts);");
			prep = conn.prepareStatement("insert or replace into " + tableName +
			" values (?, ?);");
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch(SQLException e){
			e.printStackTrace();
			System.err.println("Verify SQL syntax.  Also verify " +
			"that the database exists.");
		}
		
		//setData has different implementations depending on the data type.
		setData();
		printData();
			
	}

	/**
	 * Must be implemented by any subclass.  Saves the data to the sqlite table
	 * corresponding tableName.
	 */
	protected abstract void setData();

	/**
	 * Queries the table for the data, and prints the data.
	 */
	void printData(){
		try {

			ResultSet rs = stat.executeQuery("select * from " + tableName + " " +
					"order by " + keyName + ";");
			//Format the output nicely
			System.out.printf("\t%-14.16s     %s\n", keyName, "\tposts");
			while(rs.next())
				System.out.printf("\t%-14.16s     %s\n", rs.getString(keyName), 
						"\t" + rs.getString("posts"));
			//Close the resultset and the connection to the database.
			rs.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Returns the name of the sqlite table that contains this statistic.
	 * @return The sqlite table's name.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Sets the name of the table where this statistic will be saved.
	 * @param tableName The name of the sqlite table.
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Retrieves the header for the uniqe key column utilized by this stat's 
	 * table.
	 * @return The column header for the key column.
	 */
	public String getKeyName() {
		return keyName;
	}

	/**
	 * Sets the header for the unique-key column utilized by this stat's table.
	 * @param keyName The name that should be used by the column header.
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	/**
	 * Retrieves the array containing the posts that were utilized to gather
	 * the data in this stat.
	 * @return The array of posts used by this stat.
	 */
	public Post[] getPostArray() {
		return postArray;
	}

	/**
	 * Sets the array of Posts that this stat should utilize to collect data.
	 * @param postArray The array of Posts that will be used.
	 */
	public void setPostArray(Post[] postArray) {
		this.postArray = postArray;
	}

}
