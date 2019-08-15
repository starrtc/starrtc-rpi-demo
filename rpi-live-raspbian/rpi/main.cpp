#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "XHLiveManager.h"
#include "CLogin.h"
#include "XHChatroomType.h"
#include "CInterfaceUrls.h"

XHLiveManager* g_pXHLiveManager = NULL;

 int main( int argc, char **argv )
{ 

	CUserManager* pUserManager = new CUserManager();
	CLogin login(pUserManager);

	bool bRet = login.logIn();

	while(bRet == false)
	{
		usleep( 1000 );
		bRet = login.logIn();
	}
	string strName = "rpiLive" + pUserManager->m_ServiceParam.m_strUserId;
	XH_CHATROOM_TYPE chatRoomType = XH_CHATROOM_TYPE_GLOBAL_PUBLIC;
	XH_LIVE_TYPE channelType = XH_LIVE_TYPE_GLOBAL_PUBLIC;
	g_pXHLiveManager = new XHLiveManager(pUserManager);

	list<ChatroomInfo> listData;
	string strLiveId = "";

	char strListType[10] = { 0 };
	sprintf(strListType, "%d,%d", CHATROOM_LIST_TYPE_LIVE, CHATROOM_LIST_TYPE_LIVE_PUSH);

	if(pUserManager->m_bAEventCenterEnable)
	{
		CInterfaceUrls::demoQueryList(strListType, listData);
	}
	else
	{
		XHLiveManager::getLiveList(pUserManager, "", strListType, listData);
	}
	list<ChatroomInfo>::iterator iter = listData.begin();

	for (; iter != listData.end(); iter++)
	{
		if(iter->m_strCreaterId == pUserManager->m_ServiceParam.m_strUserId && strName == iter->m_strName)
		{
			strLiveId = iter->m_strRoomId;
			break;
		}
	}
	if(strLiveId == "")
	{
		strLiveId = g_pXHLiveManager->createLive(strName, chatRoomType, channelType);
		if(strLiveId != "")
		{
			string strInfo = "{\"id\":\"";
			strInfo += strLiveId;
			strInfo += "\",\"creator\":\"";
			strInfo += pUserManager->m_ServiceParam.m_strUserId;
			strInfo += "\",\"name\":\"";
			strInfo += strName;
			strInfo += "\"}";
			if(pUserManager->m_bAEventCenterEnable)
			{
				printf("%s\n", (char*)strInfo.c_str());
				CInterfaceUrls::demoSaveToList(pUserManager->m_ServiceParam.m_strUserId, CHATROOM_LIST_TYPE_LIVE, strLiveId, strInfo);
			}
			else
			{
				g_pXHLiveManager->saveToList(pUserManager->m_ServiceParam.m_strUserId, CHATROOM_LIST_TYPE_LIVE, strLiveId, strInfo);
			}
		}
	}

	if(strLiveId != "")
	{
		g_pXHLiveManager->m_Param.videoParam.w = 640;
		g_pXHLiveManager->m_Param.videoParam.h = 480;
		g_pXHLiveManager->m_Param.videoParam.fps = 15;
		g_pXHLiveManager->m_Param.videoParam.bitrate = 1024;
		bRet = g_pXHLiveManager->startLive(strLiveId, true);
		if(bRet)
		{
			while (true)
			{
				usleep( 800 );
			}
		}
		else
		{
			printf("startLive failed \n");
		}	
	}
	else
	{
		printf("createLive failed \n");
	}	
 }
