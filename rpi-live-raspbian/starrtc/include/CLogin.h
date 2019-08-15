#ifndef __LOGIN__
#define __LOGIN__
#include "CUserManager.h"
#include <string>
using namespace std;

#ifdef __cplusplus
extern "C"
{
#endif

class CLogin
{
public:
	/*
	 * 构造方法
	 */
	CLogin(CUserManager* pUserManager);
	/*
	 * 析构方法
	 */
	~CLogin();

	/*
	 * 登录
	 */
	bool logIn();

	/*
	 * 开启IM服务
	 */
	bool startIMServer(string strIP, int nPort, string userId, string agentId, string strToken);

	/*
	 * 开启IM服务
	 */
	bool stopIMServer();
};

#ifdef __cplusplus
}
#endif
#endif


