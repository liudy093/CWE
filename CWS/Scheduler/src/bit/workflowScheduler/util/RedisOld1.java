package bit.workflowScheduler.util;

import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bit.workflowScheduler.main.Main;

//一个调度器连接一次Redis，调度器死亡前关闭Redis
public class RedisOld1 {
	
	private Jedis jedis = null;
	
	public void Redis() {
		
	}

//	public static void main(String[] args) {
//		List<String> str = new LinkedList<String>();
//		str = Arrays.asList("ZhuYan","ZhangYang","LiZiwei","HuangZhi");
//		write("3120190832", str, 1);
//		List<String> states = read("3120190832", 1);
//		System.out.println("存储在"+"3120190832"+"中的状态为：");
//		for (String zfc : states)
//			System.out.print(zfc + " ");
//		System.out.println();
//		
//		write("123", "456", 1);
//	}
	
//	        private static Jedis redis=new Jedis("192.168.146.101",6379);
//	        public static DeviceInfo getdeviceid(String deviceid){
//	                Boolean have = redis.exists(deviceid);
//	                if(have){
//	                        List<String> list = redis.hmget(deviceid, "appplatform", "brand", "deviceStyle", "osType");
//	                        DeviceInfo deviceInfo = new DeviceInfo();
//	                        deviceInfo.setDeviceId(deviceid);
//	                        deviceInfo.setAppPlatform(list.get(0));
//	                        deviceInfo.setBrand(list.get(1));
//	                        deviceInfo.setDeviceStyle(list.get(2));
//	                        deviceInfo.setOsType(list.get(3));
//	                        return deviceInfo;
//	                }else{
//	                        return null;
//	                }
//	        }
//	        public static void setdeviceid(String devid , String platform , String brand , String deviceStyle , String osType){
//	                Map<String,String> map=new HashMap<String, String>();
//	                map.put("appplatform",platform);
//	                map.put("brand",brand);
//	                map.put("deviceStyle",deviceStyle);
//	                map.put("osType",osType);
//	                redis.hmset(devid,map);
//	        }
//	        public static String getappversion(String deviceid,String appid,String newversion){
//	                String version = redis.hget(deviceid, appid);
//	                if(version!=null){
//	                        return version;
//	                }else{
//	                        redis.hset(deviceid,appid,newversion);
//	                        return newversion;
//	                }
//	        }

	/**
	 * 链接Redis数据库
	 * @param host
	 * @param port
	 * @param recursionTimes
	 * @return
	 */
	public void link(String host, int port, int recursionTimes) {
		try {
			this.jedis = new Jedis(host, port);
		}
		catch(Exception e){
			//recursionTimes递归计数
			if (recursionTimes < 4){
				recursionTimes++;
				//延时1s
				try
				{
					Thread.currentThread().sleep(1000);//毫秒
				}
				catch(Exception ex){}
                //递归方法链接
				link(host, port,  recursionTimes);
			}else{
				//连接Redis失败3次,显示连接失败和异常信息
				Main.log.severe("连接redis数据库失败" + e);
			}
		}
	}
	
	/**
	 * 断开数据库链接
	 */
	public void close() {
		if(this.jedis != null){
			this.jedis.close();
		}
	}
	
	//写哈希表 key 中的字段 field 的值设为 value 
	public void write(String key, String field, String value, int recursionTimes) {
		try{
			jedis.hset(key, field, value);
		}
		catch(Exception e){
			//recursionTimes递归计数
			if (recursionTimes < 4){
				recursionTimes++;
				//延时1s
				try
				{
					Thread.currentThread().sleep(1000);//毫秒
				}
				catch(Exception ex){}
                //递归方法write
				write(key, field, value, recursionTimes);
			}else{
				Main.log.severe("写入redis(key, field, value)失败" + e);
			}
		}
	}
	
