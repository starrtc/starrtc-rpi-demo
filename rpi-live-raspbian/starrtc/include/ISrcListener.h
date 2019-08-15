#pragma once
#ifdef __cplusplus
extern "C"
{
#endif
class  ISrcListener
{
public:
	virtual int createChannelOK(char* channelId) = 0;
	virtual int createChannelFailed(char* errString) = 0;

	virtual int applyUploadChannelOK(char* channelId) = 0;
	virtual int applyUploadChannelFailed(char* errString, char* channelId) = 0;

	virtual int deleteChannelOK(char* channelId) = 0;
	virtual int deleteChannelFailed(char* errString, char* channelId) = 0;

	virtual int setPeerStreamDownloadConfigOK(char* channelId) = 0;
	virtual int setPeerStreamDownloadConfigFailed(char* channelId) = 0;

	virtual int stopOK() = 0;

	virtual int srcError(char* errString) = 0;
};

#ifdef __cplusplus
}
#endif