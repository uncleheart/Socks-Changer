#include "pers_dll_infc_JavaInvokeCPlus.h"

#include "SocksServer.hpp"
#include <windows.h>
using namespace std;



#ifdef _X86_
extern "C" { int _afxForceUSRDLL; }
#else
extern "C" { int __afxForceUSRDLL; }
#endif


boost::shared_ptr<socks::socks_server> pSocksServer;

static JavaVM *gJavaVM;
jobject gJavaObj;


jmethodID midNoticeNewCon;


jmethodID midNoticeNewDatafromDst;
jmethodID midNoticeNewDatafromSource;


jmethodID midNoticeClose;

jclass clsNewConData;
jmethodID midNewConData;


ReturnType SetMethodId(JNIEnv *env, jmethodID &mid, jclass &cls, const char * className, const char * methodName, const char * methodSig)
{
	//JNIEnv *env;
	//gJavaVM->AttachCurrentThread(((void **)&env), NULL);

	cls = (env)->FindClass(className);
	if (!cls) {
		printf("Class %s not found\n", className);
		return ERROR_SETTING;
	}
	mid = env->GetMethodID(cls, methodName, methodSig);

	if (!mid) {
		printf("Method %s of Class %s not found\n", methodName, methodSig);
		return ERROR_SETTING;
	}
	//gJavaVM->DetachCurrentThread();
	return SUCCESS;

}
//
//ReturnType SetNoticeNewCon(char * className, char * methodName, char * methodSig)
//{
//	jclass clsNoticeNewCon;
//	JNIEnv *env;
//	gJavaVM->AttachCurrentThread(((void **)&env), NULL);
//
//	clsNoticeNewCon = (env)->FindClass(className);
//	//cls = (env)->FindClass("com/uncleheart/test/JavaInvokeCPlus");
//	if (!clsNoticeNewCon) {
//		printf("Class %s not found\n", className);
//		return ERROR_SETTING;
//	}
//
//	//mid = (env)->GetStaticMethodID(cls, "TestFromJava", "()I");//"([Ljava/lang/String;)V"
//	midNoticeNewCon = env->GetMethodID(clsNoticeNewCon, methodName, methodSig);//"([Ljava/lang/String;)V"
//
//	if (!midNoticeNewCon) {
//		printf("Method %s of Class %s not found\n", methodName, methodSig);
//		return ERROR_SETTING;
//	}
//	gJavaVM->DetachCurrentThread();
//	return SUCCESS;
//}
//
//ReturnType SetNoticeNewDatafromDst(char * className, char * methodName, char * methodSig)
//{
//	jclass cls;
//	JNIEnv *env;
//	gJavaVM->AttachCurrentThread(((void **)&env), NULL);
//	cls = (env)->FindClass(className);
//	//cls = (env)->FindClass("com/uncleheart/test/JavaInvokeCPlus");
//	if (!cls) {
//		printf("Class %s not found\n", className);
//		return ERROR_SETTING;
//	}
//
//	//mid = (env)->GetStaticMethodID(cls, "TestFromJava", "()I");//"([Ljava/lang/String;)V"
//	midNoticeNewDatafromDst = (env)->GetMethodID(cls, methodName, methodSig);//"([Ljava/lang/String;)V"
//
//	if (!midNoticeNewDatafromDst) {
//		printf("Method %s of Class %s not found\n", methodName, methodSig);
//		return ERROR_SETTING;
//	}
//	gJavaVM->DetachCurrentThread();
//	return SUCCESS;
//}
//ReturnType SetNoticeNewDatafromSource(char * className, char * methodName, char * methodSig)
//{
//	jclass cls;
//	JNIEnv *env;
//	gJavaVM->AttachCurrentThread(((void **)&env), NULL);
//	cls = (env)->FindClass(className);
//	//cls = (env)->FindClass("com/uncleheart/test/JavaInvokeCPlus");
//	if (!cls) {
//		printf("Class %s not found\n", className);
//		return ERROR_SETTING;
//	}
//
//	//mid = (env)->GetStaticMethodID(cls, "TestFromJava", "()I");//"([Ljava/lang/String;)V"
//	midNoticeNewDatafromSource = (env)->GetMethodID(cls, methodName, methodSig);//"([Ljava/lang/String;)V"
//
//	if (!midNoticeNewDatafromSource) {
//		printf("Method %s of Class %s not found\n", methodName, methodSig);
//		return ERROR_SETTING;
//	}
//	gJavaVM->DetachCurrentThread();
//	return SUCCESS;
//}
//ReturnType SetNoticeClose(char * className, char * methodName, char * methodSig)
//{
//	jclass clsNoticeClose;
//	JNIEnv *env;
//	gJavaVM->AttachCurrentThread(((void **)&env), NULL);
//	clsNoticeClose = (env)->FindClass(className);
//	//cls = (env)->FindClass("com/uncleheart/test/JavaInvokeCPlus");
//	if (!clsNoticeClose) {
//		printf("Class %s not found\n", className);
//		return ERROR_SETTING;
//	}
//
//	//mid = (env)->GetStaticMethodID(cls, "TestFromJava", "()I");//"([Ljava/lang/String;)V"
//	midNoticeClose = (env)->GetMethodID(clsNoticeClose, methodName, methodSig);//"([Ljava/lang/String;)V"
//
//	if (!midNoticeClose) {
//		printf("Method %s of Class %s not found\n", methodName, methodSig);
//		return ERROR_SETTING;
//	}
//	gJavaVM->DetachCurrentThread();
//	return SUCCESS;
//}
//
//ReturnType SetNewConData(char * className, char * methodName, char * methodSig)
//{
//
//	JNIEnv *env;
//	gJavaVM->AttachCurrentThread(((void **)&env), NULL);
//	clsNewConData = (env)->FindClass(className);
//	//cls = (env)->FindClass("com/uncleheart/test/JavaInvokeCPlus");
//	if (!clsNewConData) {
//		printf("Class %s not found\n", className);
//		return ERROR_SETTING;
//	}
//	clsNewConData=(jclass)env->NewGlobalRef(clsNewConData);  //全局引用  
//
//	//mid = (env)->GetStaticMethodID(cls, "TestFromJava", "()I");//"([Ljava/lang/String;)V"
//	midNewConData = (env)->GetMethodID(clsNewConData, methodName, methodSig);//"([Ljava/lang/String;)V"
//
//	if (!midNoticeClose) {
//		printf("Method %s of Class %s not found\n", methodName, methodSig);
//		return ERROR_SETTING;
//	}
//	jstring jstr = env->NewStringUTF("test");
//	jobject newConData = env->NewObject(clsNewConData, midNewConData, (jint)16541, (jint)1, jstr, jstr, (jint)2, jstr, jstr, (jint)2);
//
//	gJavaVM->DetachCurrentThread();
//	return SUCCESS;
//}

