package pers.ui.proxy.options;

public class IPMatch {
	 public static boolean isInRange(String network, String mask) {
	        String[] networkips = network.split("\\.");
	        int ipAddr = (Integer.parseInt(networkips[0]) << 24)
	                | (Integer.parseInt(networkips[1]) << 16)
	                | (Integer.parseInt(networkips[2]) << 8)
	                | Integer.parseInt(networkips[3]);
	        int type = Integer.parseInt(mask.replaceAll(".*/", ""));
	        int mask1 = 0xFFFFFFFF << (32 - type);
	        String maskIp = mask.replaceAll("/.*", "");
	        String[] maskIps = maskIp.split("\\.");
	        int cidrIpAddr = (Integer.parseInt(maskIps[0]) << 24)
	                | (Integer.parseInt(maskIps[1]) << 16)
	                | (Integer.parseInt(maskIps[2]) << 8)
	                | Integer.parseInt(maskIps[3]);

	        return (ipAddr & mask1) == (cidrIpAddr & mask1);
	    }
}