	//写同时将多个 field-value (域-值)对设置到哈希表 key 中
	public void write(String key, Map<String,String> map, int recursionTimes) {
		try{
			jedis.hmset(key, map);
		}
		catch(Exception e){
			//recursionTimes递归计数
			if (recursionTimes < 4){
				recursionTimes++;
				//延时1s
				try
				{
					Thread.currentThread().sleep(1000);//毫秒
				}
				catch(Exception ex){}
                //递归方法write
				write(key, map, recursionTimes);
			}else{
				Main.log.severe("写入redis(key, Map(field, value))失败" + e);
			}
		}
	}

	
	//读哈希表 key 中的字段 field 的值设为 value
	public String read(String key, String field, int recursionTimes) {
		
		String state = null;
		try{
			state = jedis.hget(key, field);
			
			return state;
		}
		catch(Exception e){
			//recursionTimes递归计数
			if (recursionTimes < 4){
				recursionTimes++;
				//延时1s
				try
				{
					Thread.currentThread().sleep(1000);//毫秒
				}
				catch(Exception ex){}
				//递归方法read
				read(key, field, recursionTimes);
			}else{
				Main.log.severe("读redis(key, field)失败" + e);
			}
		}
		return state;
	}
	
	//写String
	public void write(String workflowName, String workflowFile, int recursionTimes) {
	
		try{
			//覆盖重写
			jedis.set(workflowName, workflowFile);
		}
		catch(Exception e){
			//recursionTimes递归计数
			if (recursionTimes < 4){
				recursionTimes++;
				//延时1s
				try
				{
					Thread.currentThread().sleep(1000);//毫秒
				}
				catch(Exception ex){}
                //递归方法write
				write(workflowName, workflowFile, recursionTimes);
			}else{
				Main.log.severe("写入redis(String, String)失败" + e);
			}
		}
	}
	     
	
/*	//写List
	public static void write(String workflowName, List<String> states, int recursionTimes) {
	
		try{
			//连接远程47.92.30.121(本地localhost)的redis服务
			Jedis jedis = new Jedis("redis-master.default", 6379);
//			Jedis jedis = new Jedis("10.0.0.9", 6379);
//			Jedis jedis = new Jedis("localhost");
//			jedis.auth("Akiraka.net");
			//覆盖重写
			jedis.del(workflowName);
			for (String s : states)
				jedis.rpush(workflowName, s);
//			System.out.println("连接redis数据库成功");
			//使用完关闭连接
			if(jedis != null){
				jedis.close();
			}
		}
		catch(Exception e){
			//recursionTimes递归计数
			if (recursionTimes < 4){
				recursionTimes++;
				//延时1s
				try
				{
					Thread.currentThread().sleep(1000);//毫秒
				}
				catch(Exception ex){}
                //递归方法write
				write(workflowName, states, recursionTimes);
			}else{
				//连接Redis失败3次,显示连接失败和异常信息
//				System.out.println("连接redis数据库失败");
//				System.out.println(e);
				Main.log.severe("连接redis数据库失败" + e);
			}
		}
	}*/
	
	//写String
//	public static void write(String workflowName, String workflowFile, int recursionTimes) {
//	
//		try{
//			//连接远程47.92.30.121(本地localhost)的redis服务
//			Jedis jedis = new Jedis("redis-master.default", 6379);
////			Jedis jedis = new Jedis("10.0.0.9", 6379);
////			Jedis jedis = new Jedis("localhost");
////			jedis.auth("Akiraka.net");
//			//覆盖重写
//			jedis.set(workflowName, workflowFile);
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
//				//连接Redis失败3次,显示连接失败和异常信息
////				System.out.println("连接redis数据库失败");
////				System.out.println(e);
//				Main.log.severe("连接redis数据库失败" + e);
//			}
//		}
//	}

	
/*	//读List
	public static List<String> read(String workflowName, int recursionTimes) {
		
		List<String> states = null;
		try{
			//连接本地的redis服务
			Jedis jedis = new Jedis("redis-master.default", 6379);
//			Jedis jedis = new Jedis("10.0.0.9", 6379);
//			Jedis jedis = new Jedis("localhost");
//			jedis.auth("Akiraka.net");

			states = jedis.lrange(workflowName, 0, -1);

			//使用完关闭连接
			if(jedis != null){
				jedis.close();
			}
			
			return states;
		}
		catch(Exception e){
			//recursionTimes递归计数
			if (recursionTimes < 4){
				recursionTimes++;
				//延时1s
				try
				{
					Thread.currentThread().sleep(1000);//毫秒
				}
				catch(Exception ex){}
				//递归方法read
				read(workflowName, recursionTimes);
			}else{
				//连接Redis失败3次,显示连接失败和异常信息
//				System.out.println("连接redis数据库失败");
//				System.out.println(e);
				Main.log.severe("连接redis数据库失败" + e);
			}
		}
		return states;
	}*/

}