ReturnType NoticeNewCon(
	_In_ IndexType index,
	_In_ uint8_t protocolType,
	_In_ std::string SourceHostname,
	_In_ std::string SourceIPAddress,
	_In_ uint16_t SourcePort,
	_In_  _Out_ std::string &DstHostname,
	_In_  _Out_ std::string &DstIPAddress,
	_In_  _Out_ uint16_t &DstPort
	)
{
	/////////////////////////////////////////
	//待修改
	// 
	JNIEnv *env;
	gJavaVM->AttachCurrentThread(((void **)&env), NULL);

	jstring jDstHostname = env->NewStringUTF(DstHostname.c_str());
	jstring jDstIPAddress = env->NewStringUTF(DstIPAddress.c_str());

	jstring jSourceHostname = env->NewStringUTF(SourceHostname.c_str());
	jstring jSourceIPAddress = env->NewStringUTF(SourceIPAddress.c_str());

	//jint res = env->CallIntMethod(gJavaObj, midNoticeNewCon, (jint)index, jHostname, jAddress, (jint)port,(jint) protocolType);

	//jobject newConData = env->NewObject(clsNewConData, midNewConData, (jint)index, (jint)protocolType, jHostname, jAddress, (jint)port, jHostname, jAddress, (jint)port);


	//获取java的Person构造方法id---构造函数的函数名为<init>，返回值为void  
	jobject objNewConData = env->NewObject(clsNewConData, midNewConData, (jint)index, (jint)protocolType, jSourceHostname, jSourceIPAddress, (jint)SourcePort, jDstHostname, jDstIPAddress, (jint)DstPort);

	ReturnType res = (ReturnType)env->CallIntMethod(gJavaObj, midNoticeNewCon, objNewConData);
	jfieldID fidDstHostname = env->GetFieldID(clsNewConData, "DstHostname", "Ljava/lang/String;");
	jfieldID fidDstIPAddress = env->GetFieldID(clsNewConData, "DstIPAddress", "Ljava/lang/String;");
	jfieldID fidDstPort = env->GetFieldID(clsNewConData, "DstPort", "I");

	if (res&CHANGE_HOST_NAME)
	{
		fidDstHostname = env->GetFieldID(clsNewConData, "DstHostname", "Ljava/lang/String;");
		jDstHostname = (jstring)env->GetObjectField(objNewConData, fidDstHostname);
	}
	if (res&CHANGE_IP_ADDRESS)
	{
		fidDstIPAddress = env->GetFieldID(clsNewConData, "DstIPAddress", "Ljava/lang/String;");
		jDstIPAddress = (jstring)env->GetObjectField(objNewConData, fidDstIPAddress);
	}

	if (res&CHANGE_PORT)
	{
		fidDstPort = env->GetFieldID(clsNewConData, "DstPort", "I");
		DstPort = (int)env->GetIntField(objNewConData, fidDstPort);
	}


	gJavaVM->DetachCurrentThread();
	return res;
}
ReturnType NoticeNewDatafromDst(IndexType index, ProxyDataStatus type, std::string &data)
{
	/////////////////////////////////////////
	//待修改
	//
	JNIEnv *env;
	gJavaVM->AttachCurrentThread((void **)&env, NULL);
	//jstring jData = env->NewStringUTF(data.c_str());
	jbyteArray jbyteArray = env->NewByteArray(data.size());
	env->SetByteArrayRegion(jbyteArray, 0, data.size(), (jbyte*)data.c_str());
	jint res = env->CallIntMethod(gJavaObj, midNoticeNewDatafromDst, (jint)index, (jint)type, jbyteArray);
	gJavaVM->DetachCurrentThread();
	return (ReturnType)res;
}
ReturnType NoticeNewDatafromSource(IndexType index, ProxyDataStatus type, std::string &data)
{
	/////////////////////////////////////////
	//待修改
	//
	JNIEnv *env;
	gJavaVM->AttachCurrentThread((void **)&env, NULL);
	//jstring jData = env->NewStringUTF(data.c_str());
	jbyteArray jbyteArray = env->NewByteArray(data.size());
	env->SetByteArrayRegion(jbyteArray, 0, data.size(), (jbyte*)data.c_str());
	jint res = env->CallIntMethod(gJavaObj, midNoticeNewDatafromSource, (jint)index, (jint)type, jbyteArray);
	gJavaVM->DetachCurrentThread();
	return (ReturnType)res;
}
ReturnType NoticeNewClose(IndexType index)
{
	/////////////////////////////////////////
	//待修改
	// 
	JNIEnv *env;
	gJavaVM->AttachCurrentThread(((void **)&env), NULL);

	jint res = env->CallIntMethod(gJavaObj, midNoticeClose, (jint)index);
	gJavaVM->DetachCurrentThread();
	return (ReturnType)res;
}

