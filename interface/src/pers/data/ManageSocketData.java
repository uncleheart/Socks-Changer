package pers.data;

import java.util.*;

import pers.dll.infc.NewConData;

public class ManageSocketData {

	TreeMap<Integer, SocketInfo> mapIndexInfoEstablish;
	TreeMap<Integer, SocketInfo> mapIndexInfoClose;
	public ManageSocketData() {
		// TODO Auto-generated constructor stub
		mapIndexInfoEstablish = new TreeMap<Integer, SocketInfo>();
		mapIndexInfoClose = new TreeMap<Integer, SocketInfo>();
	}
	public SocketInfo GetSocketInfoEstablish(int tIndex){
		if (mapIndexInfoEstablish.containsKey(tIndex)) {
			return mapIndexInfoEstablish.get(tIndex);
		}
		return null;
	}
	public SocketInfo GetSocketInfoClose(int tIndex){
		if (mapIndexInfoClose.containsKey(tIndex)) {
			return mapIndexInfoClose.get(tIndex);
		}
		return null;
	}
	public void NoticeNewCon(NewConData newConData,InterceptStatus tInterceptStatus) {
		SocketInfo tInfo = new SocketInfo(newConData.DstHostname, newConData.DstHostname, newConData.DstPort, newConData.protocolType, tInterceptStatus);
		mapIndexInfoEstablish.put(newConData.Index, tInfo);
	}

	public int NoticeNewDatafromDst(int tIndex, ProxyDataStatus tProxyDataStatus, byte[] tData) // char[4]
	{
		if (mapIndexInfoEstablish.containsKey(tIndex)) {
			return mapIndexInfoEstablish.get(tIndex).InsertDatafromDst(tProxyDataStatus,tData);
		}
		return 0;
	}
	public int NoticeNewDatafromSource(int tIndex, ProxyDataStatus tProxyDataStatus, byte[] tData) // char[4]
	{
		if (mapIndexInfoEstablish.containsKey(tIndex)) {
			return mapIndexInfoEstablish.get(tIndex).InsertDatafromSource(tProxyDataStatus, tData);
		}
		return 0;
	}
	public void NoticeClose(int tIndex) {
		if (mapIndexInfoEstablish.containsKey(tIndex)) {
			SocketInfo tSocketInfo=mapIndexInfoEstablish.get(tIndex);
			mapIndexInfoClose.put(tIndex, tSocketInfo);
			mapIndexInfoEstablish.remove(tIndex);
		}
	}

}
