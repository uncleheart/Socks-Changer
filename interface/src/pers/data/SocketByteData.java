package pers.data;



public class SocketByteData implements Cloneable  {
	public ProxyDataStatus proxyDataStatus;
	public	byte[] data;
	public SocketByteData(ProxyDataStatus tProxyDataStatus, byte[]tData)
	{
		proxyDataStatus=tProxyDataStatus;
		data=tData;
	}
	public Object clone() 
	  { 
	   Object o=null; 
	  try 
	   { 
	   o=(SocketByteData)super.clone();//Object 中的clone()识别出你要复制的是哪一个对象。 
	   } 
	  catch(CloneNotSupportedException e) 
	   { 
	    System.out.println(e.toString()); 
	   } 
	  return o; 
	  }  
}
