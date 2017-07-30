package pers.ui.proxy.options.enumsum;

public enum BooleanOperator {
	AND(1),OR(2);
	private int BooleanOperatorType = 0;
//	final static public String[] StrArray={"AND","OR"};
	BooleanOperator(int tBooleanOperatorType)
	{
		BooleanOperatorType=tBooleanOperatorType;
	}
	public int GetStatus() {
		return BooleanOperatorType;
	}
	
    @Override
	public String toString(){
		switch (BooleanOperatorType) {
		case 1:
			return "And";
		case 2:
			return "Or";
		default:
			return "error";
		}
	}
}
