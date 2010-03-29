package tumblstats;

import java.sql.SQLException;
import java.util.HashMap;

import tumblib.Post;

/**
 * This class accumulates the number of posts of each particular type in the
 * postArray given.
 * @author Yeison Rodriguez
 *
 */
public class TypeStats extends Stats {
	TypeStats(Post[] postArray){
		super(postArray);
	}

	@Override
	protected void setData() {
		try{
			//The hashmap uses the type as the key, and the frequency of
			//that type as the value.
			HashMap<String, Integer> typeMap = new HashMap<String, Integer>();
			for(int i = 0; i < postArray.length; i++){
				String type = postArray[i].getType().name();
				if(typeMap.containsKey(type))
					typeMap.put(type, typeMap.get(type) + 1);
				else
					typeMap.put(type, 1);	

				prep.setString(1, type);
				prep.setInt(2, typeMap.get(type));
				prep.addBatch();
			}
			//Send the batch of all statements to the database.
			prep.executeBatch();
		}catch(SQLException e){
			e.printStackTrace();
		}
		

	}
}