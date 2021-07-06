package com.junliang.boot;

import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;

/**
 * TODO desc
 * @author junlinag.li
 * @date 2021/7/2
 */
public class test {

	public static void main(String[] args) {
		// String fileUrl = "https://e-archives-dev.oss-cn-shanghai.aliyuncs.com/1367036488438124546/IMG/9ad115c7-8755-4e96-adeb-bebf16e93bd0-a1ec08fa513d269742a02aab55fbb2fb4216d8d0.jpg?Expires=1625202897&OSSAccessKeyId=LTAI4GCc2n26BfFhe1kEixQw&Signature=9Ltp6pBNCscLtAlKyxx%2B2qYyIO4%3D";
		// System.out.println(fileUrl);
		//
		// //移除参数
		// int index = fileUrl.lastIndexOf("?");
		// if (index != -1) {
		// 	fileUrl = fileUrl.substring(0, index);
		// }
		// if (index != -1) {
		// 	index = fileUrl.lastIndexOf("/");
		// 	fileUrl = fileUrl.substring(index + 1);
		// }
		//
		// System.out.println(fileUrl);

		/*
		 *   "fileInfoPath":[
		 * "/[custFormValues]/[attachmentImages]/fileURL",
		 * "/pdfFileURL"
		 * ]
		 */

		String urlMappingPath = "/[custFormValues]/[attachmentImages]/fileURL";
		String[] split = urlMappingPath.split("/");

		// StringUtils.

		System.out.println(split.length);
		for (int i = 0; i < split.length; i++) {
			System.out.println(split[i]);
			String str =  split[i];
			if ( str.startsWith("[")&&str.endsWith("]")){

			}

			if (i == split.length -1)
				System.out.println(split[i]);
		}

	}
}