/*
* Class:     pers_dll_infc_JavaInvokeCPlus
* Method:    Init
* Signature: ()I
*/
JNIEXPORT jint JNICALL Java_pers_dll_infc_JavaInvokeCPlus_Init
(JNIEnv *env, jobject jobj)
{
	//env->GetJavaVM(&gJavaVM);
	gJavaObj = env->NewGlobalRef(jobj);
	return SUCCESS;
}

/*
* Class:     pers_dll_infc_JavaInvokeCPlus
* Method:    InitNewCon
* Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
*/
JNIEXPORT jint JNICALL Java_pers_dll_infc_JavaInvokeCPlus_InitNewCon
(JNIEnv *env, jobject jobj, jstring jClassName, jstring jMethodName, jstring jMethodSig)
{

	const char * ClassName, *MethodName, *MethodSig;
	jboolean isCopy1, isCopy2, isCopy3;
	ClassName = env->GetStringUTFChars(jClassName, &isCopy1);
	MethodName = env->GetStringUTFChars(jMethodName, &isCopy2);
	MethodSig = env->GetStringUTFChars(jMethodSig, &isCopy3);

	jclass cls = nullptr;
	SetMethodId(env, midNoticeNewCon, cls, ClassName, MethodName, MethodSig);
	//SetNoticeNewCon((char*)NewConClassName, (char*)NewConMethodName, (char*)NewConSig);

	env->ReleaseStringUTFChars(jClassName, ClassName);
	env->ReleaseStringUTFChars(jMethodName, MethodName);
	env->ReleaseStringUTFChars(jMethodSig, MethodSig);
	return SUCCESS;
}

