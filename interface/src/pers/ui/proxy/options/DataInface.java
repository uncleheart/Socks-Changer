package pers.ui.proxy.options;

import pers.data.SocketInfo;
import pers.dll.infc.NewConData;

public interface DataInface {

    public byte[] Handle(int tIndex,byte[] srcData,SocketInfo tSocketInfo);

}	