package com.sprd.performance.testflow;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.sprd.performance.main.Performance;
import com.sprd.performance.util.CmdUtil;
import com.sprd.performance.util.LogInit;

/**
 * @ClassName: CommandHelper
 * @Description:TODO 封装了一些adb操作
 * @author: Author name
 * @date: 2015年9月1日 下午3:18:33
 * 
 */
public class CommandHelper {
	// 所有case的存放路径
	private static String case_file = System.getProperty("user.dir")
			+ File.separator + "tools" + File.separator + "jars";
	private static String SHELL_EXE_BASE = "/data/local/tmp/";
	private String sn;

	public CommandHelper(String sn) {
		String newsn = sn.equals("") ? "" : "-s " + sn;
		this.sn = newsn;
	}

	// 安装apk,
	// param: apk路径
	public void installApk(String apk) {
		Performance.LOG.log(Level.INFO, "installApk:" + apk);
		try {
			Runtime.getRuntime().exec("adb " + sn + " root ");
			Thread.sleep(2000);
			Runtime.getRuntime().exec("adb " + sn + " remount ");
			Thread.sleep(2000);
			Performance.LOG.log(Level.INFO, "adb " + sn + " install -r \""
					+ apk + "\"");
			Runtime.getRuntime().exec(
					"adb " + sn + " install -r \"" + apk + "\"");
			Thread.sleep(55000);
		} catch (IOException e) {
			Performance.LOG.log(Level.SEVERE,
					"installApk IOException:" + e.getStackTrace());
		} catch (InterruptedException e) {
			Performance.LOG.log(Level.SEVERE,
					"installApk InterruptedException:" + e.getStackTrace());
		}
	}

	// push case jar
	// param: jar路径
	public void pushJar(String name) {
		Performance.LOG.log(Level.INFO, "pushJar:" + name);
		try {
			Performance.LOG.log(Level.INFO, "adb " + sn + " push \"" + name
					+ "\" " + SHELL_EXE_BASE);
			Runtime.getRuntime().exec(
					"adb " + sn + " push \"" + name + "\" " + SHELL_EXE_BASE);
			Thread.sleep(4000);
		} catch (IOException e) {
			Performance.LOG.log(Level.SEVERE,
					"pushJar IOException:" + e.getStackTrace());
		} catch (InterruptedException e) {
			Performance.LOG.log(Level.SEVERE, "pushJar InterruptedException:"
					+ e.getStackTrace());
		}
	}

	// 执行case jar
	// param: 路径，jar名称，包名称,apk路径
	public void runCase(String dir, String name, String pkgname, String apkpath) {
		WriteToExcel writeToExcel = new WriteToExcel(dir, sn);
		String apkpkgname = CmdUtil.getPkgName(apkpath);// 获取apk的包名，用于后面的卸载和强制停止
		for (int i = 0; i < Integer.valueOf(Performance.count); i++) {
			Performance.LOG.log(Level.INFO, "runCase:" + name + ",i:" + i);
			Process process = null;
			try {
				process = Runtime.getRuntime().exec(
						"adb " + sn + " shell uiautomator runtest " + name
								+ ".jar -c " + pkgname);
				try {
					process.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Performance.LOG
							.log(Level.SEVERE, "runCase InterruptedException:"
									+ e.getStackTrace());
				}
				InputStream inputStream = process.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line = "";
				String str = "";
				while ((line = reader.readLine()) != null) {
					str = str + "\n" + line;
				}
				Performance.LOG.log(Level.INFO, "runCase end.\n" + str);
				reader.close();
				if (process != null)
					process.destroy();
				writeToExcel.collectResult(name, i);
			} catch (IOException e) {
				Performance.LOG.log(Level.SEVERE,
						"runCase IOException:" + e.getStackTrace());
			}
			//强制停止掉该apk
			stopApks(apkpkgname);
		}
		deleteJars();
		uninstallAndReboot(apkpkgname);
	}

