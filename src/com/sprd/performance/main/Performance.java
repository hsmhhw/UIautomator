package com.sprd.performance.main;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sprd.performance.testflow.TestPerformance;
import com.sprd.performance.util.LogInit;
import com.sprd.performance.util.xml.GetTestCount;


/**   
 * @ClassName:  Performance   
 * @Description:TODO(程序总入口)   
 * @author:shan.ji   
 * @date:   2015年9月1日 下午2:19:51   
 *      
 */ 
public class Performance {
	
	public static final Logger LOG = Logger.getLogger(Performance.class
			.getName());
	//配置文件路径
	private static String config_file = System.getProperty("user.dir")
			+ File.separator + "AntutuSetting.xml";
	//测试次数
	public static String count;
	//手機sn号码
	private static String sn;
	
	public static void main(String args[]) {
		Performance performance = new Performance();
		performance.init(args);
		TestPerformance testPerformance = new TestPerformance(count, sn);
		testPerformance.startTest();
	}

	//初始化
	private  void init(String[] args) {
		Performance.LOG.log(Level.INFO, "init:");
		//获取sn号
		if(args.length ==0){
			sn = "";
		}else{
			sn = args[0];
		}
		//log初始化
		LogInit logInit = new LogInit();
		logInit.initLog(sn);
		//获取测试次数
		GetTestCount getTestCount = new GetTestCount(config_file);
		count = getTestCount.getCount();
		Performance.LOG.log(Level.INFO, "count:"+count);			
	}
}
