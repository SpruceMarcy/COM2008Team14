package myUI;

import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Work {
	public int workID;
	public String title;
	public String _abstract;
	public int state;
	public int issn;
	public File pdf;
	byte[] pdfFileBytes;
	public Work(int workID, String title, String _abstract, File pdf, int state) {
		this.workID = workID;
		this.title = title;
		this._abstract = _abstract;
		this.pdf = pdf;
		this.state = state;
	}

	public Work(String title, String _abstract, File pdf, int issn) {
		this.workID = -1;
		this.title = title;
		this._abstract = _abstract;
		this.pdf = pdf;
		this.issn = issn;
		this.state = 0;
	}
	public Work(String title, String _abstract, byte[] pdfFileBytes, int issn) {
		this.workID = -1;
		this.title = title;
		this._abstract = _abstract;
		this.pdfFileBytes = pdfFileBytes;
		this.issn = issn;
		this.state = 0;
	}
	public Work(int workID, String title, String _abstract, byte[] pdfFileBytes, int state) {
		this.workID = workID;
		this.title = title;
		this._abstract = _abstract;
		this.pdfFileBytes = pdfFileBytes;
		this.state = state;
	}
	public Author mainAuthor;
	public ArrayList<Author> authors = new ArrayList<Author>();
	public File getPdf() {
		if(pdf!=null)return pdf;
		pdf = new File(workID+"_"+title+"_"+(state==1?"draft":"final")+".pdf");
		try {
			if (!pdf.exists()) {
				pdf.createNewFile();
			}
			OutputStream targetFile=  new FileOutputStream(pdf);
	        targetFile.write(pdfFileBytes);
	        targetFile.flush();
	        targetFile.close();
		}
		catch(Exception e) {
			System.out.println(e);
			return null;
		}
		return pdf;
	}
	
}
