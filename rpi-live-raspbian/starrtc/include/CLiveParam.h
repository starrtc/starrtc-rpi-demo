#ifndef __LIVE_PARAM__
#define __LIVE_PARAM__
#include <string>
using namespace std;

#ifdef __cplusplus
extern "C"
{
#endif

class CAudioParamInfo
{
public:
	CAudioParamInfo();
	~CAudioParamInfo();
public:
	int audioSampleRateInHz;
	int audioChannels;
	int audioBitRate;
};
class CVideoParam
{
public:
	CVideoParam();
	~CVideoParam();

	void setPPSData(unsigned char* data, int dataLen);

	void setSPSData(unsigned char* data, int dataLen);
public:
	int  w;
	int  h;
	int fps;
	int bitrate;
	unsigned char* ppsData;
	int ppsDataLen;
	unsigned char* spsData;
	int spsDataLen;
};


class CLiveParam
{
public:
	CLiveParam();
	~CLiveParam();
public:
	CAudioParamInfo audioParam;
	CVideoParam videoParam;
};


#ifdef __cplusplus
}
#endif
#endif


