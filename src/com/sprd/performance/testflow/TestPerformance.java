package com.sprd.performance.testflow;

import java.io.File;
import java.util.logging.Level;

import com.sprd.performance.main.Performance;
import com.sprd.performance.util.LogInit;
import com.sprd.performance.util.xml.CaseInfoParser;

/**
 * @ClassName: TestPerformance
 * @Description:TODO 性能测试的主要流程在这个文件中
 * @author: Author name
 * @date: 2015年9月1日 下午3:08:57
 * 
 */
public class TestPerformance {
	// 所有apk的存放路径
	private static String apk_file = System.getProperty("user.dir")
			+ File.separator + "tools" + File.separator + "apks";
	// 结果目录
	public static final String resultDir = System.getProperty("user.dir")
			+ File.separator + "result";
	// 所有case的存放路径
	private static String case_file = System.getProperty("user.dir")
			+ File.separator + "tools" + File.separator + "jars";

	private String sn;
	private String count;

	public TestPerformance(String count, String sn) {
		Performance.LOG.log(Level.INFO, "count:" + count + ",sn:" + sn);
		this.sn = sn;
		this.count = count;
	}

	/**
	 * 
	 * @Title: getFileNameNoEx   
	 * @Description: 去掉文件名的后缀   
	 * @param: @param 原文件名
	 * @return: 去掉后缀后的文件名    
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	// 主要测试步骤
	public void startTest() {
		Performance.LOG.log(Level.INFO, "startTest");
		CommandHelper commandHelper = new CommandHelper(sn);
		CollectScreenShot collectScreenShot = new CollectScreenShot(sn);
		collectScreenShot.makeScreenShotDir();
		File file = new File(apk_file);
		File[] apks = file.listFiles();
		//先生成一个以时间和sn号组成的文件夹
		String dir = makeResultdir();
		if (apks.length != 0) {
			for (File apk : apks) {
				// 安装apk
				if(!apk.getName().equals(".svn")){
					commandHelper.installApk(apk.getAbsolutePath());
					String name = getFileNameNoEx(apk.getName());
					// push jar包
					commandHelper.pushJar(case_file + File.separator + name
							+ ".jar");
					// 解析xml
					CaseInfoParser caseInfoParser = new CaseInfoParser(case_file
							+ File.separator + name + ".xml");
					String pkgname = caseInfoParser.getPackageName();
					// 删除所有图片
					collectScreenShot.delScreenShot();		
					// 开始测试
					commandHelper.runCase(dir,name, pkgname,apk.getAbsolutePath());
				}			
			}
		}
	}

	// 建立结果目录
	private String makeResultdir() {
		String timedir = LogInit.getCurrentTime();
		String dir = null;
		if (timedir != null) {
			dir = resultDir + File.separator + timedir + sn;
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		return dir;
	}
}
