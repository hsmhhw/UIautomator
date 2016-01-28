/**  
 * All rights Reserved, Designed By Spreadtrum   
 * @Title:  CmdUtil.java   
 * @Package com.sprd.performance.util   
 * @Description:    TODO  
 * @author: shan.ji   
 * @date:   2015年10月9日 上午9:44:05   
 * @version V1.0     
 */
package com.sprd.performance.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import com.sprd.performance.main.Performance;

/**
 *@auther: Ji Shan
 *@E-mail: Shan.Ji@spreadtrum.com
 *@version
 *@创建时间：
 *@类说明:
 */
/**
 * @ClassName: CmdUtil
 * @Description:TODO(工具类)
 * @author: shan.ji
 * @date: 2015年10月9日 上午9:44:05
 * 
 */
public class CmdUtil {
	public static String getPkgName(String apkpath) {
		Process process = null;
		String pkgname = null;
		try {
			process = Runtime.getRuntime().exec("aapt dump badging " + apkpath);
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Performance.LOG.log(Level.SEVERE,
						"getPkgName InterruptedException:" + e.getStackTrace());
			}
			InputStream inputStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String line = "";
			String str = "";
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("package: name='")) {
					String strs[] = line.split("'");
					pkgname = strs[1];
				}
				str = str + "\n" + line;
			}
			Performance.LOG.log(Level.INFO, "getPkgName end.\n" + str);
			reader.close();
			if (process != null)
				process.destroy();
		} catch (IOException e) {
			Performance.LOG.log(Level.SEVERE,
					"getPkgName IOException:" + e.getStackTrace());
		}
		return pkgname;
	}
}
