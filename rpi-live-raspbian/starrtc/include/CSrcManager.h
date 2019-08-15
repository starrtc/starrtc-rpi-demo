#ifndef __SRC_MANAGER__
#define __SRC_MANAGER__
#include "CUserManager.h"
#include "ISrcListener.h"
#include <string>
using namespace std;

#ifdef __cplusplus
extern "C"
{
#endif
#define STREAM_CONFIG_MAX_SIZE 7
class CSrcManager : public ISrcListener
{
public:
	/*
	 * 构造方法
	 */
	CSrcManager(CUserManager* pUserManager);
	/*
	 * 析构方法
	 */
	~CSrcManager();
public:
	/*
	 * 创建Channel
	 */
	bool createChannel(string strName, string strChatroomId);

	/*
	* 全局参数设置
	*/
	void globalSetting(int w, int h, int fps, int bitrate);

	/*
	 * 开启直播编码器
	 */
	bool startEncoder(int audioSampleRateInHz, int audioChannels, int audioBitRate, unsigned char* ppsData,int ppsDataLen, unsigned char* spsData,int spsDataLen);
	/*
	 * 设置设备名称
	 */
	void setDeviceName(char* strDeviceName);
	/*
	 * Channel 申请上传
	 */
	bool applyUpload(string channelId);

	//videoData的释放由此函数负责
	void insertVideoRaw(unsigned char* videoData, int dataLen, int isBig);

	/*
	 * Channel 停止上传
	 */
	bool stopUpload();

	/*
	 *  停止直播编码器
	 */
	bool stopEncoder();
public:
	virtual int createChannelOK(char* channelId);
	virtual int createChannelFailed(char* errString);

	virtual int applyUploadChannelOK(char* channelId);
	virtual int applyUploadChannelFailed(char* errString, char* channelId);

	virtual int deleteChannelOK(char* channelId);
	virtual int deleteChannelFailed(char* errString, char* channelId);

	virtual int setPeerStreamDownloadConfigOK(char* channelId);
	virtual int setPeerStreamDownloadConfigFailed(char* channelId);

	virtual int stopOK();

	virtual int srcError(char* errString);
private:
	void resetReturnVal();

	/**
	 * 成功
	 * @param data
	 */
	virtual void success();

	/**
	 * 失败
	 * @param errMsg
	 */
	virtual void failed(string errMsg);
public:
	string m_ChannelId;
private:
	CUserManager* m_pUserManager;
	
	bool m_bReturn;
	bool m_bSuccess;
	string m_strErrInfo;
	int m_configArr[STREAM_CONFIG_MAX_SIZE];
};

#ifdef __cplusplus
}
#endif
#endif


