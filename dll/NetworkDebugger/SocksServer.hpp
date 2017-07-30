#ifndef SOCKS_SERVER_HPP
#define SOCKS_SERVER_HPP

#if defined(_MSC_VER) && (_MSC_VER >= 1200)
# pragma once
#endif // defined(_MSC_VER) && (_MSC_VER >= 1200)

#include <istream>
#include <deque>
#include <string>
#include <cstring> // for std::memcpy
#include <list>			
#include <boost/lockfree/spsc_queue.hpp>

#include <boost/thread.hpp>
#include <boost/asio.hpp>
#include <boost/bind.hpp>
#include <boost/noncopyable.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/enable_shared_from_this.hpp>
#include <boost/array.hpp>
#include <boost/date_time.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>
#include <boost/lockfree/spsc_queue.hpp>
#include "io.hpp"
#include "Declare.h"
#include"ManageIndexNumber.hpp"

//#define   IndexType int16_t
//#define  ReturnType int8_t

namespace socks {

	using boost::asio::ip::tcp;
	using boost::asio::ip::udp;

	class socks_session
		: public boost::enable_shared_from_this<socks_session>
	{
	public:
		explicit socks_session(
			boost::asio::io_service &io
			)
			: m_io_service(io)
			, m_local_socket(io)
			, m_remote_socket(io)
			, m_udp_socket(io)
			, m_resolver(io)
			, m_version(-1)
			, m_method(-1)
			, m_command(-1)
			, m_atyp(-1)
			, m_port(0)
			, m_verify_passed(false)
			, m_udp_timer(io)
			, SendDstBuffer(128)
			, SendSourceBuffer(128)
			, IsCopy(true)
			, InterceptClientRequests(true)
			, InterceptServerrResponses(true)
			, index(0)
			, BSendtoDst(false)
			, BSendtoSource(false)
			, BRecvfromDst(false)
			, BRecvfromSource(false)
			,BSuspendRecvfromDst(false)
			,BSuspendRecvfromSource(false)
		{

		}
		~socks_session()
		{
			CloseSocket();
		}

	public:
		void SetNoticeNewCon(
			boost::function<ReturnType(
				_In_ IndexType index,
				_In_ uint8_t protocolType,
				_In_ std::string SourceHostname,
				_In_ std::string SourceIPAddress,
				_In_ uint16_t SourcePort,
				_In_  _Out_ std::string &DstHostname,
				_In_  _Out_ std::string &DstIPAddress,
				_In_  _Out_ uint16_t &DstPort
				)> 
			pNoticeNewCon

			)
		{
			NoticeNewCon = pNoticeNewCon;
		}
		void SuspendRecv(bool tBSuspendRecvfromDst, bool tBSuspendRecvfromSource)
		{
			BSuspendRecvfromDst = tBSuspendRecvfromDst;
			BSuspendRecvfromSource = tBSuspendRecvfromSource;
		}
		void SetDelinSessionSum(boost::function<ReturnType(IndexType index)> pDelinSessionSum)
		{
			DelinSessionSum = pDelinSessionSum;
		}
		void SetNoticeNewDatafromDst(boost::function<ReturnType(IndexType index, ProxyDataStatus type, std::string &data)> pNoticeNewDatafromDst)
		{
			NoticeNewDatafromDst = pNoticeNewDatafromDst;
		}
		void SetNoticeNewDatafromSource(boost::function<ReturnType(IndexType index, ProxyDataStatus type, std::string &data)> pNoticeNewDatafromSource)
		{
			NoticeNewDatafromSource = pNoticeNewDatafromSource;
		}
		void SetNoticeClose(boost::function<ReturnType(IndexType index)> pNoticeClose)
		{
			NoticeClose = pNoticeClose;
		}
		void SetIndex(IndexType tIndex)
		{
			index = tIndex;
		}
		ReturnType GetSocketStatus()
		{
			ReturnType res=0;
			res |= m_remote_socket.is_open() ? 0b01 : 0;
			res |= m_local_socket.is_open() ? 0b10 : 0;
			return res;
		}
		int SetInterceptStatus(int status)
		{
			InterceptServerrResponses = (bool)(status&INTERCEPT_SERVER_RESPONSES);
			InterceptClientRequests = (bool)(status&INTERCEPT_CLIENT_REQUESTS);
			return 0;
		}

		void start()
		{
			// read
			//  +----+----------+----------+
			//  |VER | NMETHODS | METHODS  |
			//  +----+----------+----------+
			//  | 1  |    1     | 1 to 255 |
			//  +----+----------+----------+
			//  [               ]
			// or
			//  +----+----+----+----+----+----+----+----+----+----+....+----+
			//  | VN | CD | DSTPORT |      DSTIP        | USERID       |NULL|
			//  +----+----+----+----+----+----+----+----+----+----+....+----+
			//    1    1      2        4                  variable       1
			//  [         ]
			// 读取[]里的部分.
			boost::asio::async_read(m_local_socket, boost::asio::buffer(m_local_buffer, 2),
				boost::asio::transfer_exactly(2),
				boost::bind(&socks_session::socks_handle_connect_1, shared_from_this(),
					boost::asio::placeholders::error,
					boost::asio::placeholders::bytes_transferred
					)
				);
		}

		tcp::socket& socket()
		{
			return m_local_socket;
		}
		void CloseSocket()
		{
			if (index == 0)//如果index为0 说明还是没有完全初始化的，不用清理
			{
				return;
			}

			DelinSessionSum(index);
			NoticeClose(index);
			index = 0;

			boost::system::error_code ignored_ec;
			// 远程和本地链接都将关闭.
			if (m_local_socket.is_open())
			{
				m_local_socket.shutdown(
					boost::asio::ip::tcp::socket::shutdown_both, ignored_ec);
				m_local_socket.close(ignored_ec);
			}
			if (m_remote_socket.is_open())
			{
				m_remote_socket.shutdown(
					boost::asio::ip::tcp::socket::shutdown_both, ignored_ec);
				m_remote_socket.close(ignored_ec);
			}
			m_udp_timer.cancel(ignored_ec);
			if (m_udp_socket.is_open())
			{
				m_udp_socket.close(ignored_ec);
			}
		}

		void addSendSourceBuffer(std::string& buffer)		//添加一个发送至本地的缓存
		{
			//以后需要处理当缓冲不够的时候的解决办法
			SendSourceBuffer.push(buffer);
			if (!BSendtoSource)
			{
				SendtoSource();
			}
		}

		void addSendDstBuffer(std::string & buffer)		//添加一个发送的缓存
		{
			//以后需要处理当缓冲不够的时候的解决办法
			SendDstBuffer.push(buffer);
			if (!BSendtoDst)
			{
				SendtoDst();
			}
		}

	protected:
		void socks_handle_connect_1(const boost::system::error_code& error, int bytes_transferred)
		{
			if (!error)
			{
				char *p = m_local_buffer.data();
				m_version = read_int8(p);
				if (m_version == SOCKS_VERSION_5)	// sock5协议.
				{
					int nmethods = read_int8(p);	// 读取客户端支持的代理方式列表.
					if (nmethods <= 0 || nmethods > 255)
					{
						std::cout << "unsupport any method!\n";
						return;
					}

					//  +----+----------+----------+
					//  |VER | NMETHODS | METHODS  |
					//  +----+----------+----------+
					//  | 1  |    1     | 1 to 255 |
					//  +----+----------+----------+
					//                  [          ]
					boost::asio::async_read(m_local_socket, boost::asio::buffer(m_local_buffer, nmethods),
						boost::asio::transfer_exactly(nmethods),
						boost::bind(&socks_session::socks_handle_connect_2, shared_from_this(),
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);
				}
				else if (m_version == SOCKS_VERSION_4)	// socks4协议.
				{
					//  +----+----+----+----+----+----+----+----+----+----+....+----+
					//  | VN | CD | DSTPORT |      DSTIP        | USERID       |NULL|
					//  +----+----+----+----+----+----+----+----+----+----+....+----+
					//  | 1  | 1  |    2    |         4         | variable     | 1  |
					//  +----+----+----+----+----+----+----+----+----+----+....+----+
					//            [                             ]

					m_command = read_int8(p);

					boost::asio::async_read(m_local_socket, boost::asio::buffer(m_local_buffer, 6),
						boost::asio::transfer_exactly(6),
						boost::bind(&socks_session::socks_handle_connect_2, shared_from_this(),
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);
				}
				else
				{
					// std::cout << "error unknow protocol.\n";
				}
			}
		}

		void socks_handle_connect_2(const boost::system::error_code& error, int bytes_transferred)
		{
			if (!error)
			{
				if (m_version == SOCKS_VERSION_5)
				{
					// 循环读取客户端支持的代理方式.
					char *p = m_local_buffer.data();
					m_method = SOCKS5_AUTH_UNACCEPTABLE;
					while (bytes_transferred != 0)
					{
						int m = read_int8(p);
						if (m == SOCKS5_AUTH_NONE || m == SOCKS5_AUTH)
							m_method = m;
						bytes_transferred--;
					}

					// 回复客户端, 选择的代理方式.
					p = m_local_buffer.data();
					write_int8(m_version, p);
					write_int8(m_method, p);

					//  +----+--------+
					//  |VER | METHOD |
					//  +----+--------+
					//  | 1  |   1    |
					//  +----+--------+
					//  [             ]
					boost::asio::async_write(m_local_socket, boost::asio::buffer(m_local_buffer, 2),
						boost::asio::transfer_exactly(2),
						boost::bind(&socks_session::socks_handle_send_version, shared_from_this(),
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);
				}

				if (m_version == SOCKS_VERSION_4)
				{
					char *p = m_local_buffer.data();
					m_address.port(read_uint16(p));
					m_address.address(boost::asio::ip::address_v4(read_uint32(p)));

					//  +----+----+----+----+----+----+----+----+----+----+....+----+
					//  | VN | CD | DSTPORT |      DSTIP        | USERID       |NULL|
					//  +----+----+----+----+----+----+----+----+----+----+....+----+
					//  | 1  | 1  |    2    |         4         | variable     | 1  |
					//  +----+----+----+----+----+----+----+----+----+----+....+----+
					//                                          [                   ]
					boost::asio::async_read_until(m_local_socket, m_streambuf, '\0',
						boost::bind(&socks_session::socks_handle_negotiation_2, shared_from_this(),
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);
				}
			}
		}

		void socks_handle_send_version(const boost::system::error_code& error, int bytes_transferred)
		{
			if (!error)
			{
				if (m_method == SOCKS5_AUTH && !m_verify_passed)			// 认证模式.
				{
					//  +----+------+----------+------+----------+
					//  |VER | ULEN |  UNAME   | PLEN |  PASSWD  |
					//  +----+------+----------+------+----------+
					//  | 1  |  1   | 1 to 255 |  1   | 1 to 255 |
					//  +----+------+----------+------+----------+
					//  [           ]
					boost::asio::async_read(m_local_socket, boost::asio::buffer(m_local_buffer, 2),
						boost::asio::transfer_exactly(2),
						boost::bind(&socks_session::socks_handle_negotiation_1, shared_from_this(),
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);
				}
				else if (m_method == SOCKS5_AUTH_NONE || m_verify_passed)	// 非认证模式, 或认证已经通过, 接收socks客户端Requests.
				{
					//  +----+-----+-------+------+----------+----------+
					//  |VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
					//  +----+-----+-------+------+----------+----------+
					//  | 1  |  1  | X'00' |  1   | Variable |    2     |
					//  +----+-----+-------+------+----------+----------+
					//  [                          ]
					boost::asio::async_read(m_local_socket, boost::asio::buffer(m_local_buffer, 5),
						boost::asio::transfer_exactly(5),
						boost::bind(&socks_session::socks_handle_requests_1, shared_from_this(),
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);
				}
			}
		}

		void socks_handle_negotiation_1(const boost::system::error_code& error, int bytes_transferred)
		{
			if (!error)
			{
				char *p = m_local_buffer.data();
				int auth_version = read_int8(p);
				if (auth_version != 1)
				{
					std::cout << "unsupport socks5 protocol\n";
					return;
				}
				int name_length = read_int8(p);
				if (name_length <= 0 || name_length > 255)
				{
					std::cout << "error unknow protocol.\n";
					return;
				}
				name_length += 1;
				//  +----+------+----------+------+----------+
				//  |VER | ULEN |  UNAME   | PLEN |  PASSWD  |
				//  +----+------+----------+------+----------+
				//  | 1  |  1   | 1 to 255 |  1   | 1 to 255 |
				//  +----+------+----------+------+----------+
				//              [                 ]
				boost::asio::async_read(m_local_socket, boost::asio::buffer(m_local_buffer, name_length),
					boost::asio::transfer_exactly(name_length),
					boost::bind(&socks_session::socks_handle_negotiation_2, shared_from_this(),
						boost::asio::placeholders::error,
						boost::asio::placeholders::bytes_transferred
						)
					);
			}
		}

		void socks_handle_negotiation_2(const boost::system::error_code& error, int bytes_transferred)
		{
			if (!error)
			{
				if (m_version == SOCKS_VERSION_5)
				{
					char *p = m_local_buffer.data();
					for (int i = 0; i < bytes_transferred - 1; i++)
						m_uname.push_back(read_int8(p));
					int passwd_len = read_int8(p);
					if (passwd_len <= 0 || passwd_len > 255)
					{
						std::cout << "error unknow protocol.\n";
						return;
					}
					//  +----+------+----------+------+----------+
					//  |VER | ULEN |  UNAME   | PLEN |  PASSWD  |
					//  +----+------+----------+------+----------+
					//  | 1  |  1   | 1 to 255 |  1   | 1 to 255 |
					//  +----+------+----------+------+----------+
					//                                [          ]
					boost::asio::async_read(m_local_socket, boost::asio::buffer(m_local_buffer, passwd_len),
						boost::asio::transfer_exactly(passwd_len),
						boost::bind(&socks_session::socks_handle_negotiation_3, shared_from_this(),
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);
				}

				if (m_version == SOCKS_VERSION_4)
				{
					std::string userid;

					userid.resize(bytes_transferred);
					m_streambuf.sgetn(&userid[0], bytes_transferred);

					// TODO: SOCKS4认证用户.
					m_verify_passed = true;

					// 发起连接.
					if (m_command == SOCKS_CMD_CONNECT)
					{
						tcp::resolver::iterator endpoint_iterator;
						m_remote_socket.async_connect(m_address,
							boost::bind(&socks_session::socks_handle_connect_3,
								shared_from_this(), boost::asio::placeholders::error,
								endpoint_iterator
								)
							);
						return;
					}

					if (m_command == SOCKS_CMD_BIND)
					{
						// TODO: 实现绑定请求.
					}
				}
			}
		}

		void socks_handle_negotiation_3(const boost::system::error_code& error, int bytes_transferred)
		{
			if (!error)
			{
				char *p = m_local_buffer.data();
				for (int i = 0; i < bytes_transferred; i++)
					m_passwd.push_back(read_int8(p));

				// TODO: SOCKS5验证用户和密码.
				m_verify_passed = true;

				p = m_local_buffer.data();
				write_int8(0x01, p);		// version 只能是1.
				write_int8(0x00, p);		// 认证通过返回0x00, 其它值为失败.

											// 返回认证状态.
											//  +----+--------+
											//  |VER | STATUS |
											//  +----+--------+
											//  | 1  |   1    |
											//  +----+--------+
				boost::asio::async_write(m_local_socket, boost::asio::buffer(m_local_buffer, 2),
					boost::asio::transfer_exactly(2),
					boost::bind(&socks_session::socks_handle_send_version, shared_from_this(),
						boost::asio::placeholders::error,
						boost::asio::placeholders::bytes_transferred
						)
					);
			}
		}

		void socks_handle_requests_1(const boost::system::error_code& error, int bytes_transferred)
		{
			if (!error)
			{
				char *p = m_local_buffer.data();
				if (read_int8(p) != SOCKS_VERSION_5)
				{
					std::cout << "error unknow protocol.\n";
					return;
				}

				m_command = read_int8(p);		// CONNECT/BIND/UDP
				read_int8(p);				// reserved.
				m_atyp = read_int8(p);			// atyp.

												//  +----+-----+-------+------+----------+----------+
												//  |VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
												//  +----+-----+-------+------+----------+----------+
												//  | 1  |  1  | X'00' |  1   | Variable |    2     |
												//  +----+-----+-------+------+----------+----------+
												//                              [                   ]
				int length = 0;
				int prefix = 1;

				// 保存第一个字节.
				m_local_buffer[0] = m_local_buffer[4];

				if (m_atyp == SOCKS5_ATYP_IPV4)
					length = 5;
				else if (m_atyp == SOCKS5_ATYP_DOMAINNAME)
				{
					length = read_int8(p) + 2;
					prefix = 0;
				}
				else if (m_atyp == SOCKS5_ATYP_IPV6)
					length = 17;

				boost::asio::async_read(m_local_socket, boost::asio::buffer(m_local_buffer.begin() + prefix, length),
					boost::asio::transfer_exactly(length),
					boost::bind(&socks_session::socks_handle_requests_2, shared_from_this(),
						boost::asio::placeholders::error,
						boost::asio::placeholders::bytes_transferred
						)
					);
			}
		}

		void socks_handle_requests_2(const boost::system::error_code& error, int bytes_transferred)
		{
			if (!error)
			{
				if (m_version == SOCKS_VERSION_5)
				{
					char *p = m_local_buffer.data();

					if (m_atyp == SOCKS5_ATYP_IPV4)
					{
						bytes_transferred += 1;	// 加上首个字节.
						m_address.address(boost::asio::ip::address_v4(read_uint32(p)));
						m_address.port(read_uint16(p));
					}
					else if (m_atyp == SOCKS5_ATYP_DOMAINNAME)
					{
						for (int i = 0; i < bytes_transferred - 2; i++)
							m_domain.push_back(read_int8(p));
						m_port = read_uint16(p);
					}
					else if (m_atyp == SOCKS5_ATYP_IPV6)
					{
						bytes_transferred += 1;	// 加上首个字节.
						boost::asio::ip::address_v6::bytes_type addr;
						for (boost::asio::ip::address_v6::bytes_type::iterator i = addr.begin();
						i != addr.end(); ++i)
						{
							*i = read_int8(p);
						}

						m_address.address(boost::asio::ip::address_v6(addr));
						m_address.port(read_uint16(p));
					}

					// 发起连接.
					if (m_command == SOCKS_CMD_CONNECT)
					{
						if (m_atyp == SOCKS5_ATYP_IPV4 || m_atyp == SOCKS5_ATYP_IPV6)
						{
							m_resolver.async_resolve(m_address,
								boost::bind(&socks_session::socks_handle_resolve,
									shared_from_this(), boost::asio::placeholders::error,
									boost::asio::placeholders::iterator
									)
								);
							return;
						}
						if (m_atyp == SOCKS5_ATYP_DOMAINNAME)
						{
							std::ostringstream port_string;
							port_string << m_port;
							tcp::resolver::query query(m_domain, port_string.str());

							m_resolver.async_resolve(query,
								boost::bind(&socks_session::socks_handle_resolve,
									shared_from_this(), boost::asio::placeholders::error,
									boost::asio::placeholders::iterator
									)
								);
							return;
						}
					}
					else if (m_command == SOCKS5_CMD_UDP || m_command == SOCKS_CMD_BIND || true)
					{
						// 实现UDP ASSOCIATE.
						if (m_command == SOCKS5_CMD_UDP)
						{
							if (m_atyp == SOCKS5_ATYP_IPV4 || m_atyp == SOCKS5_ATYP_IPV6)
							{
								// 得到客户端指定的协议类型, ipv4或ipv6, 并open udp_socket随机分配一个udp端口.
								m_client_endpoint = udp::endpoint(m_address.address(), m_address.port());
								boost::system::error_code ec;
								m_udp_socket.open(m_client_endpoint.protocol(), ec);
								if (ec)
								{
									// 打开udp socket失败.
									//  +----+-----+-------+------+----------+----------+
									//  |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
									//  +----+-----+-------+------+----------+----------+
									//  | 1  |  1  | X'00' |  1   | Variable |    2     |
									//  +----+-----+-------+------+----------+----------+
									//  [                                               ]
									p = m_local_buffer.data();
									write_int8(SOCKS_VERSION_5, p);
									write_int8(SOCKS5_GENERAL_SOCKS_SERVER_FAILURE, p);
									write_int8(0x00, p);
									write_int8(1, p);
									// 没用的东西.
									for (int i = 0; i < 6; i++)
										write_int8(0, p);
									boost::asio::async_write(m_local_socket, boost::asio::buffer(m_local_buffer, 10),
										boost::asio::transfer_exactly(10),
										boost::bind(&socks_session::socks_handle_error, shared_from_this(),
											boost::asio::placeholders::error,
											boost::asio::placeholders::bytes_transferred
											)
										);
									return;
								}

								// 绑定udp端口.
								m_udp_socket.bind(udp::endpoint(m_client_endpoint.protocol(), 0), ec);
								if (ec)
								{
									// 打开udp socket失败.
									//  +----+-----+-------+------+----------+----------+
									//  |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
									//  +----+-----+-------+------+----------+----------+
									//  | 1  |  1  | X'00' |  1   | Variable |    2     |
									//  +----+-----+-------+------+----------+----------+
									//  [                                               ]
									p = m_local_buffer.data();
									write_int8(SOCKS_VERSION_5, p);
									write_int8(SOCKS5_GENERAL_SOCKS_SERVER_FAILURE, p);
									write_int8(0x00, p);
									write_int8(1, p);
									// 没用的东西.
									for (int i = 0; i < 6; i++)
										write_int8(0, p);
									boost::asio::async_write(m_local_socket, boost::asio::buffer(m_local_buffer, 10),
										boost::asio::transfer_exactly(10),
										boost::bind(&socks_session::socks_handle_error, shared_from_this(),
											boost::asio::placeholders::error,
											boost::asio::placeholders::bytes_transferred
											)
										);
									return;
								}

								// 如果IP地址为空, 则使用tcp连接上的IP.
								if (m_address.address() == boost::asio::ip::address::from_string("0.0.0.0"))
								{
									boost::system::error_code ec;
									tcp::endpoint endp = m_local_socket.remote_endpoint(ec);
									if (ec)
									{
										CloseSocket();
										return;
									}

									m_client_endpoint.address(endp.address());
								}

								// 打开成功, 返回当前服务器端吕等信息给客户端.

								//  +----+-----+-------+------+----------+----------+
								//  |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
								//  +----+-----+-------+------+----------+----------+
								//  | 1  |  1  | X'00' |  1   | Variable |    2     |
								//  +----+-----+-------+------+----------+----------+
								//  [                                               ]

								p = m_local_buffer.data();
								write_int8(SOCKS_VERSION_5, p);
								write_int8(SOCKS5_SUCCEEDED, p);
								write_int8(0x00, p);
								write_int8(1, p);
								// IP地址(BND.ADDR)直接写0, 客户端知道代理服务器的地址.
								for (int i = 0; i < 4; i++)
									write_int8(0, p);
								// UDP侦听的端口(BND.PORT).
								write_uint16(m_udp_socket.local_endpoint().port(), p);
								// 发送.
								boost::asio::async_write(m_local_socket, boost::asio::buffer(m_local_buffer, 10),
									boost::asio::transfer_exactly(10),
									boost::bind(&socks_session::socks_handle_succeed, shared_from_this(),
										boost::asio::placeholders::error,
										boost::asio::placeholders::bytes_transferred
										)
									);
								return;
							}
							if (m_atyp == SOCKS5_ATYP_DOMAINNAME)
							{
								// TODO: 实现客户端域名解析, 用于udp传回数据.
							}
						}

						//  +----+-----+-------+------+----------+----------+
						//  |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
						//  +----+-----+-------+------+----------+----------+
						//  | 1  |  1  | X'00' |  1   | Variable |    2     |
						//  +----+-----+-------+------+----------+----------+
						//  [                                               ]
						p = m_local_buffer.data();
						write_int8(SOCKS_VERSION_5, p);
						write_int8(SOCKS5_COMMAND_NOT_SUPPORTED, p);
						write_int8(0x00, p);
						write_int8(1, p);
						// 没用的东西.
						for (int i = 0; i < 6; i++)
							write_int8(0, p);
						boost::asio::async_write(m_local_socket, boost::asio::buffer(m_local_buffer, 10),
							boost::asio::transfer_exactly(10),
							boost::bind(&socks_session::socks_handle_error, shared_from_this(),
								boost::asio::placeholders::error,
								boost::asio::placeholders::bytes_transferred
								)
							);
					}
				}
			}
		}

		void socks_handle_connect_3(const boost::system::error_code &error, tcp::resolver::iterator endpoint_iterator)
		{
			if (error)
			{
				OutputDebugStringA(error.message().c_str()); OutputDebugStringA(endpoint_iterator->host_name().c_str());
				if (m_version == SOCKS_VERSION_5)
				{
					// 连接目标失败!
					//  +----+-----+-------+------+----------+----------+
					//  |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
					//  +----+-----+-------+------+----------+----------+
					//  | 1  |  1  | X'00' |  1   | Variable |    2     |
					//  +----+-----+-------+------+----------+----------+
					//  [                                               ]
					char *p = m_local_buffer.data();
					write_int8(SOCKS_VERSION_5, p);
					write_int8(SOCKS5_CONNECTION_REFUSED, p);
					write_int8(0x00, p);
					write_int8(1, p);
					// 没用的东西.
					for (int i = 0; i < 6; i++)
						write_int8(0, p);
					boost::asio::async_write(m_local_socket, boost::asio::buffer(m_local_buffer, 10),
						boost::asio::transfer_exactly(10),
						boost::bind(&socks_session::socks_handle_error, shared_from_this(),
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);

				
				}
				else 
				// 连接失败.
				if (m_version == SOCKS_VERSION_4)
				{
					//  +----+----+----+----+----+----+----+----+
					//  | VN | CD | DSTPORT |      DSTIP        |
					//  +----+----+----+----+----+----+----+----+
					//  | 1  | 1  |    2    |         4         |
					//  +----+----+----+----+----+----+----+----+
					//  [                                       ]
					char *p = m_local_buffer.data();
					write_int8(SOCKS_VERSION_4, p);
					write_int8(SOCKS4_CANNOT_CONNECT_TARGET_SERVER, p);
					// 没用了, 随便填.
					write_uint16(0x00, p);
					write_uint32(0x00, p);
					boost::asio::async_write(m_local_socket, boost::asio::buffer(m_local_buffer, 8),
						boost::asio::transfer_exactly(8),
						boost::bind(&socks_session::socks_handle_error, shared_from_this(),
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);

				
				}
				CloseSocket();
				return;
			}
			else 
			{   //连接成功
				if (m_version == SOCKS_VERSION_5)
				{
					// 连接成功.
					//  +----+-----+-------+------+----------+----------+
					//  |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
					//  +----+-----+-------+------+----------+----------+
					//  | 1  |  1  | X'00' |  1   | Variable |    2     |
					//  +----+-----+-------+------+----------+----------+
					//  [                                               ]

					char *p = m_local_buffer.data();
					int len = 4;
					write_int8(SOCKS_VERSION_5, p);
					write_int8(SOCKS5_SUCCEEDED, p);
					write_int8(0x00, p);
					write_int8(m_atyp, p);

					boost::system::error_code ec;
					tcp::endpoint endp = m_remote_socket.remote_endpoint(ec);
					if (ec)
					{
						CloseSocket();
						return;
					}

					if (m_atyp == SOCKS5_ATYP_IPV4)
					{
						len += 6;
						write_uint32(endp.address().to_v4().to_ulong(), p);
						write_uint16(endp.port(), p);
					}
					if (m_atyp == SOCKS5_ATYP_IPV6)
					{
						len += 18;
						boost::asio::ip::address_v6::bytes_type addr;
						addr = endp.address().to_v6().to_bytes();
						for (std::size_t i = 0; i < addr.size(); i++)
							write_int8(addr[i], p);
						write_uint16(endp.port(), p);
					}
					if (m_atyp == SOCKS5_ATYP_DOMAINNAME)
					{
						len += (m_domain.size() + 3);
						write_int8(m_domain.size(), p);
						write_string(m_domain, p);
						write_uint16(m_port, p);
					}

					// 发送回复.
					boost::asio::async_write(m_local_socket, boost::asio::buffer(m_local_buffer, len),
						boost::asio::transfer_exactly(len),
						boost::bind(&socks_session::socks_handle_succeed, shared_from_this(),
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);

					RecvfromDst();	//第一次从远端接受数据
					return;
				}

				if (m_version == SOCKS_VERSION_4)
				{
					//  +----+----+----+----+----+----+----+----+
					//  | VN | CD | DSTPORT |      DSTIP        |
					//  +----+----+----+----+----+----+----+----+
					//  | 1  | 1  |    2    |         4         |
					//  +----+----+----+----+----+----+----+----+
					//  [                                       ]
					char *p = m_local_buffer.data();
					write_int8(0, p);
					write_int8(SOCKS4_REQUEST_GRANTED, p);

					boost::system::error_code ec;
					tcp::endpoint endp = m_remote_socket.remote_endpoint(ec);
					if (ec)
					{
						CloseSocket();
						return;
					}

					write_uint16(endp.port(), p);
					write_uint32(endp.address().to_v4().to_ulong(), p);

					// 回复成功.
					boost::asio::async_write(m_local_socket, boost::asio::buffer(m_local_buffer, 8),
						boost::asio::transfer_exactly(8),
						boost::bind(&socks_session::socks_handle_succeed, shared_from_this(),
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);

					//接受远端的数据
					RecvfromDst();
					return;
				}
			}
		}

		void socks_handle_resolve(const boost::system::error_code &error, tcp::resolver::iterator endpoint_iterator)
		{
			if (error)
			{
				CloseSocket();
				return;
			}


			std::string remoteHostname = endpoint_iterator->host_name();
			std::string remoteIPAddress = endpoint_iterator->endpoint().address().to_string();
			uint16_t remotePort = endpoint_iterator->endpoint().port();

			std::string localHostname = m_local_socket.local_endpoint().address().to_string();   //预留给htttp
			std::string localIPAddress = m_local_socket.local_endpoint().address().to_string();
			uint16_t localPort = m_local_socket.local_endpoint().port();
			ReturnType result = NONE;  

			result = NoticeNewCon(index, 0, localHostname, localIPAddress, localPort,remoteHostname,remoteIPAddress,remotePort);			//询问是否进行修改，并且确定拦截设置

			InterceptServerrResponses = (bool)(result&INTERCEPT_SERVER_RESPONSES);
			InterceptClientRequests = (bool)(result&INTERCEPT_CLIENT_REQUESTS);
			IsCopy = (InterceptServerrResponses || InterceptClientRequests) ? true : !((bool)(result&NOT_COPY));  //只要有拦截，那么不允许不复制


			//改变了ip地址，或者是只改变了端口的   都用这种连接方法
			if (result &CHANGE_IP_ADDRESS || result&CHANGE_HOST_NAME || result&CHANGE_PORT)
			{
				std::ostringstream s1;
				s1 << remotePort;
				if (result &CHANGE_HOST_NAME)//如果改了主机名
				{
					boost::asio::ip::tcp::resolver::query query(remoteHostname, s1.str());

				}
				boost::asio::ip::tcp::resolver::query query(remoteIPAddress, s1.str());
				endpoint_iterator = m_resolver.resolve(query);

			}

			boost::asio::async_connect(m_remote_socket, endpoint_iterator,
				boost::bind(&socks_session::socks_handle_connect_3,
					shared_from_this(),
					boost::asio::placeholders::error,
					endpoint_iterator
					)
				);

		}

		void socks_handle_error(const boost::system::error_code &error, int bytes_transferred)
		{
			// 什么都不用做了, 退了.
			if (m_udp_socket.is_open())
			{
				// 如果打开了udp socket, 则先关掉不.
				boost::system::error_code ec;
				m_udp_socket.close(ec);
			}
		}

		void socks_handle_succeed(const boost::system::error_code &error, int bytes_transferred)
		{
			if (error)
			{
				CloseSocket();
				return;
			}
			if (m_command == SOCKS5_CMD_UDP)
			{
				// 开始投递异步udp数据接收, 并开始转发数据.
				// 转发规则为:
				// 1. 任何接收到的非来自m_client_endpoint上的数据, 添加协议头后, 都将转发到m_client_endpoint.
				// 2. 接收到来自m_client_endpoint上的数据, 解析协议头, 并转发到协议中指定的endpoint.
				// 3. tcp socket断开时, 取消所有异步IO, 销毁当前session对象.
				// 4. tcp socket上任何数据传输, 处理方法如同步骤2.
				// 5. 任何socket错误, 处理方法如同步骤2.
				// 6. m_udp_timer超时(即5分钟内没任何数据传输), 处理方法如同步骤2.
				for (int i = 0; i < MAX_RECV_BUFFER_SIZE; i++)
				{
					recv_buffer& recv_buf = m_recv_buffers[i];
					boost::array<char, 2048>& buf = recv_buf.buffer;
					m_udp_socket.async_receive_from(boost::asio::buffer(buf), recv_buf.endp,
						boost::bind(&socks_session::socks_handle_udp_read, shared_from_this(),
							i,
							boost::asio::placeholders::error,
							boost::asio::placeholders::bytes_transferred
							)
						);
				}

				// 启动定时器.
				m_udp_timer.expires_from_now(boost::posix_time::seconds(1));
				m_udp_timer.async_wait(
					boost::bind(&socks_session::socks_udp_timer_handle,
						shared_from_this(),
						boost::asio::placeholders::error
						)
					);
			}
			else if (m_command == SOCKS_CMD_CONNECT)
			{
				//第一次从本地开始接受数据
				RecvfromSource();

			}
			else
			{
				// for debug.
				BOOST_ASSERT(0);
			}
		}

		//将数据发给本地
		inline void SendtoSource()
		{
			if (!BSendtoSource && !SendSourceBuffer.empty())
			{
				std::string & tBuffer = SendSourceBuffer.front();
				BSendtoSource = true;
				boost::asio::async_write(m_local_socket, boost::asio::buffer(tBuffer, tBuffer.size()),
					boost::asio::transfer_exactly(tBuffer.size()),
					boost::bind(&socks_session::HandleSenttoSource, shared_from_this(),
						boost::asio::placeholders::error,
						boost::asio::placeholders::bytes_transferred
						)
					);
			}
		}

		//数据已经发至本地
		void HandleSenttoSource(const boost::system::error_code &error, int bytes_transferred)
		{
			if (error)
			{
				CloseSocket();
				return;
			}
			if (!SendSourceBuffer.empty())			//后续想想会有说明问题没
			{
				SendSourceBuffer.pop();
			}
			BSendtoSource = false;
			if (InterceptServerrResponses)	//如果是拦截状态，那么就继续发数据至本地，不管接收的事情
			{
				SendtoSource();
			}
			else
			{
				RecvfromDst();
			}
		}

		//从本地接收数据
		inline void RecvfromSource()
		{
			if (BSuspendRecvfromSource||BRecvfromSource)//如果需要暂停接受或者已经在接受数据了
			{
				return;
			}
			BRecvfromSource = true;
			m_local_socket.async_read_some(
				boost::asio::buffer(SendDstArray),
				boost::bind(
					&socks_session::HandleRecvfromSource,
					shared_from_this(),
					boost::asio::placeholders::error,
					boost::asio::placeholders::bytes_transferred
					)
				);

		}

		//处理从本地接收到了数据
		void HandleRecvfromSource(const boost::system::error_code &error, int bytes_transferred)
		{
			if (error)
			{
				CloseSocket();
				return;
			}
			std::string tBuffer;
			tBuffer.assign(SendDstArray.c_array(), bytes_transferred);
			BRecvfromSource = false;
			if (InterceptClientRequests)//如果拦截本地的请求数据包，那么就通知界面，然后继续接受数据
			{
				NoticeNewDatafromSource(index, NOT_SEND, tBuffer);
				RecvfromSource();
			}
			else
			{
				//如果不拦截，那么就得直接加入发送队列，并且马上发送数据
				SendDstBuffer.push(tBuffer);
				SendtoDst();
				if (IsCopy)
				{
					NoticeNewDatafromSource(index, SENT, tBuffer);
				}


			}

		}

		//从远端接收数据
		inline void RecvfromDst()
		{
			if (BSuspendRecvfromDst||BRecvfromDst)//如果需要暂停接受或者已经在接受数据了
			{
				return;
			}
			BRecvfromDst = true;
			m_remote_socket.async_read_some(boost::asio::buffer(SendSourceArray),
				boost::bind(&socks_session::HandleRecvfromDst, shared_from_this(),
					boost::asio::placeholders::error,
					boost::asio::placeholders::bytes_transferred
					)
				);
		}

		//处理从远端接收到了数据
		void HandleRecvfromDst(const boost::system::error_code &error, int bytes_transferred)
		{
			if (error)
			{
				CloseSocket();
				return;
			}
			std::string  tBuffer;
			tBuffer.assign(SendSourceArray.c_array(), bytes_transferred);
			BRecvfromDst = false;
			if (InterceptServerrResponses)//如果拦截远端的返回包，那么就继续接收数据
			{
				NoticeNewDatafromDst(index, NOT_SEND, tBuffer);
				RecvfromDst();
			}
			else
			{
				SendSourceBuffer.push(tBuffer);
				SendtoSource();
				if (IsCopy)
				{
					NoticeNewDatafromDst(index, SENT, tBuffer);
				}
			}

		}

		//将数据发给远端
		inline void SendtoDst()
		{
			if (!BSendtoDst &&  !SendDstBuffer.empty())
			{
				BSendtoDst = true;
				std::string & tBuffer = SendDstBuffer.front();
				boost::asio::async_write(m_remote_socket, boost::asio::buffer(tBuffer, tBuffer.size()),
					boost::asio::transfer_exactly(tBuffer.size()),
					boost::bind(&socks_session::HandleSenttoDst, shared_from_this(),
						boost::asio::placeholders::error,
						boost::asio::placeholders::bytes_transferred
						)
					);
			}
		}

		//处理数据已经发至远端
		void HandleSenttoDst(const boost::system::error_code &error, int bytes_transferred)
		{
			if (error)
			{
				CloseSocket();
				return;
			}
			if (!SendDstBuffer.empty())
			{
				SendDstBuffer.pop();
			}
			BSendtoDst = false;
			if (InterceptClientRequests || !SendDstBuffer.empty())	//如果是拦截状态或者数据没发完，那么就继续发数据至远端，不管接收的事情
			{
				SendtoDst();
			}
			else
			{
				RecvfromSource();
			}

		}

		void socks_handle_udp_read(int buf_index, const boost::system::error_code &error, int bytes_transferred)
		{
			if (error)
			{
				CloseSocket();
				return;
			}
			// 更新计时.
			m_meter = boost::posix_time::second_clock::local_time();

			// 解析数据.
			recv_buffer& recv_buf = m_recv_buffers[buf_index];
			boost::array<char, 2048>& buf = recv_buf.buffer;

			char *p = buf.data();

			// 这里进行数据转发, 如果是client发过来的数据, 则解析协议包装.
			if (recv_buf.endp == m_client_endpoint)
			{
				// 解析协议.
				//  +----+------+------+----------+----------+----------+
				//  |RSV | FRAG | ATYP | DST.ADDR | DST.PORT  |   DATA   |
				//  +----+------+------+----------+----------+----------+
				//  | 2  |  1   |  1   | Variable |    2      | Variable |
				//  +----+------+------+----------+----------+----------+
				udp::endpoint endp;

				do {
					// 字节支持小于24.
					if (bytes_transferred < 24)
						break;

					// 不是协议中的数据.
					if (read_int16(p) != 0 || read_int8(p) != 0)
						break;

					// 远程主机IP类型.
					boost::int8_t atyp = read_int8(p);
					if (atyp != 0x01 && atyp != 0x04)
						break;

					// 目标主机IP.
					boost::uint32_t ip = read_uint32(p);
					if (ip == 0)
						break;
					endp.address(boost::asio::ip::address_v4(ip));

					// 读取端口号.
					boost::uint16_t port = read_uint16(p);
					if (port == 0)
						break;
					endp.port(port);

					// 这时的指针p是指向数据了(2 + 1 + 1 + 4 + 2 = 10).
					std::string response(p, bytes_transferred - 10);

					// 转发到指定的endpoint.
					do_write(response, endp);
				} while (false);

				// 继续读取下一组udp数据.
				m_udp_socket.async_receive_from(boost::asio::buffer(buf), recv_buf.endp,
					boost::bind(&socks_session::socks_handle_udp_read, shared_from_this(),
						buf_index,
						boost::asio::placeholders::error,
						boost::asio::placeholders::bytes_transferred
						)
					);
				return;
			}

			// 转发至客户端, 需要添加头协议.
			std::string response;
			response.resize(bytes_transferred + 10);
			char* wp = (char*)response.data();

			// 添加头信息.
			write_uint16(0, wp);	// RSV.
			write_uint8(0, wp);		// FRAG.
			write_uint8(1, wp);		// ATYP.
			write_uint32(recv_buf.endp.address().to_v4().to_ulong(), wp);	// ADDR.
			write_uint16(recv_buf.endp.port(), wp);	// PORT.
			std::memcpy(wp, p, bytes_transferred);	// DATA.

													// 转发数据.
			do_write(response, m_client_endpoint);

			// 继续读取下一组udp数据.
			m_udp_socket.async_receive_from(boost::asio::buffer(buf), recv_buf.endp,
				boost::bind(&socks_session::socks_handle_udp_read, shared_from_this(),
					buf_index,
					boost::asio::placeholders::error,
					boost::asio::placeholders::bytes_transferred
					)
				);

		}

		void socks_handle_udp_write(const boost::system::error_code& error, std::size_t bytes_transferred)
		{
			if (error)
			{
				return;
			}

			// 更新计时.
			m_meter = boost::posix_time::second_clock::local_time();

			// 弹出已经发送过的数据包.
			m_send_buffers.pop_front();
			if (!m_send_buffers.empty())
			{
				// 继续发送下一个数据包.
				m_udp_socket.async_send_to(
					boost::asio::buffer(m_send_buffers.front().buffer.data(),
						m_send_buffers.front().buffer.size()),
					m_send_buffers.front().endp,
					boost::bind(&socks_session::socks_handle_udp_write,
						shared_from_this(),
						boost::asio::placeholders::error,
						boost::asio::placeholders::bytes_transferred
						)
					);
			}
		}

		void socks_udp_timer_handle(const boost::system::error_code& error)
		{
			// 出错失败返回.
			if (error)
				return;

			// 超时关闭.
			if (boost::posix_time::second_clock::local_time() - m_meter >= boost::posix_time::minutes(1))
			{
				CloseSocket();
				return;
			}

			// 启动定时器.
			m_udp_timer.expires_from_now(boost::posix_time::seconds(1));
			m_udp_timer.async_wait(
				boost::bind(&socks_session::socks_udp_timer_handle,
					shared_from_this(),
					boost::asio::placeholders::error
					)
				);
		}

		void do_write(const std::string& msg, const udp::endpoint& endp)
		{
			bool write_in_progress = !m_send_buffers.empty();
			// 保存到发送队列.
			send_buffer buf;
			buf.buffer = msg;
			buf.endp = endp;
			m_send_buffers.push_back(buf);
			if (!write_in_progress)
			{
				m_udp_socket.async_send_to(
					boost::asio::buffer(m_send_buffers.front().buffer.data(),
						m_send_buffers.front().buffer.size()),
					m_send_buffers.front().endp,
					boost::bind(&socks_session::socks_handle_udp_write,
						shared_from_this(),
						boost::asio::placeholders::error,
						boost::asio::placeholders::bytes_transferred
						)
					);
			}
		}


	protected:
		bool IsCopy;					//是否复制数据，如果否的话，那就是直接转发，不会通知jni
		bool InterceptServerrResponses; //是否拦截服务器响应
		bool InterceptClientRequests;	//是否拦截客户端请求

		bool BSendtoDst;				//是否正在发送给远端
		bool BSendtoSource;				//是否正在发送给本地
		bool BRecvfromDst;			//是否正在从远端接收数据
		bool BRecvfromSource;			//是否正在从本地接收数据

		bool BSuspendRecvfromDst;		//是否暂停从远端接收数据
		bool BSuspendRecvfromSource;		//是否暂停从本地接收数据 

		IndexType index;				//索引值

		boost::function<ReturnType(
			_In_ IndexType index,
			_In_ uint8_t protocolType,
			_In_ std::string SourceHostname,
			_In_ std::string SourceIPAddress,
			_In_ uint16_t SourcePort,
			_In_  _Out_ std::string &DstHostname,
			_In_  _Out_ std::string &DstIPAddress,
			_In_  _Out_ uint16_t &DstPort
			)> NoticeNewCon;
		boost::function<ReturnType(IndexType index)> DelinSessionSum;
		boost::function<ReturnType(IndexType index, ProxyDataStatus type, std::string &data)> NoticeNewDatafromDst;
		boost::function<ReturnType(IndexType index, ProxyDataStatus type, std::string &data)> NoticeNewDatafromSource;
		boost::function<ReturnType(IndexType index)> NoticeClose;

		boost::array<char, 2048> SendDstArray;
		boost::array<char, 2048> SendSourceArray;
		boost::lockfree::spsc_queue<std::string, boost::lockfree::fixed_sized<false> > SendDstBuffer;		//发送缓存  队列   //注意修正参数
		boost::lockfree::spsc_queue<std::string, boost::lockfree::fixed_sized<false> > SendSourceBuffer;		//接收缓存  队列  //注意修正参数

		boost::asio::io_service &m_io_service;
		tcp::socket m_local_socket;
		boost::array<char, 2048> m_local_buffer;
		tcp::socket m_remote_socket;
		boost::array<char, 2048> m_remote_buffer;
		boost::asio::streambuf m_streambuf;
		udp::socket m_udp_socket;
		udp::endpoint m_client_endpoint;
		// 数据接收缓冲.
		struct recv_buffer
		{
			udp::endpoint endp;
			boost::array<char, 2048> buffer;
		};
		std::map<int, recv_buffer> m_recv_buffers;
		// 用作数据包发送缓冲.
		struct send_buffer
		{
			udp::endpoint endp;
			std::string buffer;
		};
		std::deque<send_buffer> m_send_buffers;
		tcp::resolver m_resolver;
		int m_version;
		int m_method;
		std::string m_uname;
		std::string m_passwd;
		int m_command;
		int m_atyp;
		tcp::endpoint m_address;
		std::string m_domain;
		short m_port;
		bool m_verify_passed;
		boost::asio::deadline_timer m_udp_timer;
		boost::posix_time::ptime m_meter;
	};

	class socks_server : public boost::noncopyable, public boost::enable_shared_from_this<socks_server>
	{
	public:
		socks_server(
			std::string addreee,
			short server_port,
			ReturnType(*pNoticeNewCon)(
				_In_ IndexType index,
				_In_ uint8_t protocolType,
				_In_ std::string SourceHostname,
				_In_ std::string SourceIPAddress,
				_In_ uint16_t SourcePort,
				_In_  _Out_ std::string &DstHostname,
				_In_  _Out_ std::string &DstIPAddress,
				_In_  _Out_ uint16_t &DstPort
				),
			ReturnType(*pNoticeNewDatafromDst)(
				_In_ IndexType index,
				_In_ ProxyDataStatus type,
				_In_ std::string &data
				),
			ReturnType(*pNoticeNewDatafromSource)(
				_In_ IndexType index,
				_In_ ProxyDataStatus type,
				_In_ std::string &data
				),
			ReturnType(*pNoticeClose)(
				_In_ IndexType index
				)
			) :
			server_thread(nullptr),
			m_acceptor(m_io_service, tcp::endpoint(boost::asio::ip::address_v4::from_string(addreee), server_port)),
			NoticeNewCon(pNoticeNewCon),
			NoticeNewDatafromDst(pNoticeNewDatafromDst),
			NoticeNewDatafromSource(pNoticeNewDatafromSource),
			NoticeClose(pNoticeClose)
		{
			m_acceptor.set_option(boost::asio::ip::tcp::acceptor::reuse_address(true));
		}
		~socks_server()
		{
			std::cout << "Start cleaning session list" << std::endl;
			CloseServer();
			std::cout << "Complete clearance." << std::endl;
		}

	public:
		//收到了连接请求  
		void Run()
		{
			boost::shared_ptr<socks_session> new_session(new socks_session(m_io_service));
			m_acceptor.async_accept(
				new_session->socket(),
				boost::bind(
					&socks_server::handle_accept,
					shared_from_this(),							//一个妥协，因为如果使用shared_from_this()就需要再来个函数了
					new_session,
					boost::asio::placeholders::error
					)
				);

			//一定要在acccpet之后开始线程，不然因为没有任务会停止
			Resume();
		}
		void handle_accept(boost::shared_ptr<socks_session> &tnew_session, const boost::system::error_code& error)
		{
			if (error)
			{
				return;
			}
			int16_t index = SessionSum.Insert(tnew_session);
			printf("收到了一个连接  序号值为：%d\n", index);
			tnew_session->SetIndex(index);
			tnew_session->SetNoticeNewCon(boost::bind(&socks_server::NewConWithLock, shared_from_this(), _1, _2, _3, _4,_5,_6,_7,_8));
			tnew_session->SetDelinSessionSum(boost::bind(&socks_server::DelinSessionSum, shared_from_this(), _1));
			tnew_session->SetNoticeNewDatafromDst(boost::bind(&socks_server::NoticeNewDatafromDstWithLock, shared_from_this(), _1, _2, _3));
			tnew_session->SetNoticeNewDatafromSource(boost::bind(&socks_server::NoticeNewDatafromSourceWithLock, shared_from_this(), _1, _2, _3));
			tnew_session->SetNoticeClose(boost::bind(&socks_server::NoticeCloseWithLock, shared_from_this(), _1));

			tnew_session->start();
			boost::shared_ptr<socks_session> new_session(new socks_session(m_io_service));


			m_acceptor.async_accept(
				new_session->socket(),
				boost::bind(&socks_server::handle_accept,
					shared_from_this(),
					new_session,
					boost::asio::placeholders::error
					)
				);
		}

		void join()//阻塞
		{
			if (server_thread != nullptr)
			{
				server_thread->join();
			}
		}
		void Stop()   //也可以叫做暂停   关闭之后可以恢复  端口监听并没有停止
		{
			if (server_thread != nullptr)
			{
				m_io_service.stop();
				join();
				delete server_thread;
				server_thread = nullptr;
			}
		}

		void CloseServer()		//close之后不可恢复
		{
			m_acceptor.close();
			SessionSum.Clear();
			Stop();
		}
		bool SuspendRecv(int16_t index,bool tBSuspendRecvfromDst, bool tBSuspendRecvfromSource)
		{
			if (SessionSum.Exist(index))
			{
				boost::shared_ptr<socks_session>& t = SessionSum.Find(index);
				if (t == nullptr)
				{
					return false;
				}
				t->SuspendRecv(tBSuspendRecvfromDst, tBSuspendRecvfromSource);
				return true;
			}
			return false;
		}
		bool AddSendtoDstBuffer(int16_t index, std::string &buffer)
		{
			if (SessionSum.Exist(index))
			{
				//SessionSum.Find(index)->addSendDstBuffer(buffer);
				boost::shared_ptr<socks_session>& t = SessionSum.Find(index);
				if (t == nullptr)
				{
					return false;
				}
				t->addSendDstBuffer(buffer);
				return true;
			}
			return false;
		}

		bool AddSendtoSourceBuffer(int16_t index, std::string &buffer)
		{
			if (SessionSum.Exist(index))
			{
				//SessionSum.Find(index)->addSendSourceBuffer(buffer);
				boost::shared_ptr<socks_session>& t = SessionSum.Find(index);
				if (t == nullptr)
				{
					return false;
				}
				t->addSendSourceBuffer(buffer);
				//boost::shared_ptr<socks_session>& t = SessionSum.Find(index);
				//t->addSendSourceBuffer(buffer);
				return true;
			}
			return false;
		}
		bool SetInterceptStatus(IndexType index ,int8_t status)
		{
			if (SessionSum.Exist(index))
			{
				
				boost::shared_ptr<socks_session>& t = SessionSum.Find(index);
				if (t == nullptr)
				{
					return false;
				}
				t->SetInterceptStatus(status);

				return true;
			}
			return false;
		}
		void PrintfStatus(IndexType index, socks_session& t)
		{
			using namespace std;
			cout << "index  " << index << "   status:" << t.GetSocketStatus()<< endl;
		}
		Dbg(void ShowStatus(IndexType index = 0)
		{
			using namespace std;
			if (index != 0)
			{
				boost::shared_ptr<socks_session> t = SessionSum.Find(index);
				cout << "index  " << index << "   status:" << t->GetSocketStatus() << endl;
			}
		})

			int Size()
		{
			return SessionSum.Size();
		}

		//用于连接中断时的清理数据
		ReturnType DelinSessionSum(IndexType index)
		{
			Dbg(std::cout << std::endl << "del " << index << "  -----------------------------------------------" << std::endl << std::endl;)
				return SessionSum.Erase(index);
		}

		//用于手动关闭连接关闭
		ReturnType DelSession(IndexType index)
		{
			if (SessionSum.Exist(index))
			{
				SessionSum.Find(index)->CloseSocket();
				return SUCCESS;
			}
			return FAIL;
		}

		ReturnType NewConWithLock(IndexType index, uint8_t protocolType, std::string SourceHostname, std::string SourceIPAddress, uint16_t SourcePort, std::string &DstHostname, std::string &DstIPAddress, uint16_t &DstPort)
		{
			boost::mutex::scoped_lock lock(muNewCon);

			return NoticeNewCon( index,  protocolType, SourceHostname, SourceIPAddress, SourcePort, DstHostname, DstIPAddress, DstPort);
		}

		ReturnType NoticeNewDatafromDstWithLock(IndexType index, ProxyDataStatus type, std::string &data)
		{
			boost::mutex::scoped_lock lock(muNewData);

			return NoticeNewDatafromDst(index, type, data);
		}

		ReturnType NoticeNewDatafromSourceWithLock(IndexType index, ProxyDataStatus type, std::string &data)
		{
			boost::mutex::scoped_lock lock(muNewData);

			return NoticeNewDatafromSource(index, type, data);
		}
		ReturnType NoticeCloseWithLock(IndexType index)
		{
			boost::mutex::scoped_lock lock(muClose);

			return NoticeClose(index);
		}
		inline void Resume()
		{
			m_io_service.reset();
			if (server_thread == nullptr)
			{
				server_thread = new boost::thread(
					socks_server::RunThread,
					boost::ref(m_io_service)
					);
			}
		}
	protected:
		static 	void RunThread(boost::asio::io_service & io_service)
		{
			io_service.run();
		}
	private:
		boost::mutex muNewCon;	//有新连接时的互斥锁
		boost::mutex muNewData;	//有新数据时的互斥锁
		boost::mutex muClose;	//通知关闭时的互斥锁


		//boost::function<ReturnType(IndexType index, std::string &address, uint16_t &port, std::string &hostname)>NoticeNewCon;
		ReturnType(*NoticeNewCon)(
			_In_ IndexType index,
			_In_ uint8_t protocolType,
			_In_ std::string SourceHostname,
			_In_ std::string SourceIPAddress,
			_In_ uint16_t SourcePort,
			_In_  _Out_ std::string &DstHostname,
			_In_  _Out_ std::string &DstIPAddress,
			_In_  _Out_ uint16_t &DstPort
			);
		//boost::function<ReturnType(IndexType index, FuncType type, std::string &data)>NoticeNewData;
		ReturnType(*NoticeNewDatafromDst)(IndexType index, ProxyDataStatus type, std::string &data);
		ReturnType(*NoticeNewDatafromSource)(IndexType index, ProxyDataStatus type, std::string &data);
		ReturnType(*NoticeClose)(IndexType index);

		ManageIndexNumber<IndexType, boost::shared_ptr<socks_session>> SessionSum;

		boost::thread * server_thread;
		boost::asio::io_service m_io_service;
		tcp::acceptor m_acceptor;

	};

} // namespace socks

#endif // SOCKS_SERVER_HPP