	/**   
	 * @Title: uninstallAndReboot   
	 * @Description: TODO(卸载apk并重启)   
	 * @param: @param pkgname      
	 * @return: void      
	 * @throws   
	 */  
	private void uninstallAndReboot(String apkpkgname) {
		Performance.LOG.log(Level.INFO, "uninstallAndReboot");
		try {
			Performance.LOG.log(Level.INFO, "adb " + sn
					+ " uninstall " + apkpkgname);
			Runtime.getRuntime().exec(
					"adb " + sn
					+ " uninstall " + apkpkgname);
			Thread.sleep(3000);
			Runtime.getRuntime().exec(
					"adb " + sn
					+ " reboot ");
			Process process = Runtime.getRuntime().exec(
					"adb " + sn
					+ " wait-for-device ");
			process.waitFor();
			Thread.sleep(60000);
			Performance.LOG.log(Level.INFO, "reboot Ok");
		} catch (IOException e) {
			Performance.LOG.log(Level.SEVERE,
					"uninstallAndReboot IOException:" + e.getStackTrace());
		} catch (InterruptedException e) {
			Performance.LOG.log(Level.SEVERE,
					"uninstallAndReboot InterruptedException:" + e.getStackTrace());
		}		
	}

	// 强制停止apk
	private void stopApks(String apkpkgname) {
		Performance.LOG.log(Level.INFO, "stopApks");
		try {
			Performance.LOG.log(Level.INFO, "adb " + sn
					+ " shell am force-stop " + apkpkgname);
			Runtime.getRuntime().exec(
					"adb " + sn
					+ " shell am force-stop " + apkpkgname);
			//强制停止后等待恢复一会
			Thread.sleep(60000);
		} catch (IOException e) {
			Performance.LOG.log(Level.SEVERE,
					"stopApks IOException:" + e.getStackTrace());
		} catch (InterruptedException e) {
			Performance.LOG.log(Level.SEVERE,
					"stopApks InterruptedException:" + e.getStackTrace());
		}
	}

	// 删除case jar
	private void deleteJars() {
		Performance.LOG.log(Level.INFO, "deleteJars");
		try {
			Performance.LOG.log(Level.INFO, "adb " + sn + " shell rm "
					+ SHELL_EXE_BASE + "*.jar");
			Runtime.getRuntime().exec(
					"adb " + sn + " shell rm " + SHELL_EXE_BASE + "*.jar");
			Thread.sleep(3000);
		} catch (IOException e) {
			Performance.LOG.log(Level.SEVERE,
					"deleteJars IOException:" + e.getStackTrace());
		} catch (InterruptedException e) {
			Performance.LOG.log(Level.SEVERE,
					"deleteJars InterruptedException:" + e.getStackTrace());
		}
	}

	// 把手机的xml push出来
	// param: push到的路径
	public void pullResult(String dir, String name) {
		Performance.LOG.log(Level.INFO, "pullResult");
		try {
			Performance.LOG.log(Level.INFO, "adb " + sn + " pull "
					+ SHELL_EXE_BASE + name + ".xml " + dir);
			Runtime.getRuntime().exec(
					"adb " + sn + " pull " + SHELL_EXE_BASE + name + ".xml "
							+ dir);
			Performance.LOG.log(Level.INFO, "adb " + sn + " shell rm "
					+ SHELL_EXE_BASE + name + ".xml ");
			Thread.sleep(3000);
			Runtime.getRuntime().exec(
					"adb " + sn + " shell rm " + SHELL_EXE_BASE + name
							+ ".xml ");
			Thread.sleep(3000);
		} catch (IOException e) {
			Performance.LOG.log(Level.SEVERE,
					"pullResult IOException:" + e.getStackTrace());
		} catch (InterruptedException e) {
			Performance.LOG.log(Level.SEVERE,
					"pullResult InterruptedException:" + e.getStackTrace());
		}
	}

}
