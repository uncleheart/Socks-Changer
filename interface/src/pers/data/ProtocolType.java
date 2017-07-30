package pers.data;


//协议类型
public enum ProtocolType {
	TCP(1),UDP(2);
	private int protocolType = 0;
	 ProtocolType(int tProtocolType)
	{
		protocolType=tProtocolType;
	}
	public int GetStatus() {
		return protocolType;
	}
	public String GetString(){
		switch (protocolType) {
		case 1:
			return "TCP";
		case 2:
			return "UDP";
		default:
			return "error";
		}
	}
}
