package tumblstats;

import java.sql.SQLException;
import java.util.GregorianCalendar;

import tumblib.Post;

/**
 * TimeStats figures out what hour of the day each post occurred at.  It
 * then accumulates this data into number of posts for each hour of the 
 * day amongst the posts in postArray.      
 * @author Yeison Rodriguez
 *
 */
public class TimeStats extends Stats{
	
	public TimeStats(Post[] postArray){
		super(postArray);
	}
	
	@Override
	protected void setData(){
		GregorianCalendar calendar = new GregorianCalendar();
		//An array of 24 elements, one for each hour.
		int [] numberOfPosts = new int[24];
		//Determine the hour of day that each post occurred.
		for(int i = 0; i < postArray.length; i++){
			calendar.setTime(postArray[i].getDate());
			//Add 1 to the corresponding hour.
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
			//Send the batch to the database.
			prep.executeBatch();
//			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

	}
}
