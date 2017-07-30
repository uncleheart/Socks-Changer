package pers.ui.proxy.options.enumsum;

public enum MatchType {
	SourceIP(1),SouceHostname(2),SourcePort(3),DstIP(4),DstHostname(5),DstPort(6);
	private int MatchType = 0;
//	final static public String[] StrArray={"SourceIP","SouceDomainName","SourcePort","DstIP","DstDomainName","DstPort"};
	MatchType(int tMatchType)
	{
		MatchType=tMatchType;
	}
	public int GetStatus() {
		return MatchType;
	}
    @Override
	public String toString(){
		switch (MatchType) {
		case 1:
			return "Source IP";
		case 2:
			return "Souce domainname";
		case 3:
			return "Source port";
		case 4:
			return "Destination IP";
		case 5:
			return "Destination domainName";
		case 6:
			return "Destination port";
		default:
			return "error";
		}
	}
}
