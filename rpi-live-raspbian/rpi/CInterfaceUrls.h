#pragma once
#include <string>
#include <list>
using namespace std;
#include "ChatroomInfo.h"

using namespace std;
class CInterfaceUrls
{
public:
	CInterfaceUrls();
	virtual ~CInterfaceUrls();
	static unsigned char ToHex(unsigned char x);
	static unsigned char FromHex(unsigned char x);
	static std::string UrlEncode(const std::string& str);
	static std::string UrlDecode(const std::string& str);

	static void demoSaveToList(string userId, int listType, string id, string data);

	static void demoDeleteFromList(string userId, int listType, string id);

	static void demoQueryList(string listType, list<ChatroomInfo>& listData);
};
