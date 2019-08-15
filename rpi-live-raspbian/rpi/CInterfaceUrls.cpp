#include "CInterfaceUrls.h"
#include "HttpRequestTool.h"
#include "cJSON.h"
#include <stdio.h>
CInterfaceUrls::CInterfaceUrls()
{
}


CInterfaceUrls::~CInterfaceUrls()
{
}

unsigned char CInterfaceUrls::ToHex(unsigned char x)
{
	return  x > 9 ? x + 55 : x + 48;
}

unsigned char CInterfaceUrls::FromHex(unsigned char x)
{
	unsigned char y;
	if (x >= 'A' && x <= 'Z') y = x - 'A' + 10;
	else if (x >= 'a' && x <= 'z') y = x - 'a' + 10;
	else if (x >= '0' && x <= '9') y = x - '0';
	else
	{
	}
	return y;
}

std::string CInterfaceUrls::UrlEncode(const std::string& str)
{
	std::string strTemp = "";
	size_t length = str.length();
	for (size_t i = 0; i < length; i++)
	{
		if (isalnum((unsigned char)str[i]) ||
			(str[i] == '-') ||
			(str[i] == '_') ||
			(str[i] == '.') ||
			(str[i] == '~'))
			strTemp += str[i];
		else if (str[i] == ' ')
			strTemp += "+";
		else
		{
			strTemp += '%';
			strTemp += ToHex((unsigned char)str[i] >> 4);
			strTemp += ToHex((unsigned char)str[i] % 16);
		}
	}
	return strTemp;
}

std::string CInterfaceUrls::UrlDecode(const std::string& str)
{
	std::string strTemp = "";
	size_t length = str.length();
	for (size_t i = 0; i < length; i++)
	{
		if (str[i] == '+') strTemp += ' ';
		else if (str[i] == '%')
		{
			unsigned char high = FromHex((unsigned char)str[++i]);
			unsigned char low = FromHex((unsigned char)str[++i]);
			strTemp += high * 16 + low;
		}
		else strTemp += str[i];
	}
	return strTemp;
}

void CInterfaceUrls::demoSaveToList(string userId, int listType, string id, string data)
{
	string url = "http://www.starrtc.com/aec/list/save.php";
	url = url + "?userId=" + userId + "&listType=";
	char buf[256] = { 0 };
	sprintf(buf, "%d", listType);
	url = url + buf;
	url = url + "&roomId=" + id;

	data = CInterfaceUrls::UrlEncode(data);

	url = url + "&data=" + data;
	
	string strData 			= "";
	std::string strVal  	= "";
	std::string strErrInfo  = "";

	int ret = libcurl_post(url.c_str(), strData.c_str(), strVal, strErrInfo);
}

void CInterfaceUrls::demoDeleteFromList(string userId, int listType, string id)
{
	string url = "http://www.starrtc.com/aec/list/del.php";
	url = url + "?userId=" + userId + "&listType=";
	char buf[256] = { 0 };
	sprintf(buf, "%d", listType);
	url = url + buf;
	url = url + "&roomId=" + id;

	string strData 			= "";
	std::string strVal  	= "";
	std::string strErrInfo  = "";

	int ret = libcurl_post(url.c_str(), strData.c_str(), strVal, strErrInfo);
}

void CInterfaceUrls::demoQueryList(string listType, list<ChatroomInfo>& listData)
{
	string url = "http://www.starrtc.com/aec/list/query.php";

	string strData = "listTypes=";
	strData = strData + listType;

	std::string strVal  	= "";
	std::string strErrInfo  = "";

	int ret = libcurl_post(url.c_str(), strData.c_str(), strVal, strErrInfo);

	cJSON* root = cJSON_Parse(strVal.c_str());     
    if (!root)
	{
        printf("Error before: [%s]\n",cJSON_GetErrorPtr());
		return ;
    }
	cJSON *itemStatus = cJSON_GetObjectItem(root, "status");

	if(itemStatus == NULL)
	{
		printf("get status err: [%s]\n",cJSON_GetErrorPtr());
		return ;
	}
	if(itemStatus->valueint != 1)
	{
		printf("status is 0  failed\n");
		return ;
	}
                
	cJSON *itemData = cJSON_GetObjectItem(root, "data");

	int arraysize = cJSON_GetArraySize(itemData); 

	cJSON *objread = NULL;
	for(int i = 0; i<arraysize; i++)
	{
		objread = cJSON_GetArrayItem(itemData,i);
		if(objread != NULL)
		{
			cJSON *item = cJSON_GetObjectItem(objread, "data");
			if(item != NULL)
			{
				string strData = item->valuestring;
				strData = CInterfaceUrls::UrlDecode(strData);
				cJSON* root1 = cJSON_Parse(strData.c_str()); 
				if(root1 != NULL)
				{
					ChatroomInfo chatroomInfo;
					cJSON *itemId = cJSON_GetObjectItem(root1, "id");
					if(itemId != NULL)
					{
						chatroomInfo.m_strRoomId = itemId->valuestring;
					}

					cJSON *itemName = cJSON_GetObjectItem(root1, "name");
					if(itemName != NULL)
					{
						chatroomInfo.m_strName = itemName->valuestring;
					}

					cJSON *itemCreator = cJSON_GetObjectItem(root1, "creator");
					if(itemCreator != NULL)
					{
						chatroomInfo.m_strCreaterId = itemCreator->valuestring;
					}
					listData.push_back(chatroomInfo);
				}  
				
			}
		}
	}
}


