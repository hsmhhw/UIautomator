package com.sprd.performance.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import com.sprd.performance.main.Performance;

/**
 *@auther: Ji Shan
 *@E-mail: Shan.Ji@spreadtrum.com
 *@version
 *@创建时间：
 *@类说明:
 */
public class LogInit {
	
	public static String getCurrentTime() {
		String returnStr = null;
		SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
		Date date = new Date();
		returnStr = f.format(date);
		return returnStr;
	}
	
	public void initLog(String sn) {
		FileHandler fh = null;
		String newsn = sn.equals("") ? "" : "_" + sn;
		SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
		try {
			File logDir = new File("Logs");
			if (!logDir.isDirectory() || !logDir.exists()) {
				logDir.mkdir();
			}
			fh = new FileHandler(logDir + "//Performance_"
					+ format.format(Calendar.getInstance().getTime()) 
					+ newsn + ".log");

			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		fh.setLevel(Level.ALL);
		Performance.LOG.addHandler(fh);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.SEVERE);
		Performance.LOG.addHandler(handler);
		Performance.LOG.setUseParentHandlers(false);
	}
}
