package pers.dll.infc;

import pers.data.ProtocolType;

public class NewConData {

	public final int Index;
	public final ProtocolType protocolType;

	public final String SourceHostname;
	public final String SourceIPAddress;
	public final int SourcePort;

	public String DstHostname;
	public String DstIPAddress;
	public int DstPort;

	public NewConData(int tIndex, int tProtocolType, String tSourceHostname, String tSourceIPAddress, int tSourcePort,
			String tDstHostname, String tDstIPAddress, int tDstPort) {
		Index = tIndex;
		protocolType = ProtocolType.values()[tProtocolType];

		SourceHostname = tSourceHostname;
		SourceIPAddress = tSourceIPAddress;
		SourcePort = tSourcePort;

		DstHostname = tDstHostname;
		DstIPAddress = tDstIPAddress;
		DstPort = tDstPort;
	}

}
