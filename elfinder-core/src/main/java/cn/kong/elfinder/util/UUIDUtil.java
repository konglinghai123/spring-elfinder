package cn.kong.elfinder.util;

import java.util.UUID;

public class UUIDUtil {

	public static String genUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
