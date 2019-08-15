#ifndef __USER_MANAGER__
#define __USER_MANAGER__
#include <string>
using namespace std;

#ifdef __cplusplus
extern "C"
{
#endif

#define AUDIO_SAMPLE_RATE	16000
#define AUDIO_CHANNELS		1
#define AUDIO_BIT_RATE		32
class CAudioParam
{
public:
	int m_nSampleRateInHz;
	int m_nChannels;
	int m_nBitRate;
};

class CServiceParam
{

public:
	string  m_strUserId;
	string  m_strAgentId;

	string  m_strLoginServiceIP;
	int m_nLoginServicePort;
	string  m_strMessageServiceIP;
	int m_nMessageServicePort;
	string  m_strChatServiceIP;
	int m_nChatServicePort;
	string  m_strUploadServiceIP;
	int m_nUploadServicePort;
	string  m_strDownloadServiceIP;
	int m_nDownloadServicePort;
	string  m_strVOIPServiceIP;
	int m_nVOIPServicePort;

	string  m_strRequestListAddr;

public:
	int m_CropType;
	int m_FrameRate;
};

class CUserManager
{
public:
	/*
	 * 构造方法
	 */
	CUserManager();
	/*
	 * 析构方法
	 */
	~CUserManager();

	bool readConfig();
	bool writeConfig();
public:
	string m_strAuthKey;
	string  m_strTokenId;

	string m_strIMServerIp;
	int m_nIMServerPort;
	int m_nDeployType;
	bool m_bVoipP2P;
	bool m_bAEventCenterEnable;
	CServiceParam m_ServiceParam;
	CAudioParam m_AudioParam;

	string m_strDeviceName;
};

#ifdef __cplusplus
}
#endif
#endif


