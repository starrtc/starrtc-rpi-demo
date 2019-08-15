#pragma once
#include <list>
#include "ChatroomInfo.h"

#ifdef __cplusplus
extern "C"
{
#endif

class IChatroomGetListListener
{
public:
	/**
	 * 查询聊天室列表回调
	 */
	virtual int chatroomQueryAllListOK(list<ChatroomInfo>& listData) = 0;
};


#ifdef __cplusplus
}
#endif