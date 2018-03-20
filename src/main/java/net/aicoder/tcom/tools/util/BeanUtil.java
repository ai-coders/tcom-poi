package net.aicoder.tcom.tools.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Stone 20050223
 *
 */

public class BeanUtil {
	private static final Log log = LogFactory.getLog(BeanUtil.class);

    /**
     * 检测属性是否存在
     * @param obj Object
     * @param propertyName String
     * @return boolean
     */
    public static boolean hasProperty(Object obj, String propertyName) {
        if (obj == null) {
            return false;
        }
        if (propertyName == null || propertyName.equals("")) {
            return false;
        }

        try {
            //Class objClass = obj.getClass();
            String propertyNameA = null;
            if (propertyName.length() > 1) {
                propertyNameA = propertyName.substring(0, 1).toUpperCase() +
                                propertyName.substring(1);
            } else {
                propertyNameA = propertyName.toUpperCase();
            }

            String methodName = null;
            Method m = null;

            try {
                methodName = "get" + propertyNameA;
                m = obj.getClass().getMethod(methodName);
            } catch (Exception ex) {
            }

            if (m == null) {
                try {
                    methodName = "is" + propertyNameA;
                    m = obj.getClass().getMethod(methodName);
                } catch (Exception ex) {
                }
            }

            if (m == null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 依据传入对象所包含的属性名称创建对应的新对象
     * @param beanObj
     * @param propertyName
     * @return
     * @throws Exception
     */
    public static Object newObjectByProperty(Object beanObj, String propertyName) throws Exception {
		if (beanObj == null) {
			// throw new IllegalArgumentException("Input Object is Null!");
			return null;
		}    	
		if (AiStringUtil.isEmpty(propertyName)) {
			throw new IllegalArgumentException("Input propertyName is Null!");
		}
		String propertyNameA = null;
		if (propertyName.length() > 1) {
			propertyNameA = propertyName.substring(0, 1).toUpperCase()
					+ propertyName.substring(1);
		} else {
			propertyNameA = propertyName.toUpperCase();
		}

		Exception getMethodException = null;
		Class<? extends Object> beanObjClass = beanObj.getClass();

		String methodName = null;
		Method method = null;

		try {
			methodName = "get" + propertyNameA;
			method = beanObjClass.getMethod(methodName);
		} catch (NoSuchMethodException | SecurityException e) {
			log.error(e.toString());
			getMethodException = e;
		}

		if (method == null) {
			try {
				methodName = "is" + propertyNameA;
				method = beanObjClass.getMethod(methodName);
			} catch (NoSuchMethodException | SecurityException e) {
				log.error(e.toString());
				getMethodException = e;
			}
		}
		if (getMethodException != null){
			throw getMethodException;
		}
		
		Type returnType = method.getGenericReturnType();// 返回类型
		Object value = getClass(returnType).newInstance();
		return value;
	}


    /**
     * 获取Bean(obj)中对应propertyName的属性值
     * @param beanObj Object
     * @param propertyName String
     * @throws Exception
     * @return Object
     */
	public static Object getPropertyValue(Object beanObj, String propertyName) throws Exception {
		if (beanObj == null) {
			// throw new IllegalArgumentException("Input Object is Null!");
			return null;
		}
		if (AiStringUtil.isEmpty(propertyName)) {
			throw new IllegalArgumentException("Input propertyName is Null!");
		}

		String propertyNameA = null;
		if (propertyName.length() > 1) {
			propertyNameA = propertyName.substring(0, 1).toUpperCase()
					+ propertyName.substring(1);
		} else {
			propertyNameA = propertyName.toUpperCase();
		}

		Exception getMethodException = null;
		Class<? extends Object> beanObjClass = beanObj.getClass();

		String methodName = null;
		Method method = null;

		try {
			methodName = "get" + propertyNameA;
			method = beanObjClass.getMethod(methodName);
		} catch (NoSuchMethodException | SecurityException e) {
			log.error(e.toString());
			getMethodException = e;
		}

		if (method == null) {
			try {
				methodName = "is" + propertyNameA;
				method = beanObjClass.getMethod(methodName);
			} catch (NoSuchMethodException | SecurityException e) {
				log.error(e.toString());
				getMethodException = e;
			}
		}
		if (getMethodException != null){
			throw getMethodException;
		}
		
		try {
			Object value = method.invoke(beanObj);
			return value;
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw e;
			//return null;
		}
	}

    /**
     * 设置Bean(obj)中对应propertyName的属性值(propertyValue)，
     * 要求对于Bean中每个属性的set方法只能定义一个,且程序会自动将传入的Value转换成Bean中对应set方法的数据类型
     * @param beanObj Object
     * @param propertyName String
     * @param propertyValue Object
     * @throws Exception
     */
	public static void setPropertyValue(Object beanObj, String propertyName,
			Object propertyValue) throws Exception {
		if (beanObj == null) {
			// throw new IllegalArgumentException("Input Object is Null!");
			return;
		}
		if (AiStringUtil.isEmpty(propertyName)) {
			throw new IllegalArgumentException("Input propertyName is Null!");
		}

		String propertyNameA = null;
		if (propertyName.length() > 1) {
			propertyNameA = propertyName.substring(0, 1).toUpperCase()
					+ propertyName.substring(1);
		} else {
			propertyNameA = propertyName.toUpperCase();
		}

		Class<? extends Object> objClass = beanObj.getClass();
		String methodName = "set" + propertyNameA;

		try {
			Method[] ma = objClass.getMethods();

			for (int i = 0; i < ma.length; i++) {
				Method method = ma[i];
				if (methodName.equals(method.getName()) == true) {
					if (method.getParameterTypes().length == 1) {

						@SuppressWarnings("rawtypes")
						Class pType = method.getParameterTypes()[0];
						Object[] arg = new Object[1];
						arg[0] = ConvDataType.toOtherType(propertyValue, pType);
						method.invoke(beanObj, arg);
					}
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 从来源对象(orig)复制数据到目标对象(dest)
	 * @param dest
	 * @param orig
	 * @throws Exception
	 */
    public static void copyProperties(Object dest, Object orig) throws
        Exception {
        copyProperties(dest, orig, null);
    }

    /**
     * 从来源对象(orig)复制数据到目标对象(dest)，不包含来源的属性列表(ignorProperties)
     * @param dest
     * @param orig
     * @param ignorProperties
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	public static void copyProperties(Object dest, Object orig,
                                      String[] ignorProperties) throws
        Exception {
        if (dest == null || orig == null) {
            return;
        }

        if (orig instanceof Map && !(dest instanceof Map)) {
            copyMapToBean(dest, (Map) orig, ignorProperties);
        } else if (!(orig instanceof Map) && dest instanceof Map) {
            copyBeanToMap((Map) dest, orig, ignorProperties);
        } else if (orig instanceof Map && dest instanceof Map) {
            copyMapToMap((Map) dest, (Map) orig, ignorProperties);
        } else if (orig instanceof java.sql.ResultSet) {
            copyResultsetToBean(dest, (java.sql.ResultSet) orig,
                                ignorProperties);
        } else {
            copyBeanToBean(dest, orig, ignorProperties);
        }
    }

    @SuppressWarnings("rawtypes")
	public static void copyMapToBean(Object dest, Map orig) throws Exception {
        copyMapToBean(dest, orig, null);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void copyMapToBean(Object dest, Map orig,
                                     String[] ignorProperties) throws Exception {
        HashSet propSet = new HashSet();
        if (ignorProperties != null) {
            for (int i = 0; i < ignorProperties.length; i++) {
                propSet.add(ignorProperties[i]);
            }
        }
        for (Iterator it = orig.keySet().iterator(); it.hasNext(); ) {
            String fieldName = (String) it.next();
            if (!propSet.contains(fieldName)) {
                Object fieldValue = orig.get(fieldName);
                setPropertyValue(dest, fieldName, fieldValue);
            }
        }
    }

    @SuppressWarnings("rawtypes")
	public static void copyBeanToMap(Map dest, Object orig) throws Exception {
        copyBeanToMap(dest, orig, null);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void copyBeanToMap(Map dest, Object orig,
                                     String[] ignorProperties) throws Exception {
        if (dest == null || orig == null) {
            return;
        }

        HashSet propSet = new HashSet();
        if (ignorProperties != null) {
            for (int i = 0; i < ignorProperties.length; i++) {
                propSet.add(ignorProperties[i]);
            }
        }

        String[] propNames = getPropertyNames(orig);
        if (propNames != null) {
            for (int i = 0; i < propNames.length; i++) {
                String fldName = propNames[i];
                if (!propSet.contains(fldName)) {
                    try {
                        Object fldValue = getPropertyValue(orig, fldName);
                        dest.put(fldName, fldValue);
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
	public static void copyMapToMap(Map dest, Map orig) throws Exception {
        copyMapToMap(dest, orig, null);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void copyMapToMap(Map dest, Map orig,
                                    String[] ignorProperties) throws Exception {
        HashSet propSet = new HashSet();
        if (ignorProperties != null) {
            for (int i = 0; i < ignorProperties.length; i++) {
                propSet.add(ignorProperties[i]);
            }

            if (orig != null && dest != null) {
                for (Iterator it = orig.keySet().iterator(); it.hasNext(); ) {
                    Object key = it.next();
                    Object value = orig.get(key);
                    if (!propSet.contains(key) &&
                        !propSet.contains(key.toString())) {
                        dest.put(key, value);
                    }
                }
            }
        } else {
            dest.putAll(orig);
        }
    }


    /**
     * 按目标Bean的属性列表，将来源Bean中同名属性的对应属性值复制到目标Bean
     * @param dest Object（目标Bean）
     * @param orig Object（来源Bean）
     */
    public static void copyBeanToBean(Object dest, Object orig)
//            throws Exception
    {
        copyBeanToBean(dest, orig, null);
    }

    /**
     * 按目标Bean的属性列表，将来源Bean中同名属性的对应属性值复制到目标Bean
     * @param dest Object（目标Bean）
     * @param orig Object（来源Bean）
     * @param ignorProperties 被忽略的属性
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void copyBeanToBean(Object dest, Object orig,
                                      String[] ignorProperties) {
        if (dest == null || orig == null) {
            return;
        }
        HashSet propSet = new HashSet();
        if (ignorProperties != null) {
            for (int i = 0; i < ignorProperties.length; i++) {
                propSet.add(ignorProperties[i]);
            }
        }
        String sNoCopy = "";
        String[] allProperties = getPropertyNames(orig);
        if (allProperties != null) {
            for (int i = 0; i < allProperties.length; i++) {
                if (!propSet.contains(allProperties[i])) {
                    try {
                        Object value = getPropertyValue(orig, allProperties[i]);
                        setPropertyValue(dest, allProperties[i], value);
                    } catch (Exception ex1) {
                        if (sNoCopy.equals("")) {
                            sNoCopy = allProperties[i];
                        } else {
                            sNoCopy = sNoCopy + "," + allProperties[i];
                        }

                    }
                }
            }
        }
    }

    /**
     * 从orig中复制dest所需要的数据
     * @param dest Object
     * @param orig Object
     */
    public static void copyBeanToBeanByDestProperties(Object dest, Object orig) {
        String[] propertyNames = getPropertyNames(dest);
        copyBeanToBeanByProperties(dest, orig, propertyNames);
    }

    /**
     * 按目标copyProperties的属性列表，将来源Bean中同名属性的对应属性值复制到目标Bean
     * @param dest Object
     * @param orig Object
     * @param copyProperties String[]
     */
    public static void copyBeanToBeanByProperties(Object dest, Object orig,
                                      String[] copyProperties) {
        for (int i = 0; i < copyProperties.length; i++) {
            try {
                Object value = getPropertyValue(orig, copyProperties[i]);
                setPropertyValue(dest, copyProperties[i], value);
            } catch (Exception ex1) {
            }
        }
    }

    public static void copyResultsetToBean(Object dest, java.sql.ResultSet orig) {
        copyResultsetToBean(dest, orig, null);
    }

    private static String fieldName2PropertyName(String[] properties,
                                                 String fieldName) {
        String propertyName = null;
        String copyFieldName = fieldName;
        if (fieldName.indexOf("_") >= 0) {
            copyFieldName = fieldName.replaceAll("_", "");
        }

        if (properties != null) {
            for (int i = 0; i < properties.length; i++) {
                if (properties[i].equalsIgnoreCase(copyFieldName)) {
                    propertyName = properties[i];
                    break;
                }
            }
        }
        return propertyName;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void copyResultsetToBean(Object dest, java.sql.ResultSet orig,
                                           String[] ignorProperties) {
        if (dest == null || orig == null) {
            return;
        }
        HashSet propSet = new HashSet();
        if (ignorProperties != null) {
            for (int i = 0; i < ignorProperties.length; i++) {
                propSet.add(ignorProperties[i]);
            }
        }

        try {
            String[] allObjProperties = getPropertyNames(dest);

            java.sql.ResultSetMetaData meta = orig.getMetaData();
            for (int i = 0; i < meta.getColumnCount(); i++) {
                String columnName = meta.getColumnName(i + 1);
                String propertyName = fieldName2PropertyName(allObjProperties,
                    columnName);
                if (propertyName != null &&
                    !propSet.contains(propertyName) &&
                    !propSet.contains(columnName)) {
                    try {
                        Object value = orig.getObject(i + 1);
                        setPropertyValue(dest, propertyName, value);
                    } catch (Exception e) {

                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    public static java.util.Date getSqlDate(java.sql.ResultSet rs,
                                            String fieldName) {
        if (rs == null) {
            return null;
        }
        try {
            java.util.Date date = (java.util.Date) rs.getObject(fieldName);
            return date;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 依据目标Bean　Class类型生成输出的新的List，即会依据输入的inList，
     * 将每行的记录复制到按目标Bean类型所生成的Bean记录中，并加入新的List返回给使用者
     * @param inList List
     * @param destClass Class
     * @throws Exception
     * @return List
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static List list2List(List inList, Class destClass) throws Exception {
        if (inList == null) {
            return null;
        }
        int iSize = inList.size();
        List outList = new ArrayList(iSize);

        for (int i = 0; i < iSize; i++) {
            Object obj = null;
            try {
                obj = destClass.newInstance();
            } catch (Exception e) {
                throw e;
            }
            try {
                copyProperties(obj, inList.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            outList.add(obj);
        }
        return outList;
    }

    /**
     * 依据目标Bean　Class类型生成输出的新的List，即会依据输入的inArray，
     * 将每行的记录(PropertyMap中的属性)复制到目标Bean，并加入新的List返回给使用者
     * @param inArray Object[]
     * @param destClass Class
     * @param PropertyMap Map <orig propertyName, dest propertyName>
     * @return List
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static List array2List(Object[] inArray,
            Class destClass, Map<String, String> PropertyMap) throws Exception {
        int size = inArray.length;
        List destList = new ArrayList(size);
        for(int i = 0; i < size; i++) {
            Object dest = destClass.newInstance();
            copyBeanToBeanWithPropertyMap(dest, inArray[i], PropertyMap);
            destList.add(dest);
        }
        return destList;
    }

    /**
     * 将源Bean(PropertyMap中的属性)复制到目标Bean
     * @param dest Object
     * @param orig Object
     * @param PropertyMap Map
     * @throws Exception
     */
    public static void copyBeanToBeanWithPropertyMap(Object dest,Object orig,
                             Map<String,String> PropertyMap) throws Exception {
        Iterator<String> keys = PropertyMap.keySet().iterator();
        while (keys.hasNext()) {
            String origProperty = keys.next();
            String destProperty = PropertyMap.get(origProperty);
            Object value = getPropertyValue(orig, origProperty);
            setPropertyValue(dest, destProperty, value);
        }
    }

    /**
     * 依据目标Bean　Class类型生成输出的新的List，即会依据输入的inArray，
     * 将每行的记录复制到按目标Bean属性需要copy Properties，并加入新的List返回给使用者
     * @param inArray Object[]
     * @param destClass Class
     * @return List
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	public static List array2ListBaseDestProperties(Object[] inArray,
            Class destClass) throws Exception {
        if(inArray == null) {
            return null;
        }
        int iSize = inArray.length;
        List outList = new ArrayList(iSize);

        for(int i = 0; i < iSize; i++) {
            Object obj = null;
            try {
                obj = destClass.newInstance();
            } catch (Exception e) {
                throw e;
            }
            copyBeanToBeanByDestProperties(obj, inArray[i]);
        }
        return outList;
    }

    public static String dumpBean(Object bean) {
        String rtn = "";
        if (bean == null) {
            return "null";
        }
        try {
/**
            rtn = bean.getClass().getName() + " : \t";
            if (bean instanceof IDto) {
                rtn += "op:" + ((IDto) bean).getOp();
            }
            rtn += "\r\n";
**/
            
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
//                if( f.getDeclaringClass().equals( bean.getClass() ) == true ){
                String fn = f.getName();
                rtn += "\t\t" + fn + " :" + BeanUtil.getPropertyValue(bean, fn) +
                    "\t--\t" + f.getType() + "\r\n";
//                }
            }

        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtn;
    }

    @SuppressWarnings("rawtypes")
	public static Class getPropertyType(Object bean, String propertyName) {
        Class returnType = null;
        try {
            String fldName = propertyName.substring(0, 1).toUpperCase() +
                             propertyName.substring(1);
            String methodName1 = "get" + fldName;
            String methodName2 = "is" + fldName;
            Class cls = bean.getClass();
            Method[] methods = cls.getMethods();
            if (methods != null) {
                for (int i = 0; i < methods.length; i++) {
                    try {
                        Method method = methods[i];
                        String getMethodName = method.getName();
                        if ((getMethodName.equals(methodName1) ||
                             getMethodName.equals(methodName2))
                            &&
                            (method.getParameterTypes() == null ||
                             method.getParameterTypes().length == 0)) {
                            returnType = method.getReturnType();
                            break;
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        } catch (Exception e) {
        }
        return returnType;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] getPropertyNames(Object bean) {
        ArrayList propertyNameList = new ArrayList();
        String[] propertyNames = null;
        try {
            Class cls = bean.getClass();
            Method[] methods = cls.getMethods();
            if (methods != null) {
                for (int i = 0; i < methods.length; i++) {
                    String fldName = "";
                    try {
                        Method method = methods[i];
                        String getMethodName = method.getName();
                        if (getMethodName.startsWith("get")
                            &&
                            (method.getParameterTypes() == null ||
                             method.getParameterTypes().length == 0)) {
                            fldName = method.getName().substring(3);
                            fldName = fldName.substring(0, 1).toLowerCase() +
                                      fldName.substring(1);
                            propertyNameList.add(fldName);
                        } else if (getMethodName.startsWith("is")
                                   &&
                                   (method.getParameterTypes() == null ||
                                    method.getParameterTypes().length == 0)) {
                            fldName = method.getName().substring(2);
                            fldName = fldName.substring(0, 1).toLowerCase() +
                                      fldName.substring(1);
                            propertyNameList.add(fldName);
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        } catch (Exception e) {
        }
        if (propertyNameList.size() > 0) {
            propertyNames = new String[propertyNameList.size()];
            for (int i = 0; i < propertyNameList.size(); i++) {
                propertyNames[i] = (String) propertyNameList.get(i);
            }
        }
        return propertyNames;
    }


    /**
     * 解码encodeParam操作后的字符串
     * @param paramString String
     * @throws Exception
     * @return Map
     */
    @SuppressWarnings("rawtypes")
	public static Map decodeParam(String paramString) throws Exception {
        if (paramString == null) {
            return null;
        }

        String[] strList = paramString.split("Z");
        byte[] byteBuffer = new byte[strList.length];

        for (int i = 0; i < strList.length; i++) {
            byteBuffer[i] = Byte.parseByte(strList[i]);
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(byteBuffer);
        GZIPInputStream zis = new GZIPInputStream(bis);
        Map result = null;
        ObjectInputStream ois = new ObjectInputStream(zis);
        result = (Map) ois.readObject();
        ois.close();

        zis.close();
        bis.close();

        return result;
    }

    /**
     * 将Map类型的参数编码成字符串
     * @param params Map
     * @throws Exception
     * @return String
     */
    @SuppressWarnings("rawtypes")
	public static String encodeParam(Map params) throws Exception {
        StringBuffer buf = new StringBuffer("");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(bos);
        ObjectOutputStream oos = new ObjectOutputStream(zos);
        oos.writeObject(params);
        oos.flush();
        zos.finish();
        bos.flush();

        byte[] data = bos.toByteArray();

        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                String ch = Byte.toString(data[i]);
                buf.append(ch + "Z");
            }
        }

        oos.close();
        zos.close();
        bos.close();

        return buf.toString();
    }

    /**
     * 深度克隆对象，
     * @param obj
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object deepClone(Object obj) {
        if (obj == null) {
            log.debug("Warn: object is null, so return null deepClone object");
            return null;
        }
        try {
            Map map = new HashMap();
            map.put("obj", obj);
            String str = encodeParam(map);
            Map newMap = decodeParam(str);
            return newMap.get("obj");
        } catch (Exception e) {
            log.error("This obj must implement java.io.Serializable : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void setPreparedParam(PreparedStatement stmt,
                                                 int parameterIndex,
                                                 int SqlType,
                                                 Object value) throws
        SQLException {
        if(value!=null) {
            stmt.setObject(parameterIndex,value);
        } else {
            stmt.setNull(parameterIndex,SqlType,null);
        }
    }
    
    /**
     * 依据属性名获取属性值
     * @param propObject
     * @param propName
     * @return
     * @throws Exception
     */
	public static Object getFieldValue(Object propObject, String propName) throws Exception {
		Object valueObject = null;

		// 获取属性值
		Field field;
		try {
			field = propObject.getClass().getField(propName);
			try {
				valueObject = field.get(propObject);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error(e.toString());
				// e.printStackTrace();
				throw e;
			}
		} catch (NoSuchFieldException | SecurityException e) {
			log.error(e.toString());
			// e.printStackTrace();
			throw e;
		}
		return valueObject;
	}

	/**
	 * 依据属性名称设置对象中相应的值
	 * @param propObject
	 * @param propName
	 * @param valueObject
	 * @throws Exception
	 */
	public static void setFieldValue(Object propObject, String propName,
			Object valueObject) throws Exception {
		// Object valueObject = null;

		// 获取属性值
		Field field;
		
		try {
			field = propObject.getClass().getDeclaredField(propName);
			field.setAccessible(true);
			try {
				field.set(propObject, valueObject);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error(e.toString());
				throw e;
			}
		} catch (NoSuchFieldException | SecurityException e) {
			log.error(e.toString());
			throw e;
		}
	}
	
	/**
	 * 这个方法不行，因为运行时泛型无法攻取
	 * @param list
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getParameterizedTypeNameOfList(Object list){
		String className = null;
		Class clazz = getParameterizedTypeOfList(list);
		if(clazz != null){
			className = clazz.getName();
		}
		return className;
	}

	/**
	 * 这个方法不行，因为运行时泛型无法攻取
	 * @param list
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class getParameterizedTypeOfList(Object list){
		if(list == null){
			return null;
		}else{
			ParameterizedType parameterizedType = (ParameterizedType) list.getClass().getGenericSuperclass();

			int index = 0; //第n个泛型    Map<K,V> 就有2个  拿K  就是0  V就是1
//			Class genericClass = getGenericClass(parameterizedType,index);
			Class genericClass = (Class)parameterizedType.getActualTypeArguments()[index];
			return genericClass;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static Class getClass(Type type) {
		return getClass(type,0);
	}
	
    @SuppressWarnings("rawtypes")
	private static Class getClass(Type type, int i) {     
        if (type instanceof ParameterizedType) { // 处理泛型类型     
            return getGenericClass((ParameterizedType) type, i);     
        } else if (type instanceof TypeVariable) {     
            return (Class) getClass(((TypeVariable) type).getBounds()[0], 0); // 处理泛型擦拭对象     
        } else {// class本身也是type，强制转型     
            return (Class) type;     
        }     
    }       
    
    @SuppressWarnings("rawtypes")
	private static Class getGenericClass(ParameterizedType parameterizedType, int i) {
    	//第n个泛型    Map<K,V> 就有2个  拿K  就是0  V就是1
        Object genericClass = parameterizedType.getActualTypeArguments()[i];     
        if (genericClass instanceof ParameterizedType) { // 处理多级泛型     
            return (Class) ((ParameterizedType) genericClass).getRawType();     
        } else if (genericClass instanceof GenericArrayType) { // 处理数组泛型     
            return (Class) ((GenericArrayType) genericClass).getGenericComponentType();     
        } else if (genericClass instanceof TypeVariable) { // 处理泛型擦拭对象     
            return (Class) getClass(((TypeVariable) genericClass).getBounds()[0], 0);     
        } else {     
            return (Class) genericClass;     
        }     
    }  
}
