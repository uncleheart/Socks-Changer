package pers.dll.infc;
import pers.data.*;

public  class JavaInvokeCPlus {
	protected NoticeCallback noticeCallback;
	public JavaInvokeCPlus(NoticeCallback tnoticeCallback)
	{
		noticeCallback=tnoticeCallback;
		System.loadLibrary("SocksChanger");
		Init();
		InitNewCon("pers/dll/infc/JavaInvokeCPlus", "NoticeNewCon", "(Lpers/dll/infc/NewConData;)I");
		InitNewDatafromDst("pers/dll/infc/JavaInvokeCPlus", "NoticeNewDatafromDst", "(II[B)I");
		InitNewDatafromSource("pers/dll/infc/JavaInvokeCPlus", "NoticeNewDatafromSource", "(II[B)I");
		InitClose("pers/dll/infc/JavaInvokeCPlus", "NoticeClose", "(I)I");
		InitNewConData("pers/dll/infc/NewConData", "<init>", "(IILjava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;I)V");
	}
	public JavaInvokeCPlus(NoticeCallback tnoticeCallback,String dllpath)
	{
		noticeCallback=tnoticeCallback;
		System.load(dllpath);
		Init();
		InitNewCon("pers/dll/infc/JavaInvokeCPlus", "NoticeNewCon", "(Lpers/dll/infc/NewConData;)I");
		InitNewDatafromDst("pers/dll/infc/JavaInvokeCPlus", "NoticeNewDatafromDst", "(II[B)I");
		InitNewDatafromSource("pers/dll/infc/JavaInvokeCPlus", "NoticeNewDatafromSource", "(II[B)I");
		InitClose("pers/dll/infc/JavaInvokeCPlus", "NoticeClose", "(I)I");
		InitNewConData("pers/dll/infc/NewConData", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	}
	public int TestfromJNI(int index, String hostname, String address, int port) {
		System.out.println( index +address+port+ hostname);
		return 1;
	}
	/////////////////////////////////////////////////////////////////////////////////////
	//下面是dll调用java的地方
/////////////////////////////////////////////////////////////////////////////////////
	
	//接收到新的请求，开始建立连接  
	//!!!注意  在调用前需要先设置该函数签名
//	public  int  NoticeNewCon(int index, String hostname, String address, int port,int tProtocolType)
//	{
//		return noticeCallback.NoticeNewCon(index, hostname, address, port,ProtocolType.values()[tProtocolType]);
//	}
	public  int  NoticeNewCon(NewConData newConData)
	{
//		newConData.DstPort=12346;
		return noticeCallback.NoticeNewCon( newConData);
//		return noticeCallback.NoticeNewCon(index, hostname, address, port,ProtocolType.values()[tProtocolType]);
//		 return 0b1|0b10|0b10000;
	}
	//接收到新的数据  
	//!!!注意  在调用前需要先设置该函数签名
	public  int NoticeNewDatafromDst(int index, int type, byte[] data)  //char[4]	
	{
		return noticeCallback.NoticeNewDatafromDst(index, ProxyDataStatus.values()[type], data);
	}

	//接收到新的数据  
	//!!!注意  在调用前需要先设置该函数签名
	public  int NoticeNewDatafromSource(int index, int type, byte[] data)  //char[4]	
	{
		return noticeCallback.NoticeNewDatafromSource(index,  ProxyDataStatus.values()[type], data);
	}
	public  int NoticeClose(int index)  //char[4]	
	{
		return noticeCallback.NoticeClose(index);
	}

	public static int TestFromJava()
	{
		System.out.println("ok");
		return 1;
	}
	
	
	
	
/////////////////////////////////////////////////////////////////////////////////////
	//下面是java调用dll的接口
/////////////////////////////////////////////////////////////////////////////////////
	
	//设置jvm虚拟机指针
	public native int Init();
//	设置NoticeNewCon的签名  返回是否成功
	public native int InitNewCon(String NewConClassName,String NewConMethodName,String NewConSig);
//	设置InitNewDatafromDst的签名  返回是否成功
	public native int InitNewDatafromDst(String NewDataClassName,String NewDataMethodName,String NewDataSig);
//	设置InitNewDatafromSource的签名  返回是否成功
	public native int InitNewDatafromSource(String NewDataClassName,String NewDataMethodName,String NewDataSig);
//	设置NoticeClose的签名  返回是否成功
	public native int InitClose(String CloseClassName,String CloseMethodName,String CloseSig);
//	public native int Init(String NewConClassName,String NewConMethodName,String NewConSig,String NewDataClassName,String NewDataMethodName,String NewDataSig ,String CloseClassName,String CloseMethodName,String CloseSig);
	//设置NewConData 的签名
	public native int InitNewConData(String ClassName,String MethodName,String Sig);
	
//	暂停数据接收
	public native boolean SuspendRecv(int index,boolean tBSuspendRecvfromDst, boolean tBSuspendRecvfromSource );
	//重新设置当前套接字的拦截状态
	public native int SetInterceptStatus(int index,int Status);   
	public native  void SayHelloWorld();
	//public native void WaitForThread();
	
	//给发送至远端的缓冲区添加数据
	public native int AddSendtoDstBuffer(int index,byte[]data);
	
	//给发送至本地的缓冲区添加数据
	public native int AddSendtoSourceBuffer(int index,byte[]data);
	//开始监听
	public native int StartServer(String ip,int port);  
	//ip&0x0 ff ff ff ff代表为int无符号 0.0.0.0 ~ 255.255.255.255   
	//port&0x0 ff ff  代表short无符号  0~65535
	//后面可以尝试直接用 int short
	
	//停止监听
	public native int StopServer();		
	
	//暂停
	public native int SuspendServer();
	
	
	public static long ByteArrayToUint(byte[] b) {  
	    return   b[3] & 0xFF |  
	            (b[2] & 0xFF) << 8 |  
	            (b[1] & 0xFF) << 16 |  
	            (b[0] & 0xFF) << 24;  
	}   
	  
	
	public static int ByteArrayToInt(byte[] b) {  
	    return   b[3] & 0xFF |  
	            (b[2] & 0xFF) << 8 |  
	            (b[1] & 0xFF) << 16 |  
	            (b[0] & 0xFF) << 24;  
	}  
	  
	public static byte[] IntToByteArray(int a) {  
	    return new byte[] {  
	        (byte) ((a >> 24) & 0xFF),  
	        (byte) ((a >> 16) & 0xFF),     
	        (byte) ((a >> 8) & 0xFF),     
	        (byte) (a & 0xFF)  
	    };  
	}
	
	/*
				
	
				
	//发出数据
	public native int SendData(int index,char[]hashcode,byte[] data); 
	
	//修改设置
	public native int Set(int index,char[]hashcode,int type,byte[] data); 
	

//	添加一条拦截设置
//	  type: ip		--	0x10	ip过滤 and		0x11	ip过滤 or
//	 		port	--	0x20	port过滤and		0x21	port过滤or 
//	 		content	--	0x30	content过滤and	0x31	content过滤or
	 
	 
//	 		后续可以添加pid 或者exe名字

	public native int AddFilter(int type,String regex,boolean isIntercept);
	
	重置所有的过滤

	public native int ResetFilter(int type,String regex,boolean isIntercept);
	
	*/
	
}
