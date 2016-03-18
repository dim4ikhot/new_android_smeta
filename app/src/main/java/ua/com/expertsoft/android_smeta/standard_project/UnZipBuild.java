package ua.com.expertsoft.android_smeta.standard_project;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import android.util.Log;

public class UnZipBuild {

	private String _zipFile; 
	private String _location; 
	
	public UnZipBuild(String zipFile, String location) { 
		_zipFile = zipFile; 
    	_location = location; 
 
//    	_dirChecker(""); 
	} 
	//Unzipping All in Cp1251 
	public File ExUnzip() throws IOException {
	    File ReturnedFile;
	    ZipArchiveInputStream zis = null;
	    FileOutputStream fos = null;
	    File file = null;
	    try {
	        byte[] buffer = new byte[8192];
	        zis = new ZipArchiveInputStream(new FileInputStream(_zipFile), "Cp1251", true); // this supports non-USACII names
	        ArchiveEntry entry;	        
	        while ((entry = zis.getNextEntry()) != null) {	        	
	        	ReturnedFile = new File(_location);
	            //File file = new File(_location, entry.getName());
	            file = new File(ReturnedFile.getPath() + "/tempFile.xml");
	            if (entry.isDirectory()) {
	                file.mkdirs();
	            } else {
	                file.getParentFile().mkdirs();
	                fos = new FileOutputStream(file);
	                int read;
	                while ((read = zis.read(buffer,0,buffer.length)) != -1)
	                    fos.write(buffer,0,read);
	                fos.close();
	                fos=null;
	            }
	        }
	    } finally {
	        try { zis.close(); } catch (Exception e) { }
	        try { if (fos!=null) fos.close(); } catch (Exception e) { }
	        ReturnedFile = file;
	        if(ReturnedFile.isFile())
	        	return ReturnedFile;
	    }
	    return ReturnedFile;
	}
	
	//zipping All in Cp1251 
		public File exZip(File fileXML) throws IOException {		   
		    File ReturnedFile = null;		    		    
		    ZipArchiveOutputStream zas = null;
		    FileInputStream fis = null;
		    File file = null;
		    try {
		        byte[] buffer = new byte[8192];
		        file = new File(_zipFile);
		        zas = new ZipArchiveOutputStream(file); // this supports non-USACII names
		        ZipArchiveEntry entry = new ZipArchiveEntry(fileXML.getName());        		        
		        zas.putArchiveEntry(entry);
		        fis = new FileInputStream(fileXML);
		        int length;
                while ((length = fis.read(buffer))!= -1) {
                    zas.write(buffer, 0, length);
                }                      		        	        		       
		    } finally {
		    	try { 
		    		if (fis!=null) {
		    			fis.close();
		    			} 
		    	}catch (Exception e) { }		    		
		    	try { 
		    		zas.closeArchiveEntry(); 
		    	} catch (Exception e) { }
		        try { 
		        	zas.close(); 
		       	} catch (Exception e) { }		        		        
		        ReturnedFile = file;
		        fileXML.delete();
		        if(ReturnedFile.isFile())
		        	return ReturnedFile;
		    }
		    return ReturnedFile;
		}
	
	//Unzipping only in UTF-8
	public File unzip() {
		String unzippedFile = "";
	    try {	        
	        ZipInputStream zipStream = new ZipInputStream(new FileInputStream(_zipFile));	     
	        ZipEntry zEntry = null;	     	        
	        while ((zEntry = zipStream.getNextEntry()) != null) {
	            Log.d("myLogs", "Unzipping " + zEntry.getName() + " at "+ _location);

	            if (zEntry.isDirectory()) {
	            	_dirChecker(zEntry.getName());
	            } else {
	            	unzippedFile = this._location + "/" + "tempFile.xml";
	                FileOutputStream fout = new FileOutputStream(unzippedFile);
	                BufferedOutputStream bufout = new BufferedOutputStream(fout);
	                byte[] buffer = new byte[1024];
	                int read = 0;
	                while ((read = zipStream.read(buffer)) != -1) {
	                    bufout.write(buffer, 0, read);
	                }
	                zipStream.closeEntry();
	                bufout.close();
	                fout.close();
	            }
	        }
	        zipStream.close();
	        Log.d("myLogs", "Unzipping complete. path :  " + _location);
	    } catch (Exception e) {
	        Log.d("myLogs", "Unzipping failed:" + e.getMessage().toString());
	        e.printStackTrace();
	    }
	    return new File(unzippedFile);

	} 
	 
	  private void _dirChecker(String dir) { 
	    File f = new File(_location + dir); 	 
	    if(!f.isDirectory()) { 
	      f.mkdirs(); 
	    } 
	  } 
}
