package bit.workflowScheduler.util;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bit.workflowScheduler.main.Main;

public class RedisTest {
    private static JedisCluster jedis = null;

    //可用连接实例的最大数目，默认为8；
    //如果赋值为-1，则表示不限制，如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)
    private static Integer MAX_TOTAL = -1;
    //控制一个pool最多有多少个状态为idle(空闲)的jedis实例，默认值是8
    private static Integer MAX_IDLE = 200;
    //等待可用连接的最大时间，单位是毫秒，默认值为-1，表示永不超时。
    //如果超过等待时间，则直接抛出JedisConnectionException
    private static Integer MAX_WAIT_MILLIS = 10000;
    //在borrow(用)一个jedis实例时，是否提前进行validate(验证)操作；
    //如果为true，则得到的jedis实例均是可用的
    private static Boolean TEST_ON_BORROW = true;
    //在空闲时检查有效性, 默认false
    private static Boolean TEST_WHILE_IDLE = true;
    //是否进行有效性检查
    private static Boolean TEST_ON_RETURN = true;

    //访问密码
    private static String AUTH = "1234@abcd";

    /**
     * 静态块，初始化Redis连接池
     */
    static {
    	Main.log.finest("正在连接redis数据库");
        try {
            JedisPoolConfig config = new JedisPoolConfig();
        /*注意：
            在高版本的jedis jar包，比如本版本2.9.0，JedisPoolConfig没有setMaxActive和setMaxWait属性了
            这是因为高版本中官方废弃了此方法，用以下两个属性替换。
            maxActive  ==>  maxTotal
            maxWait==>  maxWaitMillis
         */
           
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT_MILLIS);
            config.setTestOnBorrow(TEST_ON_BORROW);
            config.setTestWhileIdle(TEST_WHILE_IDLE);
            config.setTestOnReturn(TEST_ON_RETURN);

            Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
//            jedisClusterNode.add(new HostAndPort("192.168.1.25", 8011));
//            jedisClusterNode.add(new HostAndPort("192.168.1.26", 7012));
//            jedisClusterNode.add(new HostAndPort("192.168.1.26", 8012));
//            jedisClusterNode.add(new HostAndPort("10.8.1.15", 7011));
//            jedisClusterNode.add(new HostAndPort("10.8.1.16", 7012));
//            jedisClusterNode.add(new HostAndPort("10.8.1.17", 7013));
            jedisClusterNode.add(new HostAndPort(Main.Redis0Ip, Main.Redis0Port));
            jedisClusterNode.add(new HostAndPort(Main.Redis1Ip, Main.Redis1Port));
            jedisClusterNode.add(new HostAndPort(Main.Redis2Ip, Main.Redis2Port));

            jedis = new JedisCluster(jedisClusterNode,1000,1000,5,config);
            Main.log.finest("连接redis数据库成功");
        } catch (Exception e) {
			Main.log.warning("连接redis数据库失败, 正常退出" + e);
            e.printStackTrace();
            System.exit(0);
        }

    }


    public static JedisCluster getJedis(){
        return jedis;
    }
    
	/**
	 * 断开数据库链接
	 */
	public void close() {
		if(jedis != null){
			jedis.close();
		}
	}
	
	//写哈希表 key 中的字段 field 的值设为 value 
		public void write(String key, String field, String value, int recursionTimes) {
			Main.log.finest("向redis写入(key, field, value)形式的数据：key=" + key +", field="+field + ", String="+ value);
			try{
				jedis.hset(key, field, value);
				Main.log.finest("向redis写入(key, field, value)形式的数据成功：key=" + key +", field="+field + ", String="+ value);
			}
			catch(Exception e){
				//recursionTimes递归计数
				if (recursionTimes < 4){
					Main.log.finest("向redis写入(key, field, value)形式的数据失败后重试");
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
//					Main.log.severe("写入redis(key, field, value)失败" + e);
					Main.log.severe("！！！写入redis(key, field, value)失败，重试后失败！！！" + e);
				}
			}
		}
		
		//写同时将多个 field-value (域-值)对设置到哈希表 key 中
		public void write(String key, Map<String,String> map, int recursionTimes) {
			Main.log.finest("向redis写入(key, Map<field, value>)形式的数据：key=" + key);
			try{
				jedis.hmset(key, map);
				Main.log.finest("向redis写入(key, Map<field, value>)形式的数据成功：key=" + key);
			}
			catch(Exception e){
				//recursionTimes递归计数
				if (recursionTimes < 4){
					Main.log.finest("向redis写入(key, Map<field, value>)形式的数据失败后重试");
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
					Main.log.severe("！！！写入redis(key, Map<field, value>)失败，重试后失败！！！" + e);
				}
			}
		}

		
		//读哈希表 key 中的字段 field 的值设为 value
		public String read(String key, String field, int recursionTimes) {
			Main.log.finest("从redis根据(key, field)读取数据");
			String state = null;
			try{
				state = jedis.hget(key, field);
				Main.log.finest("从redis根据(key, field)读取数据成功");
				return state;
			}
			catch(Exception e){
				//recursionTimes递归计数
				if (recursionTimes < 4){
					Main.log.finest("从redis根据(key, field)读取数据失败后重试");
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
					Main.log.severe("！！！读redis(key, field)失败，重试后失败！！！" + e);
				}
			}
			return state;
		}
		
		//写String
		public void write(String workflowName, String workflowFile, int recursionTimes) {
			Main.log.finest("向redis写入(String, String)形式的数据：key=" + workflowName + ", value="+workflowFile);
			try{
				//覆盖重写
				jedis.set(workflowName, workflowFile);
				Main.log.finest("向redis写入(String, String)形式的数据成功：key=" + workflowName + ", value="+workflowFile);
			}
			catch(Exception e){
				//recursionTimes递归计数
				if (recursionTimes < 4){
					Main.log.finest("向redis写入(String, String)形式的数据失败后重试");
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
					Main.log.severe("！！！写入redis(String, String)失败，重试后失败！！！" + e);
				}
			}
		}
		
		//写byte[]
		public void write(String workflowName, byte[] workflowFile, int recursionTimes) {
			Main.log.finest("向redis写入(key, value)形式的数据：key=" + workflowName);
			try{
				//覆盖重写
				jedis.set(workflowName.getBytes(), workflowFile);
				Main.log.finest("向redis写入(key, value)形式的数据成功：key=" + workflowName);
			}
			catch(Exception e){
				//recursionTimes递归计数
				if (recursionTimes < 4){
					Main.log.finest("向redis写入(key, value)形式的数据失败后重试");
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
					Main.log.severe("！！！写入redis(key, value)失败，重试后失败！！！" + e);
				}
			}
		}
		
		//读哈希表 key 中的字段 field 的值设为 value
		public static String read(String key, int recursionTimes) {
			Main.log.finest("从redis根据(key)读取数据");
			String state = null;
			try{
				state = jedis.get(key);
				Main.log.finest("从redis根据(key)读取数据成功");
				return state;
			}
			catch(Exception e){
				//recursionTimes递归计数
				if (recursionTimes < 4){
					Main.log.finest("从redis根据(key)读取数据失败后重试");
					recursionTimes++;
					//延时1s
					try
					{
						Thread.currentThread().sleep(1000);//毫秒
					}
					catch(Exception ex){}
					//递归方法read
					read(key, recursionTimes);
				}else{
					Main.log.severe("！！！读redis(key)失败，重试后失败！！！" + e);
				}
			}
			return state;
		}
		
		public void delete(String key, int recursionTimes) {
			Main.log.finest("从redis根据(key)删除数据");
			try{
				jedis.del(key);
				Main.log.finest("从redis根据(key)删除数据成功");
			}
			catch(Exception e){
				//recursionTimes递归计数
				if (recursionTimes < 4){
					Main.log.finest("从redis根据(key)删除数据失败后重试");
					recursionTimes++;
					//延时1s
					try
					{
						Thread.currentThread().sleep(1000);//毫秒
					}
					catch(Exception ex){}
	                //递归方法write
					jedis.del(key);
				}else{
					Main.log.warning("删除redis(key)失败，重试后失败" + e);
//					System.out.println("删除redis(key)失败" + e);
				}
			}
		}
}


