package net.aicoder.tcom.tools.util;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;  
import java.nio.file.Path;  
import java.nio.file.StandardOpenOption;  
import java.util.ArrayList;  
import java.util.List;  
  
import com.alibaba.fastjson.JSON;  

public class JsonUtil {
	private static final String DEFAULT_CHARSET_NAME = "UTF-8";

	public static <T> String serialize(T object) {
		return JSON.toJSONString(object);
	}

	public static <T> T deserialize(String string, Class<T> clz) {
		return JSON.parseObject(string, clz);
	}

	public static <T> T load(Path path, Class<T> clz) throws IOException {
		return deserialize(new String(Files.readAllBytes(path), DEFAULT_CHARSET_NAME), clz);
	}

	public static <T> void save(Path path, T object) throws IOException {
		if (Files.notExists(path.getParent())) {
			Files.createDirectories(path.getParent());
		}
		Files.write(path, serialize(object).getBytes(DEFAULT_CHARSET_NAME), StandardOpenOption.WRITE,
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	public static void main(String[] args) {
		Dept dept1 = new Dept();
		dept1.setDeptId(100);
		dept1.setDeptName("Dept100");
		Person person1 = new Person();
		person1.setAddress("address");
		person1.setAge(11);
		person1.setName("amao");
		person1.setDept(dept1);

		Dept dept2 = new Dept();
		dept2.setDeptId(200);
		dept2.setDeptName("Dept200");
		Person person2 = new Person();
		person2.setAddress("address");
		person2.setAge(11);
		person2.setName("amao");
		person2.setDept(dept2);

		List<Person> lp1 = new ArrayList<Person>();
		lp1.add(person1);
		lp1.add(person2);
		System.out.println(serialize(lp1));
		
		String person21Str = serialize(person1);
		Person person21 = deserialize(person21Str, Person.class);
		System.out.println(serialize(person21));
		
		String person2Str = serialize(lp1);
		@SuppressWarnings("unchecked")
		List<Person> lp2 = (List<Person>)deserialize(person2Str, ArrayList.class);
		System.out.println(serialize(lp2));
	}
}

class Dept implements Serializable{  
	private static final long serialVersionUID = 1L;
	
	private int deptId;  
	private String deptName; 
	
	public int getDeptId() {
		return deptId;
	}
	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
}

class Person implements Serializable{  
    private static final long serialVersionUID = 1L;
    
    private Dept dept;
    private String name;  
    private int age;  
    private String address;
    
    public Dept getDept() {
		return dept;
	}
	public void setDept(Dept dept) {
		this.dept = dept;
	}
	public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
    public int getAge() {  
        return age;  
    }  
    public void setAge(int age) {  
        this.age = age;  
    }  
    public String getAddress() {
        return address;  
    }  
    public void setAddress(String address) {  
        this.address = address;  
    }  
}  