#pragma once
#include <string>
using namespace std;

#ifdef __cplusplus
extern "C"
{
#endif

class ChatroomInfo
{
public:
	string m_strName;
	string m_strCreaterId;
	string m_strRoomId;
	bool m_bLive;
};
#ifdef __cplusplus
}
#endif