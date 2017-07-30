package pers.ui.proxy.options.enumsum;

public enum MatchRelationship {
	Match(1),NotMatch(2);
	private int MatchRelationshipType = 0;
//	final static public String[] StrArray={"Match","Not Match"};
	MatchRelationship(int tMatchRelationshipType)
	{
		MatchRelationshipType=tMatchRelationshipType;
	}
	public int GetStatus() {
		return MatchRelationshipType;
	}

    @Override
    public String toString() {
    	switch (MatchRelationshipType) {
		case 1:
			return "Match";
		case 2:
			return "Not match";
		default:
			return "error";
		}
    }
}
