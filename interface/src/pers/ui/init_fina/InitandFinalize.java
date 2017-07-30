package pers.ui.init_fina;

import java.io.*;
import java.util.*;

public class InitandFinalize {

	public PersonalSettings personalSettings;
	private Properties properties;
	public InitandFinalize()
	{
		personalSettings=new PersonalSettings();
		properties = new java.util.Properties(); 
		ReadFromFile();
		
	}
	 private void ReadFromFile() {
		// TODO Auto-generated method stub
		 
		 InputStream inStream;
		try {
			inStream = new java.io.FileInputStream("Settings.properties");
			properties.load(inStream); 
			personalSettings.IP=(String) properties.getProperty("IP","0.0.0.0");
			personalSettings.PORT=new Integer(properties.getProperty("PORT","12345"));
			personalSettings.DLLpath=(String)properties.getProperty("DLLpath","NetworkDebugger.dll");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			personalSettings.IP="0.0.0.0";
			personalSettings.PORT=12345;
			personalSettings.DLLpath="NetworkDebugger.dll";
			return ;
		}

	}
	 @SuppressWarnings("deprecation")
	public int StoretoFile()
	 {
		 OutputStream outputStream;
		try {
			outputStream = new FileOutputStream("Settings.properties");
			properties.save(outputStream, "更新配置");
			 return 0;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

	 }
	 @Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		StoretoFile();
	}
}
