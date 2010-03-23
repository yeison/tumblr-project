package tumblstats;

import java.sql.SQLException;
import java.util.HashMap;

import tumblib.Post;

public class TypeStats extends Stats {
	TypeStats(String tableName, String keyName, Post[] postArray){
		super(tableName, keyName, postArray);
	}

	@Override
	protected void setData() {
		try{
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

			prep.executeBatch();
		}catch(SQLException e){
			e.printStackTrace();
		}
		

	}
}