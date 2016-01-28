package com.sprd.performance.testflow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import jxl.CellView;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.sprd.performance.main.Performance;

/**
 * @ClassName: WriteToExcel
 * @Description:TODO(主要是一些excel相关的操作)
 * @author: shan.ji
 * @date: 2015年9月1日 下午4:14:42
 * 
 */
public class WriteToExcel {
	private static String SHELL_EXE_BASE = "/data/local/tmp/";
	int startRow = 1, startColumn = 0;
	int curRowSheetRes = startRow, curColumnSheetRes = startColumn;

	private String sn;
	private String dir;
	private String oldsn;

	public WriteToExcel(String dir, String sn) {
		this.oldsn = sn;
		String newsn = sn.equals("") ? "" : "-s " + sn;
		this.sn = newsn;
		this.dir = dir;
	}

	public void collectResult(String name, int i) {
		Performance.LOG.log(Level.INFO, "collectResult");
		// 每一类测试新建一个目录
		File file = new File(dir, name);
		if (!file.exists()) {
			file.mkdirs();
		}
		CollectScreenShot collectScreenShot = new CollectScreenShot(oldsn);
		collectScreenShot.pullScreen(file.getPath(), i);
		writrXmlToExcel(name, i);
	}

	// 将结果写入excel
	private void writrXmlToExcel(String name, int i) {
		Performance.LOG.log(Level.INFO, "writrXmlToExcel");
		CommandHelper commandHelper = new CommandHelper(oldsn);
		commandHelper.pullResult(dir + File.separator + name, name);
		// 如果是第一次，则需要填写excel名称信息
		if (i == 0) {
			List<String> names = null;
			// 获取所有测试项名称
			try {
				names = getTestNames(name, "0");
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				Performance.LOG.log(
						Level.SEVERE,
						"writrXmlToExcel DocumentException:"
								+ e.getStackTrace());
				return;
			}
			// 初始化excel,填充名称信息
			try {
				initExcel(name, names);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				Performance.LOG.log(
						Level.SEVERE,
						"writrXmlToExcel RowsExceededException:"
								+ e.getStackTrace());
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				Performance.LOG.log(Level.SEVERE,
						"writrXmlToExcel WriteException:" + e.getStackTrace());
			}
		}
		// 从xml获取值
		List<String> values = null;
		try {
			values = getTestNames(name, "1");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			Performance.LOG.log(Level.SEVERE,
					"writrXmlToExcel DocumentException:" + e.getStackTrace());
			return;
		}
		// 填值进去
		try {
			writeToExcel(name, values);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			Performance.LOG.log(
					Level.SEVERE,
					"writrXmlToExcel RowsExceededException:"
							+ e.getStackTrace());
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			Performance.LOG.log(Level.SEVERE, "writrXmlToExcel WriteException:"
					+ e.getStackTrace());
		}
		// 值填充到excel后删除antutu.xml
		File xmlfile = new File(dir + File.separator + name, name + ".xml");
		if (xmlfile.exists()) {
			xmlfile.delete();
		}
	}

	// 填充值到Excel
	private void writeToExcel(String name, List<String> values)
			throws RowsExceededException, WriteException {
		Performance.LOG.log(Level.INFO, "writeToExcel:");
		// 写入数据
		Workbook wb;
		WritableWorkbook wwb = null;
		try {
			wb = Workbook.getWorkbook(new File(dir + File.separator + name,
					name + ".xls"));
			wwb = Workbook.createWorkbook(new File(dir + File.separator + name,
					name + ".xls"), wb);
		} catch (BiffException e2) {
			// TODO Auto-generated catch block
			Performance.LOG.log(Level.SEVERE, "in writeToExcel BiffException:"
					+ e2.getStackTrace());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			Performance.LOG.log(Level.SEVERE, "in writeToExcel IOException:"
					+ e2.getStackTrace());
		}
		// 装载模板
		WritableSheet ws = wwb.getSheet(0);
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				Label label = new Label(curColumnSheetRes, curRowSheetRes,
						values.get(i));
				ws.addCell(label);
				curRowSheetRes++;
			}
			curRowSheetRes = startRow;
			curColumnSheetRes++;
			try {
				wwb.write();
				wwb.close();
			} catch (Exception e) {
				Performance.LOG.log(Level.SEVERE, "in writeToExcel Exception:"
						+ e.getMessage());
			}
		}
	}

	// 获取所有测试项的值或者名称
	// params:1.name:测试的是哪个apk,2.type:0表示或者名称，1表示获取值
	private List<String> getTestNames(String name, String type)
			throws DocumentException {
		List<String> keys = new ArrayList<String>();
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new File(
				dir + File.separator + name, name + ".xml"));
		// 获取根元素
		Element root = document.getRootElement();
		for (Iterator iter = root.elementIterator(); iter.hasNext();) {
			Element e = (Element) iter.next();
			if (type.equals("0")) {
				String key = e.attributeValue("name");
				keys.add(key);
			} else {
				String key = e.attributeValue("value");
				keys.add(key);
			}
		}
		return keys;
	}

	// 初始化excel
	private void initExcel(String name, List<String> names)
			throws RowsExceededException, WriteException {
		Performance.LOG.log(Level.INFO, "initExcel");
		WritableSheet sheetRes;
		WritableWorkbook copy = null;
		File xlsFile = new File(dir + File.separator + name, name + ".xls");
		try {
			copy = Workbook.createWorkbook(xlsFile);
		} catch (IOException e) {
			Performance.LOG.log(Level.SEVERE,
					"initExcel IOException:" + e.getStackTrace());
		}
		copy.createSheet("result", 1);
		sheetRes = copy.getSheet(0);

		CellView navCellView = new CellView();
		navCellView.setAutosize(true); // 设置自动大小
		navCellView.setSize(18);
		sheetRes.setColumnView(0, navCellView); // 设置col显示样式
		if (names != null) {
			for (int i = 0; i < names.size(); i++) {
				Label label = new Label(curColumnSheetRes, curRowSheetRes,
						names.get(i));
				sheetRes.addCell(label);
				curRowSheetRes++;
			}
			curRowSheetRes = startRow;
			curColumnSheetRes++;

			try {
				copy.write();
				copy.close();
			} catch (Exception e) {
				Performance.LOG.log(Level.SEVERE,
						"initExcel Exception:" + e.getStackTrace());
			}
		}
	}

	public static void main(String args[]) {
		WriteToExcel writeToExcel = new WriteToExcel(
				"E:\\workspace\\Performance\\result\\2015.09.02_09.36.58", "");
		writeToExcel.writrXmlToExcel("antutu", 0);
	}
}
