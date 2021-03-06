package com.somnus.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class FastJsonTestCase {
	/**
     * 简单序列化
     * 	QuoteFieldNames———-输出key时是否使用双引号,默认为true 
     * 	WriteMapNullValue——–是否输出值为null的字段,默认为false 
     * 	WriteNullNumberAsZero—-数值字段如果为null,输出为0,而非null 
     * 	WriteNullListAsEmpty—–List字段如果为null,输出为[],而非null 
     * 	WriteNullStringAsEmpty—字符类型字段如果为null,输出为"",而非null 
     * 	WriteNullBooleanAsFalse–Boolean字段如果为null,输出为false,而非null
     */
	@Test
    public void simpleTest() {
        User user = new User("admin",null,20,new BigDecimal("20.2"));
        user.setBirthday(new Date());
        user.setPlots(Arrays.asList(new Plot("diudiu"), new Plot("dudu")));
        
        String jsonStr = JSON.toJSONString(user/*,SerializerFeature.PrettyFormat*/);
        System.out.println(jsonStr);
    }
	
	/**
     * 复杂序列化
     */
	@Test
    public void complexTest() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", "owen");
        map.put("age", 25);
        map.put("sex", "男");

        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("name", "jack");
        temp.put("age", 18);
        
        map.put("girinfo", temp);

        List<String> list = new ArrayList<String>();
        list.add("爬山");
        list.add("电影");
        list.add("旅游");
        
        map.put("hobby", list);

        String jsonStr = JSON.toJSONString(map);
        System.out.println(jsonStr);
    }
	
	/**
     * 简单反序列化
     */
	@Test
    public void simpleDeserializeTest() {
        String jsonStr = "{\"age\":20,\"birthday\":\"2017-01-23 17:19:04\",\"blance\":20.20,\"flag\":false,\"plots\":[{\"name\":\"diudiu\"},{\"name\":\"dudu\"}],\"address\":{\"province\":\"jiangxi\",\"city\":\"jiujiang\"}}";
        User user = JSON.parseObject(jsonStr, User.class);
        System.out.println(user);
        
        JSONObject jo = JSON.parseObject(jsonStr);
        System.out.println(jo.getString("age"));
        System.out.println(jo.getString("birthday"));
        System.out.println(jo.getBigDecimal("blance"));
        System.out.println(jo.getBooleanValue("blance"));
        JSONArray array = jo.getJSONArray("plots");
        for (Object no : array) {
            JSONObject job = (JSONObject) no;
            System.out.println(job.getString("name"));
        }
        JSONObject child = jo.getJSONObject("address");
        System.out.println(child.getString("province"));
        System.out.println(child.getString("city"));
    }
	
	/**
	 * 数组反序列化
	 */
	@Test
    public void arrayDeserializeTest() {
        String jsonStr = "[{\"username\":\"owen\",\"password\":\"passw0rd\", \"age\":24},{\"username\":\"owen\",\"password\":\"passw0rd\", \"age\":24}]";
        List<User> list = JSON.parseArray(jsonStr, User.class);
        System.out.println(list);
        
        JSONArray array = JSON.parseArray(jsonStr);
        for (Object no : array) {
            JSONObject job = (JSONObject) no;
            System.out.println(job.get("username").toString());
        }
    }
	
	/**
     * 泛型反序列化
     */
	@Test
    public void genericTypeDeserializeTest() {
        String jsonStr = "{\"user\":{\"username\":\"owen\",\"password\":\"passw0rd\", \"age\":24}}";
        Map<String, User> map = JSON.parseObject(jsonStr, new TypeReference<Map<String, User>>(){});
        System.out.println(map.get("user")+"\n");

        String jsonStr2 = "{\"user\":[{\"username\":\"owen\", \"age\":24}, {\"username\":\"jack\", \"age\":18}]}";
        Map<String, List<User>> users = JSON.parseObject(jsonStr2, new TypeReference<Map<String, List<User>>>(){});
        for (User user : users.get("user")) {
        	System.out.println(user);
            System.out.println("------------------");
        }
        
        System.out.println("\n");
        
        String jsonStr3 = "[{\"username\":\"owen\", \"age\":24}, {\"username\":\"jack\", \"age\":18}]";
        List<User> list = JSON.parseObject(jsonStr3, new TypeReference<List<User>>(){});
        for (User user : list) {
        	System.out.println(user);
            System.out.println("------------------");
        }
    }
	
	public static class User {
		@JSONField(serialize = false)
		private String username;
		
		private String password;
		
		private int age;
		
		@JSONField(serializeUsing = BigDecimalSerializer.class)
		private BigDecimal blance;
		
		@JSONField(serialzeFeatures ={SerializerFeature.WriteNullBooleanAsFalse})
		private boolean flag;
		
		private List<Plot> plots;
		
		@JSONField (format="yyyy-MM-dd HH:mm:ss") 
		private Date birthday;
		
		private Map<String,List<String>> map;
		
		public User() {
			super();
		}
		
		public User(String username, String password, int age, BigDecimal blance) {
			super();
			this.username = username;
			this.password = password;
			this.age = age;
			this.blance = blance;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public List<Plot> getPlots() {
			return plots;
		}
		public void setPlots(List<Plot> plots) {
			this.plots = plots;
		}
		public Map<String, List<String>> getMap() {
			return map;
		}
		public void setMap(Map<String, List<String>> map) {
			this.map = map;
		}
		public BigDecimal getBlance() {
			return blance;
		}
		public void setBlance(BigDecimal blance) {
			this.blance = blance;
		}
		public Date getBirthday() {
			return birthday;
		}
		public void setBirthday(Date birthday) {
			this.birthday = birthday;
		}
		public boolean isFlag() {
			return flag;
		}
		public void setFlag(boolean flag) {
			this.flag = flag;
		}

		public String toString() {  
	    	return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);   
	    } 
	}
	
	public static class Plot{
		
		public Plot() {
			super();
		}
		public Plot(String name){
			this.name = name;
		}
		private String name;

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	public static class BigDecimalSerializer implements ObjectSerializer{

		@Override
		public void write(JSONSerializer serializer, Object object,
				Object fieldName, Type fieldType, int features) throws IOException {
			SerializeWriter out = serializer.getWriter();

	        if (object == null) {
	            if (out.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
	                out.write('0');
	            } else {
	                out.writeNull();
	            }
	            return;
	        }

	        BigDecimal val = (BigDecimal) object;
	        DecimalFormat fmt = new DecimalFormat("0.00");
	        out.write(fmt.format(val));

	        if (out.isEnabled(SerializerFeature.WriteClassName) && fieldType != BigDecimal.class && val.scale() == 0) {
	            out.write('.');
	        }
			
		}

	}
}

