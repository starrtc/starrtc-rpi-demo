#ifndef __CHATROOM_MANAGER__
#define __CHATROOM_MANAGER__
#include "CUserManager.h"
#include "IStarIMChatroomListener.h"
#include "IChatroomGetListListener.h"
#include <string>
using namespace std;

#ifdef __cplusplus
extern "C"
{
#endif

class CChatroomManager : public IStarIMChatroomListener
{
public:
	/*
	 * 构造方法
	 */
	CChatroomManager(CUserManager* pUserManager);
	/*
	 * 析构方法
	 */
	~CChatroomManager();
public:

	/*
	 * 添加获取列表后回调函数指针
	 * @param pChatroomGetListListener 回调函数指针
	 */
	static void addChatroomGetListListener(IChatroomGetListListener* pChatroomGetListListener);
	
	/*
	 * 获取聊天室列表
	 * @param pUserManager 用户信息
	 * @param listType 类型
	 */
	static void getChatroomList(CUserManager* pUserManager, string strUserId, string listType);

	/*
	* 创建ChatRoom
	*/
	bool createChatRoom(string strName, int chatroomType);

	/*
	* 加入ChatRoom
	*/
	bool joinChatRoom(string strChatroomId);

	/**
	 * 退出聊天室
	 */
	bool exitChatroom();

	/*
	 * 删除聊天室
	 */
	bool deleteChatRoom(string strRoomId, int listType);

	/*
	 * 创建后上报创建的聊天室信息
	 */
	bool reportChatroom(string strRoomId, int listType, string data);

	string getChatroomId();
public:
	//聊天室创建成功
	void chatroomCreateOK(string roomId, int maxContentLen);
	//聊天室加入成功
	void chatroomJoinOK(string roomId, int maxContentLen);
	//聊天室创建失败
	void chatroomCreateFailed(string errString);
	//聊天室加入失败
	void chatroomJoinFailed(string roomId, string errString);
	//聊天室报错
	void chatRoomErr(string errString);
	//聊天室关闭成功
	void chatroomStopOK();
	//聊天室删除成功
	void chatroomDeleteOK(string roomId);
	//聊天室删除失败
	void chatroomDeleteFailed(string roomId, string errString);
private:
	void resetReturnVal();
	/**
	 * 成功
	 * @param data
	 */
	void success();
	/**
	 * 失败
	 * @param errMsg
	 */
	void failed(string errMsg);
private:
	//用户信息
	CUserManager* m_pUserManager;
	bool m_bJoinChatRoom;
	bool m_bReturn;
	bool m_bSuccess;
	string m_strErrInfo;
	string m_ChatRoomId;
};

#ifdef __cplusplus
}
#endif
#endif


