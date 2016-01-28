package com.sprd.performance.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.sprd.performance.main.Performance;

/**
 * @ClassName: GetTestCount
 * @Description:TODO 从xml中读取测试次数
 * @author: shan.ji
 * @date: 2015年9月1日 下午2:24:57
 * 
 */
public class GetTestCount {
	private String count;

	public GetTestCount(String path) {
		Performance.LOG.log(Level.INFO, "GetTestCount:");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		XMLReader reader = null;
		try {
			parser = factory.newSAXParser();
			reader = parser.getXMLReader();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			Performance.LOG.log(
					Level.SEVERE,
					"GetTestCount ParserConfigurationException:"
							+ e.getStackTrace());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			Performance.LOG.log(Level.SEVERE,
					"GetTestCount SAXException:" + e.getStackTrace());
		}
		reader.setContentHandler(new MyHandle());
		File file = new File(path);
		FileInputStream isr = null;
		try {
			isr = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Performance.LOG.log(Level.SEVERE,
					"GetTestCount FileNotFoundException:" + e.getStackTrace());
		}
		InputSource source = new InputSource(isr);
		try {
			reader.parse(source);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Performance.LOG.log(Level.SEVERE,
					"GetTestCount IOException:" + e.getStackTrace());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			Performance.LOG.log(Level.SEVERE,
					"GetTestCount SAXException:" + e.getStackTrace());
		}
	}

	public String getCount() {
		return count;
	}

	// 用于解析配置文件
	class MyHandle extends DefaultHandler {
		private String name;

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			super.endElement(uri, localName, qName);
		}

		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes attrs) throws SAXException {
			// TODO Auto-generated method stub
			name = qName;
			super.startElement(namespaceURI, localName, qName, attrs);
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			super.characters(ch, start, length);
			if (name == null)
				return;
			String content = new String(ch, start, length);
			if (name.equals("count")) {
				count = content;
			}
			name = null;
		}
	}
}
