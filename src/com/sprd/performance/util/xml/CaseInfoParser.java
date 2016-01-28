package com.sprd.performance.util.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.sprd.performance.main.Performance;
import com.sprd.performance.util.xml.AbstractXmlParser.ParseException;

public class CaseInfoParser {
	private String tmppackage = "";

	public CaseInfoParser(String path) {
		Performance.LOG.log(Level.INFO, "CaseInfoParser,path:"+path);
		File file = new File(path);
		Parser parser = new Parser();
		try {
			parser.parse(new BufferedReader(new FileReader(file)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Performance.LOG.log(Level.SEVERE, "CaseInfoParser FileNotFoundException:"+e.getStackTrace());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Performance.LOG.log(Level.SEVERE, "CaseInfoParser ParseException:"+e.getStackTrace());
		}
	}

	public String getPackageName() {
		String tmp = "";
		if (tmppackage.length() > 1)
			tmp = tmppackage.substring(0, tmppackage.length() - 1);
		return tmp;
	}

	public String getCaseName(String packagename) {
		String names[] = packagename.split("\\.");
		String casename = names[names.length - 1] + ".jar";
		return casename;
	}

	public class Parser extends AbstractXmlPullParser {
		private static final String TAG = "TestPackage";
		private static final String SUITTAG = "TestSuite";
		private static final String CASETAG = "TestCase";

		@Override
		public void parse(XmlPullParser parser) throws XmlPullParserException,
				IOException {
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals(SUITTAG)) {
						String name = parser.getAttributeValue("", "name");
						tmppackage = tmppackage + name + ".";
					} else if (parser.getName().equals(CASETAG)) {
						String name = parser.getAttributeValue("", "name");
						tmppackage = tmppackage + name + ",";

					}
				}
				eventType = parser.next();
			}
		}
	}

}
