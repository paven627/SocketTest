package com.test;

public class StrToByte {
	public static void main(String[] args) {
//		String s = "PROXY TCP4 255.255.255.255 255.255.255.255 65535 65535\r\n";
//		String s = "PROXY TCP6 ffff:f...f:ffff ffff:f...f:ffff 65535 65535\r\n";
//		String s = "PROXY UNKNOWN\r\n";
		String s = "PROXY UNKNOWN ffff:f...f:ffff ffff:f...f:ffff 65535 65535\r\n";
		
		byte[] bytes = s.getBytes();
		for (byte b : bytes) {
			System.out.print((int) b +",");
		}
	}
}
