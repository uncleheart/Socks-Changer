package pers.dll.infc;

import pers.data.*;

public interface NoticeCallback {
	// 接收到新的数据
	// !!!注意 在调用前需要先设置该函数签名
	int NoticeNewDatafromDst(int index, ProxyDataStatus type, byte[] data); // char[4]
	
	// 接收到新的数据
	// !!!注意 在调用前需要先设置该函数签名
	int NoticeNewDatafromSource(int index, ProxyDataStatus type, byte[] data); // char[4]

	// 接收到新的请求，开始建立连接
	// !!!注意 在调用前需要先设置该函数签名
	int NoticeNewCon(NewConData newConData);

	// 该连接已关闭
	// !!!注意 在调用前需要先设置该函数签名
	int NoticeClose(int index);

}
