#pragma once
#include <string>
#include <list>
using namespace std;
#ifdef __cplusplus
extern "C"
{
#endif

class IStarIMChatroomListener
{
public:
	//聊天室创建成功
	virtual void chatroomCreateOK(string roomId, int maxContentLen) = 0;
	//聊天室加入成功
	virtual void chatroomJoinOK(string roomId, int maxContentLen) = 0;
	//聊天室创建失败
	virtual void chatroomCreateFailed(string errString) = 0;
	//聊天室加入失败
	virtual void chatroomJoinFailed(string roomId, string errString) = 0;
	//聊天室报错
	virtual void chatRoomErr(string errString) = 0;
	//聊天室关闭成功
	virtual void chatroomStopOK() = 0;
	//聊天室删除成功
	virtual void chatroomDeleteOK(string roomId) = 0;
	//聊天室删除失败
	virtual void chatroomDeleteFailed(string roomId, string errString) = 0;
};
#ifdef __cplusplus
}
#endif
