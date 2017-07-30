package pers.data;

//发送状态
public enum ProxyDataStatus {
	SENT(0), // 已经发送出去了
	NOT_SEND(1), // 还没有发送出去
	ABANDON(2),// 放弃发送的数据，即肯定不会发送的数据
	NEW_DATA(3);  //新数据
	private int status = 0;

	private ProxyDataStatus(int t) {
		status = t;
	}

//	public int GetStatus() {
//		return status;
//	}
    @Override
	public String toString(){
		switch (status) {
		case 0:
			return "SENT";
		case 1:
		return "NOT SEND";
		case 2:
			return "ABANDON";
		case 3:
			return "NEW DATA";
		default:
			return "error";
		}
	}
}
