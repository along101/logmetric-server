package com.along101.logmetric.common.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLUtil {

	public static <T> T loadFromResource(Class<T> clazz, String name) throws JAXBException, IOException {
		InputStream stream = null;
		try{
			stream = ReflectUtil.getDefaultClassLoader().getResourceAsStream(name);
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return (T) jaxbUnmarshaller.unmarshal(stream);
		}finally{
			if(stream != null){
				stream.close();
			}
		}

	}

}