/*
* Class:     pers_dll_infc_JavaInvokeCPlus
* Method:    InitNewDatafromDst
* Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
*/
JNIEXPORT jint JNICALL Java_pers_dll_infc_JavaInvokeCPlus_InitNewDatafromDst
(JNIEnv *env, jobject jobj, jstring jClassName, jstring jMethodName, jstring jMethodSig)
{


	const char *ClassName, *MethodName, *MethodSig;
	jboolean isCopy1, isCopy2, isCopy3;

	ClassName = env->GetStringUTFChars(jClassName, &isCopy1);
	MethodName = env->GetStringUTFChars(jMethodName, &isCopy2);
	MethodSig = env->GetStringUTFChars(jMethodSig, &isCopy3);

	jclass cls = nullptr;
	SetMethodId(env, midNoticeNewDatafromDst, cls, ClassName, MethodName, MethodSig);
	//SetNoticeNewDatafromDst((char*)ClassName, (char*)MethodName, (char*)Sig);

	env->ReleaseStringUTFChars(jClassName, ClassName);
	env->ReleaseStringUTFChars(jMethodName, MethodName);
	env->ReleaseStringUTFChars(jMethodSig, MethodSig);
	return SUCCESS;
}

/*
* Class:     pers_dll_infc_JavaInvokeCPlus
* Method:    InitNewDatafromSource
* Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
*/
JNIEXPORT jint JNICALL Java_pers_dll_infc_JavaInvokeCPlus_InitNewDatafromSource
(JNIEnv *env, jobject jobj, jstring jClassName, jstring jMethodName, jstring jMethodSig)
{
	const char *ClassName, *MethodName, *MethodSig;
	jboolean isCopy1, isCopy2, isCopy3;

	ClassName = env->GetStringUTFChars(jClassName, &isCopy1);
	MethodName = env->GetStringUTFChars(jMethodName, &isCopy2);
	MethodSig = env->GetStringUTFChars(jMethodSig, &isCopy3);

	jclass cls = nullptr;
	SetMethodId(env, midNoticeNewDatafromSource, cls, ClassName, MethodName, MethodSig);
	//SetNoticeNewDatafromSource((char*)ClassName, (char*)MethodName, (char*)Sig);

	env->ReleaseStringUTFChars(jClassName, ClassName);
	env->ReleaseStringUTFChars(jMethodName, MethodName);
	env->ReleaseStringUTFChars(jMethodSig, MethodSig);
	return SUCCESS;
}


/*
* Class:     pers_dll_infc_JavaInvokeCPlus
* Method:    InitClose
* Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
*/
JNIEXPORT jint JNICALL Java_pers_dll_infc_JavaInvokeCPlus_InitClose
(JNIEnv *env, jobject jobj, jstring jClassName, jstring jMethodName, jstring jMethodSig)
{
	const char *ClassName, *MethodName, *MethodSig;
	jboolean isCopy1, isCopy2, isCopy3;

	ClassName = env->GetStringUTFChars(jClassName, &isCopy1);
	MethodName = env->GetStringUTFChars(jMethodName, &isCopy2);
	MethodSig = env->GetStringUTFChars(jMethodSig, &isCopy3);

	jclass cls = nullptr;
	SetMethodId(env, midNoticeClose, cls, ClassName, MethodName, MethodSig);

	//SetNoticeClose((char*)NewDataClassName, (char*)NewDataMethodName, (char*)NewDataSig);

	env->ReleaseStringUTFChars(jClassName, ClassName);
	env->ReleaseStringUTFChars(jMethodName, MethodName);
	env->ReleaseStringUTFChars(jMethodSig, MethodSig);
	return SUCCESS;
}


/*
* Class:     pers_dll_infc_JavaInvokeCPlus
* Method:    InitNewConData
* Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
*/
JNIEXPORT jint JNICALL Java_pers_dll_infc_JavaInvokeCPlus_InitNewConData
(JNIEnv *env, jobject jobj, jstring jClassName, jstring jMethodName, jstring jMethodSig)
{
	const char *ClassName, *MethodName, *MethodSig;
	jboolean isCopy1, isCopy2, isCopy3;

	ClassName = env->GetStringUTFChars(jClassName, &isCopy1);
	MethodName = env->GetStringUTFChars(jMethodName, &isCopy2);
	MethodSig = env->GetStringUTFChars(jMethodSig, &isCopy3);


	SetMethodId(env, midNewConData, clsNewConData, ClassName, MethodName, MethodSig);
	clsNewConData = (jclass)env->NewGlobalRef(clsNewConData);  //全局引用  
//SetNewConData((char*)ClassName, (char*)MethodName, (char*)Sig);

	env->ReleaseStringUTFChars(jClassName, ClassName);
	env->ReleaseStringUTFChars(jMethodName, MethodName);
	env->ReleaseStringUTFChars(jMethodSig, MethodSig);
	return SUCCESS;
}

