#ifndef __XH_LIVE_MANAGER__
#define __XH_LIVE_MANAGER__
#include "CChatroomManager.h"
#include "CSrcManager.h"
#include "CUserManager.h"
#include "CLiveParam.h"
#include "IChatroomGetListListener.h"
#include "ChatroomInfo.h"
#include <string>
#include <list>
using namespace std;

#ifdef __cplusplus
extern "C"
{
#endif

class XHLiveManager : public IChatroomGetListListener
{
public:
	/*
	 * 构造方法
	 */
	XHLiveManager(CUserManager* pUserManager);
	/*
	 * 析构方法
	 */
	~XHLiveManager();

	static void getLiveList(CUserManager* pUserManager, string strUserId, string listType, list<ChatroomInfo>& listData);
	/**
	 * 创建直播
	 */
	string createLive(string strName, int chatroomType, int channelType);

	/**
	 * 开始直播
	 * @param strLiveID ID
	 */
	bool startLive(string strLiveID, bool bGetData);

	/*
	* 全局参数设置
	*/
	void globalSetting(int w, int h, int fps, int bitrate);

	/*
	 * 开启直播编码器
	 */
	bool startEncoder(int audioSampleRateInHz, int audioChannels, int audioBitRate, unsigned char* ppsData,int ppsDataLen, unsigned char* spsData,int spsDataLen);

	/**
	 * 保存到列表
	 * @param userId
	 * @param type
	 * @param liveId
	 * @param data
	 */
	bool saveToList(string userId, int type, string liveId, string data);

	/**
	 * 从列表删除
	 * @param userId 用户ID
	 * @param type 类型
	 * @param liveId liveID
	 */
	void deleteFromList(string userId, int type, string liveId);

	void insertVideoRaw(unsigned char* videoData, int dataLen, int isBig);

	/**
	 * 查询聊天室列表回调
	 */
	virtual int chatroomQueryAllListOK(list<ChatroomInfo>& chatRoomInfoList);
public:
	CLiveParam m_Param;
private:
	CUserManager* m_pUserManager;
	CChatroomManager* m_pChatroomManager;
	CSrcManager* m_pSrcManager; 
	static bool m_bGetListReturn;
};

#ifdef __cplusplus
}
#endif
#endif


