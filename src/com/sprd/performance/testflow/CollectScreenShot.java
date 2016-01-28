package com.sprd.performance.testflow;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.sprd.performance.main.Performance;

/**
 * @ClassName: CollectScreenShot
 * @Description:TODO(主要是截图相关的操作)
 * @author: Author name
 * @date: 2015年9月1日 下午4:58:36
 * 
 */
public class CollectScreenShot {
	private String sn;

	public CollectScreenShot(String sn) {
		String newsn = sn.equals("") ? "" : "-s " + sn;
		this.sn = newsn;
	}

	// 删除截图
	public void delScreenShot() {
		Performance.LOG.log(Level.INFO, "delScreenShot:");
		try {
			Runtime.getRuntime().exec(
					"adb " + sn + " shell rm  /storage/sdcard0/pictures/screenshot/*.png");
			Thread.sleep(2000);
		} catch (IOException e) {
			Performance.LOG.log(Level.SEVERE,
					"delScreenShot IOException:" + e.getStackTrace());
		} catch (InterruptedException e) {
			Performance.LOG.log(Level.SEVERE,
					"delScreenShot InterruptedException:" + e.getStackTrace());
		}
	}

	// pull截图
	public void pullScreen(String path,int i) {
		Performance.LOG.log(Level.INFO, "pullScreen:path:" + path + "i:" + i);
		try {
			Runtime.getRuntime().exec(
					"adb " + sn + " pull /storage/sdcard0/pictures/screenshot/ " + path);
			Thread.sleep(2000);
		} catch (IOException e) {
			Performance.LOG.log(Level.SEVERE,
					"pullScreen IOException:" + e.getStackTrace());
		} catch (InterruptedException e) {
			Performance.LOG.log(Level.SEVERE,
					"pullScreen InterruptedException:" + e.getStackTrace());
		}
		renameAndDel(path,i);
	}

	// 把截图全部重新命名，使可以方便的从截图看出是第几次测试
	public void renameAndDel(String path,int i) {
		Performance.LOG
				.log(Level.INFO, "renameAndDel:path:" + path + ",i:" + i);
		File folder = new File(path);
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.getName().endsWith(".png")) {
				String newname = path + File.separator + String.valueOf(i + 1)
						+ "_" + fileEntry.getName();
				File targetFile = new File(newname);
				if (!fileEntry.getName().contains("_")) {
					fileEntry.renameTo(targetFile);
					fileEntry.deleteOnExit();
				}
			} else if (!(fileEntry.getName().endsWith(".xls"))) {
				fileEntry.deleteOnExit();
			}
		}
		delScreenShot();
	}

	/**   
	 * @Title: makeScreenShotDir   
	 * @Description: TODO(创建截图目录)   
	 * @param:       
	 * @return: void      
	 * @throws   
	 */  
	public void makeScreenShotDir() {
		Performance.LOG.log(Level.INFO, "makeScreenShotDir");
		try {
			Runtime.getRuntime().exec(
					"adb " + sn + " root");
			Thread.sleep(2000);
			Runtime.getRuntime().exec(
					"adb " + sn + " remount");
			Thread.sleep(2000);
			Runtime.getRuntime().exec(
					"adb " + sn + " shell mkdir /storage/sdcard0/pictures/");
			Thread.sleep(2000);
			Runtime.getRuntime().exec(
					"adb " + sn + " shell mkdir /storage/sdcard0/pictures/screenshot/ ");
			Thread.sleep(2000);
		} catch (IOException e) {
			Performance.LOG.log(Level.SEVERE,
					"makeScreenShotDir IOException:" + e.getStackTrace());
		} catch (InterruptedException e) {
			Performance.LOG.log(Level.SEVERE,
					"makeScreenShotDir InterruptedException:" + e.getStackTrace());
		}
	}

}
