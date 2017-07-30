package pers.data;

public enum InterceptStatus {
	INTERCEPT_NONE(0b0),			//不进行拦截
	INTERCEPT_SEND(0b1), // 拦截客户端的请求
	
	INTERCEPT_RECV(0b10), // 拦截server的响应
	INTERCEPT_ALL(0b11);
	private int status = 0;

	private InterceptStatus(int t) {
		status = t;
	}
	public int GetStatus() {
		return status;
	}
	@Override
	public String toString() {
		switch (status) {
		case 0b0:
			return "SENT";
		case 0b10:
			return "NOT SEND";
		default:
			return "error";
		}
	}
}
