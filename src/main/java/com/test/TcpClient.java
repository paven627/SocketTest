package com.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpClient {

	//
	static byte[] arr = new byte[] {
			// 非满长度IP
			// 80, 82, 79, 88, 89, 32, 84, 67, 80, 52, 32, 49, 57, 50, 46, 49,
			// 54, 56, 46, 52, 51,
			// 46, 49, 57, 52, 32, 49, 57, 50, 46, 49, 54, 56, 46, 49, 46, 49,
			// 56, 49, 32, 54, 48, 57, 49, 53, 32, 56, 49,
			// 56, 49, 13, 10,

			// 满长度IP
			// 80, 82, 79, 88, 89, 32, 84, 67, 80, 52, 32, 50, 53, 53, 46, 50,
			// 53, 53, 46, 50, 53, 53, 46, 50, 53, 53, 32,
			// 50, 53, 53, 46, 50, 53, 53, 46, 50, 53, 53, 46, 50, 53, 53, 32,
			// 54, 53, 53, 51, 53, 32, 54, 53, 53, 51, 53,
			// 13, 10,

			// ip6地址
			// 80,82,79,88,89,32,84,67,80,54,32,102,102,102,102,58,102,46,46,46,102,58,102,102,102,102,32,102,102,102,102,58,102,46,46,46,102,58,102,102,102,102,32,54,53,53,51,53,32,54,53,53,51,53,13,10,

			// unknown
			// 80,82,79,88,89,32,85,78,75,78,79,87,78,13,10,
			// 最长
			// 80,82,79,88,89,32,85,78,75,78,79,87,78,32,102,102,102,102,58,102,46,46,46,102,58,102,102,102,102,32,102,102,102,102,58,102,46,46,46,102,58,102,102,102,102,32,54,53,53,51,53,32,54,53,53,51,53,13,10,

			0, 10, 11, 97, 49, 48, 48, 48, 48, 48, 51, 48, 48, 49, 16, 1, 26, -10, 1, 8, -121, -85, -125, -72, 2, 16,
			-71, -108, 1, 32, -48, -90, -31, -33, 3, 40, -128, 5, 48, -16, 8, 56, 1, 72, -88, 70, 80, 2, 90, 5, 54, 46,
			49, 46, 48, 98, 14, 73, 80, 72, 79, 78, 69, 32, 54, 83, 32, 80, 76, 85, 83, 106, 36, 53, 70, 52, 53, 52, 54,
			67, 51, 45, 54, 56, 48, 69, 45, 52, 70, 55, 49, 45, 57, 51, 65, 70, 45, 51, 69, 65, 66, 67, 68, 54, 51, 55,
			48, 49, 56, 114, 3, 119, 109, 97, -112, 1, 21, -104, 1, -41, 2, -96, 1, 1, -88, 1, 1, -78, 1, 20, 99, 111,
			109, 46, 109, 111, 106, 105, 46, 77, 111, 106, 105, 87, 101, 97, 116, 104, 101, 114, -70, 1, 108, 77, 111,
			122, 105, 108, 108, 97, 47, 53, 46, 48, 32, 40, 105, 80, 104, 111, 110, 101, 59, 32, 67, 80, 85, 32, 105,
			80, 104, 111, 110, 101, 32, 79, 83, 32, 57, 95, 51, 32, 108, 105, 107, 101, 32, 77, 97, 99, 32, 79, 83, 32,
			88, 41, 32, 65, 112, 112, 108, 101, 87, 101, 98, 75, 105, 116, 47, 54, 48, 49, 46, 49, 46, 52, 54, 32, 40,
			75, 72, 84, 77, 76, 44, 32, 108, 105, 107, 101, 32, 71, 101, 99, 107, 111, 41, 32, 77, 111, 98, 105, 108,
			101, 47, 49, 51, 69, 50, 51, 48, -54, 1, 1, 50, 32, 1, 45, 38, -5, -24, 66, 53, 5, -28, 31, 66, 56, 1, 64,
			0, 80, 2, 88, 0, 96, 1, 104, 3, 120, 1, -126, 1, 32, 100, 98, 101, 57, 48, 100, 97, 99, 56, 101, 54, 99,
			100, 48, 102, 97, 98, 102, 54, 97, 50, 99, 55, 99, 102, 53, 49, 98, 98, 99, 55, 102, -128, -128, -128, -128,
			-128 };

	public static void main(String[] args) throws IOException, InterruptedException {

		// 3次TCP连接，每个连接发送2个请求数据
		for (int i = 0; i < 1; i++) { 
			Socket socket = null;
			OutputStream out = null;
			InputStream inputStream = null;
			try {

//				 socket = new Socket("localhost", 8080);
				socket = new Socket("127.0.0.1", 8181);
				// socket = new Socket("127.0.0.1", );
				// socket = new Socket("60.205.230.151", 8080);
//				socket = new Socket("60.205.230.151", 8181);
//				 socket = new Socket("192.168.1.181", 8181);
				out = socket.getOutputStream();

				out.write(arr);
				out.flush();

				inputStream = socket.getInputStream();
				System.out.println(inputStream.available());
//				byte[] by = toByteArray(inputStream);
//				for (byte b : by) {
//					System.out.print((char) b);
//				}

				byte[] b = new byte[inputStream.available()];
				inputStream.read(b, 0, inputStream.available());
				for (byte c : b) {
					System.out.print((char) c);
				}

			} finally {
				// 关闭连接
				out.close();
				inputStream.close();
				socket.close();
			}

		}
	}

	public static byte[] toByteArray(InputStream input) throws IOException {
		byte[] buffer1 = new byte[5];
		input.read(buffer1, 0, 5);
		int length = byteArrayToInt(buffer1);
		byte[] buffer = new byte[length];
		// System.out.println("buffer.length before->" + buffer.length);
		// System.out.println("input available->" + input.available());
		// n = input.read(buffer);
		int readbytes = 0;
		while (readbytes < length) {
			int read = input.read(buffer, readbytes, length - readbytes);
			if (read == -1) {
				break;
			}
			readbytes += read;
		}
		return buffer;
		// System.out.println("buffer.length after->"+n);
		// output.write(buffer, 0, n);
		// return output.toByteArray();
	}

	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		for (int i = 1; i < 5; i++) {
			int shift = (5 - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;
		}
		return value;
	}
}