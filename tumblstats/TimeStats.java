package tumblstats;

import java.sql.SQLException;
import java.util.GregorianCalendar;

import tumblib.Post;

public class TimeStats extends Stats{
	
	TimeStats(String tableName, String keyName, Post[] postArray){
		super(tableName, keyName, postArray);
	}
	
	@Override
	protected void setData(){
		GregorianCalendar calendar = new GregorianCalendar();
		int [] numberOfPosts = new int[24];
		for(int i = 0; i < postArray.length; i++){
			calendar.setTime(postArray[i].getDate());
			numberOfPosts[calendar.get(calendar.HOUR_OF_DAY)] += 1;
		}

		try{
			int hour = 0;
			while(hour < numberOfPosts.length){
				if(numberOfPosts[hour] != 0){
					//The first parameter in setInt is the column index. 
					prep.setInt(1, hour);
					prep.setInt(2, numberOfPosts[hour]);
					prep.addBatch();
				}
				hour++;
			}

//			conn.setAutoCommit(false);
			prep.executeBatch();
//			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

	}
}
