package net.aicoder.tcom.tools.util;
import java.io.File;
import java.io.StringReader;  
import java.io.StringWriter;  

import javax.xml.bind.JAXBContext;  
import javax.xml.bind.Marshaller;  
import javax.xml.bind.Unmarshaller;  
  
/** 
 * Jaxb2工具类 
 * @author      StoneShi 
 * @create      2013-3-29 下午2:40:14 
 */  
public class JaxbUtil {  
    /** 
     * JavaBean转换成xml 
     * 默认编码UTF-8 
     * @param obj 
     * @param writer 
     * @return  
     */  
    public static String convertToXml(Object obj) {  
        return convertToXml(obj, "UTF-8");  
    }  
  
    /** 
     * JavaBean转换成xml 
     * @param obj 
     * @param encoding  
     * @return  
     */  
    public static String convertToXml(Object obj, String encoding) {  
        String result = null;  
        try {  
            JAXBContext context = JAXBContext.newInstance(obj.getClass());  
            Marshaller marshaller = context.createMarshaller();  
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);  
  
            StringWriter writer = new StringWriter();  
            marshaller.marshal(obj, writer);  
            result = writer.toString();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return result;  
    }  
  
    /** 
     * xml转换成JavaBean 
     * @param c 
     * @param xml 
     * @return 
     */  
    @SuppressWarnings("unchecked")  
    public static <T> T convertToJavaBean(Class<T> c, String xml) {  
        T t = null;  
        try {  
            JAXBContext context = JAXBContext.newInstance(c);  
            Unmarshaller unmarshaller = context.createUnmarshaller();  
            t = (T) unmarshaller.unmarshal(new StringReader(xml));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return t;  
    }
    
    /** 
     * xml转换成JavaBean 
     * @param c 
     * @param file 
     * @return 
     */  
    @SuppressWarnings("unchecked")  
    public static <T> T convertToJavaBean(Class<T> c, File file) {  
        T t = null;  
        try {  
            JAXBContext context = JAXBContext.newInstance(c);  
            Unmarshaller unmarshaller = context.createUnmarshaller();  
            t = (T) unmarshaller.unmarshal(file);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
  
        return t;  
    }  
}  