/*
* Class:     pers_dll_infc_JavaInvokeCPlus
* Method:    SuspendRecv
* Signature: (IZZ)Z
*/
JNIEXPORT jboolean JNICALL Java_pers_dll_infc_JavaInvokeCPlus_SuspendRecv
(JNIEnv *env, jobject jobj, jint tIndex, jboolean tBSuspendRecvfromDst, jboolean tBSuspendRecvfromSource)
{
	return (jboolean)pSocksServer->SuspendRecv((uint16_t)tIndex, (bool)tBSuspendRecvfromDst, (bool)tBSuspendRecvfromSource);
}


/*
* Class:     pers_dll_infc_JavaInvokeCPlus
* Method:    SetSocket
* Signature: (II)I
*/
JNIEXPORT jint JNICALL Java_pers_dll_infc_JavaInvokeCPlus_SetInterceptStatus
(JNIEnv *env, jobject jobj, jint tIndex , jint status)
{
	pSocksServer->SetInterceptStatus((int)tIndex, (int8_t)status);
	return 0;
}

/*
* Class:     pers_dll_infc_JavaInvokeCPlus
* Method:    AddSendtoDstBuffer
* Signature: (I[B)I
*/
JNIEXPORT jint JNICALL Java_pers_dll_infc_JavaInvokeCPlus_AddSendtoDstBuffer
(JNIEnv *env, jobject jobj, jint index, jbyteArray data)
{
	jbyte *jby = env->GetByteArrayElements(data, NULL);
	std::string t;
	t.assign((char *)jby, (int)env->GetArrayLength(data));

	pSocksServer->AddSendtoDstBuffer(index, t);

	env->ReleaseByteArrayElements(data, jby, JNI_COMMIT);

	return SUCCESS;
}



/*
* Class:     pers_dll_infc_JavaInvokeCPlus
* Method:    AddSendtoSourceBuffer
* Signature: (I[B)I
*/
JNIEXPORT jint JNICALL Java_pers_dll_infc_JavaInvokeCPlus_AddSendtoSourceBuffer
(JNIEnv *env, jobject jobj, jint index, jbyteArray data)
{
	jbyte *jby = env->GetByteArrayElements(data, NULL);
	std::string t;
	t.assign((char *)jby, (int)env->GetArrayLength(data));

	pSocksServer->AddSendtoSourceBuffer(index, t);

	env->ReleaseByteArrayElements(data, jby, JNI_COMMIT);

	return SUCCESS;
}



JNIEXPORT jint JNICALL Java_pers_dll_infc_JavaInvokeCPlus_StartServer
(JNIEnv *env, jobject jobj, jstring jAddress, jint JPort)
{
	if (pSocksServer != nullptr)
	{
		return ERROR;
	}
	const char * IP;
	jboolean isCopy;
	IP = env->GetStringUTFChars(jAddress, &isCopy);
	std::string address(IP);
	env->ReleaseStringUTFChars(jAddress, IP);

	pSocksServer.reset(new socks::socks_server(address, (uint16_t)JPort, NoticeNewCon, NoticeNewDatafromDst, NoticeNewDatafromSource, NoticeNewClose));

	pSocksServer->Run();

	return 0;
}


/*
* Class:     pers_dll_infc_JavaInvokeCPlus
* Method:    StopServer
* Signature: ()I
*/
JNIEXPORT jint JNICALL Java_pers_dll_infc_JavaInvokeCPlus_StopServer
(JNIEnv *, jobject)
{
	if (pSocksServer != nullptr)
	{
		pSocksServer->CloseServer();
		return (jint)0;
	}
	return (jint)-1;
}

jint __stdcall JNI_OnLoad(JavaVM *vm, void *reserved)
{
	gJavaVM = vm;

	return JNI_VERSION_1_4;
}

extern "C"  BOOL APIENTRY DllMain(HANDLE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
		Dbg(printf("\nprocess dattach of dll");)
			pSocksServer = nullptr;
		break;
	case DLL_THREAD_ATTACH:
		Dbg(printf("\nthread attach of dll");)
			break;
	case DLL_THREAD_DETACH:
		Dbg(printf("\nthread detach of dll");)
			break;
	case DLL_PROCESS_DETACH:
		Dbg(printf("\nprocess detach of dll");)
			break;
	}
	return TRUE;
}














