#ifndef __STARRTC_CORE__
#define __STARRTC_CORE__
#include "CUserManager.h"
#include "ISrcListener.h"
#include "IStarIMChatroomListener.h"
#include "IChatroomGetListListener.h"
#include <string>
using namespace std;

#ifdef __cplusplus
extern "C"
{
#endif
/*
 * StarRtc接口类
 */
class StarRtcCore
{
private:
	/*
	 * 构造函数
	 * @param pUserManager 用户配置信息
	 */
	StarRtcCore();
	
public:

	/*
	 * 获取StarRtc接口对象
	 * @param pUserManager 用户配置信息
	 */
	static StarRtcCore* getStarRtcCoreInstance();
	
	/*
	 * 析构函数
	 */
	~StarRtcCore();

	/*
	 * 注册回调函数
	 */
	void registerCallback();

	/**
	 * 添加StarIMChatroom消息监听
	 * @param listener
	 */
	void addStarIMChatroomListener(IStarIMChatroomListener* listener);

	/**
	 * 添加Src消息监听
	 * @param listener
	 */
	void addSrcListener(ISrcListener* pSrcListener);

	/**
	 * 添加获取列表监听
	 * @param listener
	 */
	void addGetListListener(IChatroomGetListListener* listener);

	int queryAllChatRoomList(char* servAddr, int servPort, char* userId, char* listType);

	void setGlobalSetting(int videoEnable, int audioEnable,
		int videoBigIsHw,
		int videoBigWidth, int videoBigHeight, int videoBigFps, int videoBigBitrate,
		int videoSmallWidth, int videoSmallHeight, int videoSmallFps, int videoSmallBitrate,
		int openGLESEnable, int dynamicBitrateAndFpsEnable, int voipP2PEnable);

	/**
	 * 启动IM服务
	 */
	bool startIMServer(char* servIP, int servPort, char* agentId, char* userId, char* starToken);

	/**
	 * 停止IM服务
	 */
	bool stopIMServer();
	/*
	 * 创建ChatRoom
	 */
	bool createChatRoom(string serverIp, int serverPort, string strName, int chatroomType, string strAgentId, string strUserId, string strTokenId);

	/*
	 * 加入ChatRoom
	 */
	bool joinChatRoom(string serverIp, int serverPort, string strChatroomId, string strAgentId, string strUserId, string strTokenId);

	/*
	 *  与ChatRoom断开连接
	 */
	bool stopChatRoomConnect();

	
	/*
	 * 设置数据流配置
	 */
	bool setStreamConfigSrc(int* sendBuf, int length);
	
	/*
	 * 创建Channel
	 */
	bool createPublicChannel(string strServerIp, int port, string strName, int channelType, string strChatroomId, string strAgentId, string strUserId, string strTokenId);

	int startLiveSrcEncoder(int audioSampleRateInHz, int audioChannels, int audioBitRate, int rotation, unsigned char* pPPSData, int nPPSDataLen, unsigned char* pSPSData, int nSPSDataLen);
	void setDeviceName(char* strDeviceName);
	int startUploadSrcServer(char* servAddr, int servPort, char* agentId, char* userId, char* starToken, char* channelId/* ,int maxAudioPacketNum,int maxVideoPacketNum */);

	int stopUploadSrcServer();
	int stopLiveSrcCodec();

	//videoData的释放由此函数负责
	void insertVideoNalu(unsigned char* videoData, int dataLen);

	int saveToChatRoomList(char* servAddr, int servPort, char* userId, int listType, char* roomId, char* data);
	
	int delFromChatRoomList(char* servAddr, int servPort, char* userId, int listType, char* roomId);


	//=========================================================================
	//===========================    live chatroom回调    ===========================
	//=========================================================================

	static int chatroomQueryAllListOK(char* listData, void* userData);
	
	static int createChatroomOK(char* roomId, int maxContentLen, void* userData);

	static int createChatroomFailed(void* userData, char* errString);

	static int joinChatroomOK(char* roomId, int maxContentLen, void* userData);

	static int joinChatroomFailed(char* roomId, char* errString, void* userData);

	static int chatroomError(char* errString, void* userData);

	static int chatroomStop(void* userData);


	static int deleteChatroomOK(char* roomId, void* userData);
	static int deleteChatroomFailed(char* roomId, char* errString, void* userData);


	//=========================================================================
	//===========================    liveSrc回调    ===========================
	//=========================================================================
	static int createChannelOK(char* channelId, void* userData);
	static int createChannelFailed(char* errString, void* userData);

	static int applyUploadChannelOK(char* channelId, void* userData);
	static int applyUploadChannelFailed(char* errString, char* channelId, void* userData);

	static int deleteChannelOK(char* channelId, void* userData);
	static int deleteChannelFailed(char* errString, char* channelId, void* userData);

	static int setPeerStreamDownloadConfigOK(char* channelId, void* userData);
	static int setPeerStreamDownloadConfigFailed(char* channelId, void* userData);

	static int stopOK(void* userData);
	static int srcError(char* errString, void* userData);
private:
	IStarIMChatroomListener *m_pStarIMChatroomListener;
	ISrcListener* m_pSrcListener;
	IChatroomGetListListener* m_pGetListListener;

};

#ifdef __cplusplus
}
#endif
#endif


