package pers.ui.repeater;

import java.io.*;

import java.net.Socket;

import pers.data.ProxyDataStatus;

public class SocketClient extends Thread{
	SplitRepeater splitRepeater;
	Socket socket;
	
	DataOutputStream dataOS;
	DataInputStream dataIS;
	int recvIndex=0;
	public SocketClient(String ipAddress,int port,SplitRepeater tSplitRepeater){
		splitRepeater=tSplitRepeater;
		try {
			socket =new Socket(ipAddress,port);
			OutputStream  os = socket.getOutputStream();//字节输出流
			 dataOS =new DataOutputStream(os);
			InputStream is = socket.getInputStream();
			dataIS=new DataInputStream(is);
			
			this.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void SendData(byte[] tData){
		try {
			dataOS.write(tData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void run() {
		try {
			while (true) {
				recvIndex+=1;
				byte[] tData = new byte[1024];
				int len=dataIS.read(tData);
				System.out.println(new String(tData,0,len));
				splitRepeater.NoticeNewDatafromDst(splitRepeater.NowKey, recvIndex, ProxyDataStatus.SENT, tData);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
