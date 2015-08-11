package com.iox.rms.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class WordTest {
	
	public WordTest() {
		
	}
	
	public static void testGenerate() {
		//HWPFDocument template = null;
		XWPFDocument template = null;
		try {
			template = new XWPFDocument(new FileInputStream(new File("c:/files/CERTIFICATE OF INSTALLATION TEMPLATE.docx")));
			replaceText(template, new String[]{"<OWNER_NAME>"}, new String[]{"OLAORE OLADELE"});
			//template.getRange().replaceText("<OWNER_NAME>", "OLAORE OLADELE");
			
			FileOutputStream fout = new FileOutputStream(new File("c:/files/CERTIFICATE OF INSTALLATION TEST.docx"));
			template.write(fout);
			System.out.println("File Saved!");
			fout.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void replaceText(XWPFDocument doc, String[] toReplace, String[] replaceWith) {
		for (XWPFParagraph p : doc.getParagraphs()) {
		    List<XWPFRun> runs = p.getRuns();
		    if (runs != null) {
		        for (XWPFRun r : runs) {
		            String text = r.getText(0);
		            if (text != null) {
		            	for(int i=0; i<toReplace.length; i++) {
		            		String tr = toReplace[i];
		            		if(text.toLowerCase().contains(tr.toLowerCase())) {
		            			text = text.replace(tr, replaceWith[i]);
				                r.setText(text, 0);
		            		}
		            	}
		            }
		        }
		    }
		}
		for (XWPFTable tbl : doc.getTables()) {
		   for (XWPFTableRow row : tbl.getRows()) {
		      for (XWPFTableCell cell : row.getTableCells()) {
		         for (XWPFParagraph p : cell.getParagraphs()) {
		            for (XWPFRun r : p.getRuns()) {
		              String text = r.getText(0);
		              if (text != null) {
		            	  for(int i=0; i<toReplace.length; i++) {
			            		String tr = toReplace[i];
			            		if(text.toLowerCase().contains(tr.toLowerCase())) {
			            			text = text.replace(tr, replaceWith[i]);
					                r.setText(text, 0);
			            		}
			            	}
		              }
		            }
		         }
		      }
		   }
		}
	}
	
	public static void main(String[] agrs) {
		WordTest.testGenerate();
	}
}
