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
	INTERCEPT_CLIENT_REQUESTS=0b1,		//1Ϊ���ؿͻ�������0Ϊ������
	INTERCEPT_SERVER_RESPONSES=0b10,	//1Ϊ���ط�������Ӧ��0Ϊ������
	CHANGE_IP_ADDRESS=0b100,
	CHANGE_HOST_NAME=0b1000,
	CHANGE_PORT=0b10000,
	NOT_COPY = 0b10000000,				//1Ϊ����������0Ϊ����������ô���ݾͲ����java���������ڼ���ʱʹ��
};

//ProxyDataStatus   ���ݵĶ�״̬
enum ProxyDataStatus
{
	//DATA_FROM_LOCAL_ALREADY_SENT=1,		//���Ϸ��͵����Ա��ص����ݣ����Ͼͷ��ͳ�ȥ�ˣ���������
	//DATA_FROM_REMOTE_ALREADY_SENT=2,		//���Ϸ��͵�����Զ�˵����ݣ����Ͼͷ��ͳ�ȥ�ˣ���������
	//DATA_FROM_LOCAL_TO_SEND=3,			//���յ������Ա��ص����ݣ���û�з��ͳ�ȥ
	//DATA_FROM_REMOTE_TO_SEND=4,			//���յ�������Զ�˵����ݣ���û�з��ͳ�ȥ

	SENT,			//�Ѿ����ͳ�ȥ��
	NOT_SEND,		// ��û�з��ͳ�ȥ

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
	MAX_RECV_BUFFER_SIZE = 768	// ���udp���ջ����С.
};
//enum {
//	SEND_TO_REMOTE,				//׼������Զ��			
//	SEND_TO_LOCAL,				//׼����������		
//	HANDLE_SENT_TO_REMOTE,		//�����Ѿ������ݷ���Զ��		
//	HANDLE_SENT_TO_LOCAL,		//�����Ѿ������ݷ�������
//
//	RECV_FROM_REMOTE,			//׼������Զ������		
//	RECV_FROM_LOCAL,			//׼�����ܱ�������		
//	HANDLE_RECV_FROM_REMOTE,	//�����Զ���յ�������
//	HANDLE_RECV_FROM_LOCAL,		//����ӱ����յ�������
//
//
//	HANDLE_ADD_BUFFER_TO_LOCAL,		//��ӵ����������ص���Ϣ������
//	HANDLE_ADD_BUFFER_TO_REMOTE,	//��ӵ�������Զ�˵���Ϣ������
//	FIRST_RECV_FROM_LOCAL,							//�տ�ʼ��һ�ν��ܱ�������
//	FIRST_RECV_FROM_REMOTE,							//�տ�ʼ��һ�ν���Զ������
//	NEW_BUFFER_TO_REMOTE,		//��Ҫ������������ݷ���Զ��
//	NEW_BUFFER_TO_LOCAL,		//��Ҫ������������ݷ�������
//
//};


//typedef struct
//{
//	uint32_t len;		//����
//	IndexType socketIndex;	//�׽���
//	uint8_t type;		//����
//	void * pData;			//����ָ��
//} MessageHeader;



#ifdef _DEBUG  
#define Dbg(code)  code
#else
#define Dbg(code) 
#endif 


#endif