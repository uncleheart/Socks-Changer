#pragma once

#include<stdint.h>

#ifndef _DECLARE_H
#define _DECLARE_H

#define IndexType uint16_t
#define ReturnType uint8_t
//#define FuncType uint8_t

enum EnumReturnType
{
	ERROR_SETTING,
	SUCCESS = 1,
	FAIL,
	NONE ,
};


//ReturnType
enum JavaReturnType
{
	INTERCEPT_CLIENT_REQUESTS=0b1,		//1为拦截客户端请求，0为不拦截
	INTERCEPT_SERVER_RESPONSES=0b10,	//1为拦截服务器响应，0为不拦截
	CHANGE_IP_ADDRESS=0b100,
	CHANGE_HOST_NAME=0b1000,
	CHANGE_PORT=0b10000,
	NOT_COPY = 0b10000000,				//1为产生副本，0为不产生，那么数据就不会给java发，仅用于加速时使用
};

//ProxyDataStatus   数据的额状态
enum ProxyDataStatus
{
	//DATA_FROM_LOCAL_ALREADY_SENT=1,		//马上发送的来自本地的数据，马上就发送出去了，不能拦截
	//DATA_FROM_REMOTE_ALREADY_SENT=2,		//马上发送的来自远端的数据，马上就发送出去了，不能拦截
	//DATA_FROM_LOCAL_TO_SEND=3,			//刚收到的来自本地的数据，还没有发送出去
	//DATA_FROM_REMOTE_TO_SEND=4,			//刚收到的来自远端的数据，还没有发送出去

	SENT,			//已经发送出去了
	NOT_SEND,		// 还没有发送出去

};
enum {
	SOCKS_VERSION_4 = 4,
	SOCKS_VERSION_5 = 5
};
enum {
	SOCKS5_AUTH_NONE = 0x00,
	SOCKS5_AUTH = 0x02,
	SOCKS5_AUTH_UNACCEPTABLE = 0xFF
};
enum {
	SOCKS_CMD_CONNECT = 0x01,
	SOCKS_CMD_BIND = 0x02,
	SOCKS5_CMD_UDP = 0x03
};
enum {
	SOCKS5_ATYP_IPV4 = 0x01,
	SOCKS5_ATYP_DOMAINNAME = 0x03,
	SOCKS5_ATYP_IPV6 = 0x04
};
enum {
	SOCKS5_SUCCEEDED = 0x00,
	SOCKS5_GENERAL_SOCKS_SERVER_FAILURE,
	SOCKS5_CONNECTION_NOT_ALLOWED_BY_RULESET,
	SOCKS5_NETWORK_UNREACHABLE,
	SOCKS5_CONNECTION_REFUSED,
	SOCKS5_TTL_EXPIRED,
	SOCKS5_COMMAND_NOT_SUPPORTED,
	SOCKS5_ADDRESS_TYPE_NOT_SUPPORTED,
	SOCKS5_UNASSIGNED
};
enum {
	SOCKS4_REQUEST_GRANTED = 90,
	SOCKS4_REQUEST_REJECTED_OR_FAILED,
	SOCKS4_CANNOT_CONNECT_TARGET_SERVER,
	SOCKS4_REQUEST_REJECTED_USER_NO_ALLOW,
};

enum {
	MAX_RECV_BUFFER_SIZE = 768	// 最大udp接收缓冲大小.
};
//enum {
//	SEND_TO_REMOTE,				//准备发给远端			
//	SEND_TO_LOCAL,				//准备发给本地		
//	HANDLE_SENT_TO_REMOTE,		//处理已经将数据发至远端		
//	HANDLE_SENT_TO_LOCAL,		//处理已经将数据发至本地
//
//	RECV_FROM_REMOTE,			//准备接受远端数据		
//	RECV_FROM_LOCAL,			//准备接受本地数据		
//	HANDLE_RECV_FROM_REMOTE,	//处理从远端收到的数据
//	HANDLE_RECV_FROM_LOCAL,		//处理从本地收到的数据
//
//
//	HANDLE_ADD_BUFFER_TO_LOCAL,		//添加到发送至本地的消息队列中
//	HANDLE_ADD_BUFFER_TO_REMOTE,	//添加到发送至远端的消息队列中
//	FIRST_RECV_FROM_LOCAL,							//刚开始第一次接受本地数据
//	FIRST_RECV_FROM_REMOTE,							//刚开始第一次接受远端数据
//	NEW_BUFFER_TO_REMOTE,		//需要将队列里的数据发给远端
//	NEW_BUFFER_TO_LOCAL,		//需要将队列里的数据发给本地
//
//};


//typedef struct
//{
//	uint32_t len;		//长度
//	IndexType socketIndex;	//套接字
//	uint8_t type;		//类型
//	void * pData;			//数据指针
//} MessageHeader;



#ifdef _DEBUG  
#define Dbg(code)  code
#else
#define Dbg(code) 
#endif 


#endif