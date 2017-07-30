package pers.data;

import java.util.HashMap;

/*
 * 
 * 注意要添加一个  手动标记数据为  已发送   但是实际上并没有发送
 */

public class SocketInfo {
	public static  int FROM_REMOTE=1;
	public static  int FROM_LOCAL=2;
	
	public String Hostname;
	public ProtocolType protocolType;

	public String IPAddress;
	public int port;
	public HashMap<Integer, SocketByteData> byteDataDst; // 正的index代表当前数据，负的代表对应的备份数据
	public HashMap<Integer, SocketByteData> byteDataSource; // 正的index代表当前数据，负的代表对应的备份数据
	public InterceptStatus interceptStatus; //该套接字的拦截状态
	
	int NowSocketByteDstIndex;
	int NowSocketByteSourceIndex;
	public SocketInfo() {
		byteDataDst = new HashMap<Integer, SocketByteData>();
		byteDataSource = new HashMap<Integer, SocketByteData>();
	}

	public SocketInfo(String hostname,String ipAddress, int tport , ProtocolType tProtocolType,InterceptStatus tInterceptStatus) {
		Hostname = hostname;
		IPAddress = ipAddress;
		port = tport;
		protocolType = tProtocolType;
		byteDataDst = new HashMap<Integer, SocketByteData>();
		byteDataSource = new HashMap<Integer, SocketByteData>();
		NowSocketByteDstIndex = 0;
		NowSocketByteSourceIndex = 0;
		interceptStatus=tInterceptStatus;

	}

	public void ModifyDatafromDst(int tIndex, byte[] tData) {
		if (byteDataDst.containsKey(tIndex)) {
			if (!byteDataDst.containsKey(-tIndex)) {
				String tmp=new String(byteDataDst.get(tIndex).data);
				byteDataDst.put(-tIndex, (SocketByteData)byteDataDst.get(tIndex).clone());
			}
			byteDataDst.get(tIndex).data=tData;
		}
	}

	public void ModifyDatafromSource(int tIndex, byte[] tData) {
		if (byteDataSource.containsKey(tIndex)) {
			if (!byteDataSource.containsKey(-tIndex)) {
				byteDataSource.put(-tIndex, (SocketByteData)byteDataSource.get(tIndex).clone());
			}
//			byteDataSource.get(tIndex).ModifyData(tData);
			byteDataSource.get(tIndex).data=tData;
		}
	}
	public int MergeDatafromDst(byte[] tData,byte[] tDataOrigin)
	{
		NowSocketByteDstIndex += 1;
		SocketByteData socketByteData=new SocketByteData(ProxyDataStatus.NEW_DATA,tData);
		if (tDataOrigin==null) {
			byteDataDst.put(NowSocketByteDstIndex, socketByteData);
		}
		else
		{
			byteDataDst.put(NowSocketByteDstIndex, socketByteData);
			 socketByteData=new SocketByteData(ProxyDataStatus.NEW_DATA,tDataOrigin);
			 byteDataDst.put(-NowSocketByteDstIndex, socketByteData);
		}
		return NowSocketByteDstIndex;
	}
	public int MergeDatafromSource(byte[] tData,byte[] tDataOrigin)
	{
		NowSocketByteSourceIndex += 1;
		SocketByteData socketByteData=new SocketByteData(ProxyDataStatus.NEW_DATA,tData);
		if (tDataOrigin==null) {
			byteDataSource.put(NowSocketByteSourceIndex, socketByteData);
		}
		else
		{
			byteDataSource.put(NowSocketByteSourceIndex, socketByteData);
			 socketByteData=new SocketByteData(ProxyDataStatus.NEW_DATA,tDataOrigin);
			 byteDataSource.put(-NowSocketByteSourceIndex, socketByteData);
		}
		return NowSocketByteSourceIndex;
	}
	public int InsertDatafromDst(ProxyDataStatus tProxyDataStatus,byte[] tData) {
		NowSocketByteDstIndex += 1;
		SocketByteData socketByteData=new SocketByteData(tProxyDataStatus,tData);
		byteDataDst.put(NowSocketByteDstIndex, socketByteData);
		return NowSocketByteDstIndex;
	}
	public int InsertDatafromSource(ProxyDataStatus tProxyDataStatus,byte[] tData) {
		NowSocketByteSourceIndex += 1;
		SocketByteData socketByteData=new SocketByteData(tProxyDataStatus,tData);
		byteDataSource.put(NowSocketByteSourceIndex, socketByteData);
		return NowSocketByteSourceIndex;
	}
	public void 	ModifyStatusFromDst(int tIndex,ProxyDataStatus tProxyDataStatus)
	{
		if (byteDataDst.containsKey(tIndex)) {
			byteDataDst.get(tIndex).proxyDataStatus=tProxyDataStatus;
		}
	}

	public void 	ModifyStatusFromSource(int tIndex,ProxyDataStatus tProxyDataStatus)
	{
		if (byteDataSource.containsKey(tIndex)) {
			byteDataSource.get(tIndex).proxyDataStatus=tProxyDataStatus;
		}
	}
	public ProxyDataStatus GetStatusFromDst(int tIndex)
	{
		if (byteDataDst.containsKey(tIndex)) {
			return byteDataDst.get(tIndex).proxyDataStatus;
		}
		return null;
	}
	public ProxyDataStatus GetStatusFromSource(int tIndex)
	{
		if (byteDataSource.containsKey(tIndex)) {
			return byteDataSource.get(tIndex).proxyDataStatus;
		}
		return null;
	}
	public void DelFromDst(int tIndex){
		if (byteDataDst.containsKey(tIndex)) {
			byteDataDst.remove(tIndex);
		}
	}
	public void DelFromSource(int tIndex){
		if (byteDataSource.containsKey(tIndex)) {
			 byteDataSource.remove(tIndex);
		}
	}
}
