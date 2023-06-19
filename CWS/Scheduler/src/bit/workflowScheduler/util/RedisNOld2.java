//package bit.workflowScheduler.util;
//
//import redis.clients.jedis.Jedis;
//
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import bit.workflowScheduler.main.Main;
//
////一个调度器连接一次Redis，调度器死亡前关闭Redis
//public class RedisNOld2 {
//	
//	//写哈希表 key 中的字段 field 的值设为 value 
//	public static void write(String key, String field, String value, int recursionTimes) {
//		try{
//			//连接远程47.92.30.121(本地localhost)的redis服务
//			Jedis jedis = new Jedis(Main.redisIP, Main.redisPort);
////			jedis.auth("Akiraka.net");
//			jedis.hset(key, field, value);
////			System.out.println("连接redis数据库成功");
//			//使用完关闭连接
//			if(jedis != null){
//				jedis.close();
//			}
//		}
//		catch(Exception e){
//			//recursionTimes递归计数
//			if (recursionTimes < 4){
//				recursionTimes++;
//				//延时1s
//				try
//				{
//					Thread.currentThread().sleep(1000);//毫秒
//				}
//				catch(Exception ex){}
//                //递归方法write
//				write(key, field, value, recursionTimes);
//			}else{
//				Main.log.severe("写入redis(key, field, value)失败" + e);
//			}
//		}
//	}
//	
//	//写同时将多个 field-value (域-值)对设置到哈希表 key 中
//	public static void write(String key, Map<String,String> map, int recursionTimes) {
//		try{
//			
//			//连接远程47.92.30.121(本地localhost)的redis服务
//			Jedis jedis = new Jedis(Main.redisIP, Main.redisPort);
////			jedis.auth("Akiraka.net");
//			jedis.hmset(key, map);
////			System.out.println("连接redis数据库成功");
//			//使用完关闭连接
//			if(jedis != null){
//				jedis.close();
//			}
//		}
//		catch(Exception e){
//			//recursionTimes递归计数
//			if (recursionTimes < 4){
//				recursionTimes++;
//				//延时1s
//				try
//				{
//					Thread.currentThread().sleep(1000);//毫秒
//				}
//				catch(Exception ex){}
//                //递归方法write
//				write(key, map, recursionTimes);
//			}else{
//				//连接Redis失败3次,显示连接失败和异常信息
//				Main.log.severe("写入redis(key, Map(field, value))失败" + e);
//			}
//		}
//	}
//
//	
//	//读哈希表 key 中的字段 field 的值设为 value
//	public static String read(String key, String field, int recursionTimes) {
//		
//		String state = null;
//		try{
//			//连接远程47.92.30.121(本地localhost)的redis服务
//			Jedis jedis = new Jedis(Main.redisIP, Main.redisPort);
////			jedis.auth("Akiraka.net");
//			state = jedis.hget(key, field);
//
////			System.out.println("连接redis数据库成功");
//			//使用完关闭连接
//			if(jedis != null){
//				jedis.close();
//			}
//			return state;
//		}
//		catch(Exception e){
//			//recursionTimes递归计数
//			if (recursionTimes < 4){
//				recursionTimes++;
//				//延时1s
//				try
//				{
//					Thread.currentThread().sleep(1000);//毫秒
//				}
//				catch(Exception ex){}
//				//递归方法read
//				read(key, field, recursionTimes);
//			}else{
//				Main.log.severe("读redis(key, field)失败" + e);
//			}
//		}
//		return state;
//
//	}
//	
//	//读哈希表 key 中的字段 field 的值设为 value
//		public static String read(String key, int recursionTimes) {
//			
//			String state = null;
//			try{
//				//连接远程47.92.30.121(本地localhost)的redis服务
//				Jedis jedis = new Jedis(Main.redisIP, Main.redisPort);
////				jedis.auth("Akiraka.net");
//				state = jedis.get(key);
//
////				System.out.println("连接redis数据库成功");
//				//使用完关闭连接
//				if(jedis != null){
//					jedis.close();
//				}
//				return state;
//			}
//			catch(Exception e){
//				//recursionTimes递归计数
//				if (recursionTimes < 4){
//					recursionTimes++;
//					//延时1s
//					try
//					{
//						Thread.currentThread().sleep(1000);//毫秒
//					}
//					catch(Exception ex){}
//					//递归方法read
//					read(key, recursionTimes);
//				}else{
//					Main.log.severe("读redis(key, field)失败" + e);
//				}
//			}
//			return state;
//
//		}
//	
//	//写String
//	public static void write(String workflowName, String workflowFile, int recursionTimes) {
//	
//		try{
//			//连接远程47.92.30.121(本地localhost)的redis服务
//			Jedis jedis = new Jedis(Main.redisIP, Main.redisPort);
////			jedis.auth("Akiraka.net");
//			//覆盖重写
//			jedis.set(workflowName, workflowFile);
//
////			System.out.println("连接redis数据库成功");
//			//使用完关闭连接
//			if(jedis != null){
//				jedis.close();
//			}
//		}
//		catch(Exception e){
//			//recursionTimes递归计数
//			if (recursionTimes < 4){
//				recursionTimes++;
//				//延时1s
//				try
//				{
//					Thread.currentThread().sleep(1000);//毫秒
//				}
//				catch(Exception ex){}
//                //递归方法write
//				write(workflowName, workflowFile, recursionTimes);
//			}else{
////				Main.log.severe("写入redis(String, String)失败" + e);
//				System.out.println("写入redis(String, String)失败" + e);
//			}
//		}
//	}
//	     
//}