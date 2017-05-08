package com.bfmj.sdk.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * 字符串工具类
 * 
 * @author yanzw
 * @date 2014-4-30 上午9:44:17
 */
public class StringUtil {

	public static int countContentLength(String str) {
		int length = 0;
		str = filterHtml(str);
		final String target = "http://";
		final int targetLen = target.length();
		int begin = str.indexOf(target, 0);
		if (begin != -1) {
			while (begin != -1) {
				length += begin;
				if (begin + targetLen == str.length()) {
					str = str.substring(begin);
					break;
				}

				int i = begin + targetLen;
				char c = str.charAt(i);
				while (((c <= 'Z') && (c >= 'A')) || ((c <= 'z') && (c >= 'a'))
						|| ((c <= '9') && (c >= '0')) || (c == '_')
						|| (c == '.') || (c == '?') || (c == '/') || (c == '%')
						|| (c == '&') || (c == ':') || (c == '=') || (c == '-')) {
					i++;
					if (i < str.length()) {
						c = str.charAt(i);
					} else {
						i--;
						length--;
						break;
					}
				}

				length += 10;

				str = str.substring(i);
				begin = str.indexOf(target, 0);
			}

			length += str.length();
		} else {
			length = str.length();
		}

		return length;
	}

	public static boolean equals(String a, String b) {
		// return true if both string are null or the content equals
		return a == b || a.equals(b);
	}

	/**
	 * 将queryString解析为HashMap
	 * 
	 * @param qs
	 * @return
	 */
	public static HashMap<String, String> parseQueryString(String qs) {
		final HashMap<String, String> params = new HashMap<String, String>();
		final String[] array = qs.split("&");
		for (final String param : array) {
			final int index = param.indexOf("=");
			if (index <= 0) {
				continue;
			}
			params.put(param.substring(0, index), param.substring(index + 1));
		}
		return params;
	}

	/**
	 * 将unicode转为中文
	 * 
	 * @param s
	 * @return
	 */
	public static String unicodeToGBK(String s) {
		final String[] k = s.split(";");
		String rs = "";
		for (int i = 0; i < k.length; i++) {
			final int strIndex = k[i].indexOf("&#");
			String newstr = k[i];
			if (strIndex > -1) {
				String kstr = "";
				if (strIndex > 0) {
					kstr = newstr.substring(0, strIndex);
					rs = rs + kstr;
					newstr = newstr.substring(strIndex);
				}

				final int m = Integer.parseInt(newstr.replace("&#", ""));
				final char c = (char) m;
				rs = rs + c;
			} else {
				rs = rs + k[i];
			}
		}

		return rs;
	}

	/**
	 * 过滤文本中的特殊表情标签，重新替换为文本表情以便发送到服务器
	 * 
	 * @param content
	 * @return
	 */
	public static String filterHtml(String content) {

		final String cont = unicodeToGBK(content);

		final String pattern = "<img src=\"(\\[f\\d+\\])\"/?>";
		final StringBuilder sb = new StringBuilder();
		final Pattern p = Pattern.compile(pattern);
		final Matcher m = p.matcher(cont);
		int end = 0;
		while (m.find()) {
			final String face = m.group(1); // 表情文本
			final int start = m.start();
			sb.append(cont.substring(end, start)).append(face);
			end = m.end();
		}
		sb.append(cont.substring(end));
		return sb.toString().trim();
	}

	/**
	 * 获取字符串中第一个匹配的部分
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public static String getFirstMatch(String str, String regex) {
		final Pattern p = Pattern.compile(regex);
		final Matcher m = p.matcher(str);
		String temp = null;
		if (m.find()) {
			temp = m.group();
		}
		return temp;
	}

	/**
	 * 对字符串进行url编码
	 * 
	 * @return
	 */
	public static String urlencode(String src, String encoding) {
		try {
			return URLEncoder.encode(src, encoding);
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return src;
	}

	public static String urlencode(String src) {
		return urlencode(src, "UTF-8");
	}

	public static boolean valid(String str) {
		return valid(str, false);
	}

	/**
	 * 验证字符串是否有效
	 * 
	 * @param str
	 * @param includeNull
	 *            为true,会检测null字符串
	 * @return
	 */
	public static boolean valid(String str, boolean includeNull) {
		return !((str == null) || "".equals(str.trim()) || (includeNull && "null"
				.equalsIgnoreCase(str)));
	}

	/**
	 * 验证字符串是否是双精度格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDouble(String str) {
		if (valid(str, true)) {
			return str.matches("[\\d\\.]+");
		}
		return false;
	}

	/**
	 * 验证字符串是否为整型
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		if (valid(str, true)) {
			return str.matches("[\\d]+");
		}
		return false;
	}

	public static String excapeXmlQuote(String str) {
		if (!valid(str, true)) {
			return str;
		}
		return str.replaceAll("&quot;", "\"").replaceAll("&apos;", "'")
				.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
				.replaceAll("&amp;", "&");
	}

	/**
	 * 字符串转化为双精度类型
	 * 
	 * @param str
	 * @param def
	 * @return
	 */
	public static double parseDouble(String str, double def) {
		return isDouble(str) ? Double.parseDouble(str) : def;
	}

	/**
	 * 字符串转化为整型类型
	 * 
	 * @param str
	 * @param def
	 * @return
	 */
	public static int parseInteger(String str, int def) {
		return isDouble(str) ? Integer.parseInt(str) : def;
	}

	/**
	 * 对字符串UTF-8编码，MD5加密
	 * 
	 * @param src
	 * @return 密文
	 */
	public static String encrypt(String src) {
		return encrypt(src, "UTF-8", "MD5");
	}

	/**
	 * 对字符串加密
	 * 
	 * @param src
	 *            明文
	 * @param encoding
	 *            编码方式
	 * @param method
	 *            加密方式，支持 MD5,SHA-1 两种方式
	 * @return
	 */
	public static String encrypt(String src, String encoding, String method) {
		MessageDigest md = null;
		if ("MD5".equals(method)) {
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (final NoSuchAlgorithmException e) {
			}
		} else if ("SHA-1".equals(method)) {
			try {
				md = MessageDigest.getInstance("SHA-1");
			} catch (final NoSuchAlgorithmException e) {
			}
		} else {
			throw new IllegalArgumentException("Only MD5 or SHA-1 supported.");
		}
		try {
			md.update(src.getBytes(valid(encoding) ? encoding : "UTF-8"));
			final byte[] bytes = md.digest();
			return bytesToString(bytes);
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalArgumentException("UnsupportedEncoding:"
					+ encoding);
		}
	}

	/**
	 * 字节数组转为字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToString(byte[] bytes) {
		final StringBuilder hs = new StringBuilder();
		for (int n = 0; n < bytes.length; n++) {
			final String stmp = Integer.toHexString(bytes[n] & 0xFF);
			if (stmp.length() == 1) {
				hs.append("0").append(stmp);
			} else {
				hs.append(stmp);
			}
		}
		return hs.toString().toLowerCase();
	}

	public static String getThrowableStackTrace(Throwable t) {
		if (t == null) {
			return "";
		}
		String result = "";
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.flush();
		result = sw.getBuffer().toString();
		pw.close();
		return result;
	}

	/**
	 * @param stringExtra
	 * @return
	 */
	public static String escapeNull(String stringExtra) {
		if (stringExtra == null || "null".equalsIgnoreCase(stringExtra)) {
			return "";
		}
		return stringExtra;
	}

	/**
	 * 格式化距离
	 * 
	 * @param distance
	 * @return
	 */
	public static String formatDistance(String distance) {
		if (!StringUtil.valid(distance, true)) {
			return "";
		}
		try {
			final double dist = Double.parseDouble(distance);
			if (dist > 1) {
				return 1.0 * Math.round(dist * 100) / 100 + "千米";
			} else {
				return 1.0 * Math.round(dist * 1000) + "米";
			}

		} catch (final Exception e) {
		}
		return distance;
	}

	/**
	 * 格式化距离
	 * 
	 * @return String
	 */
	public String formatDistanceEn(String rawdistance) {
		if (rawdistance != null) {
			final DecimalFormat sf = new DecimalFormat("#.##");
			final DecimalFormat si = new DecimalFormat("#.#");
			final float distance = Float.parseFloat(rawdistance);
			String distance_s = "";
			if (distance >= 1.0) {
				distance_s = sf.format(distance);
				return distance_s + "Km";
			} else {
				distance_s = si.format(distance * 1000);
				return distance_s + "m";
			}

		} else {
			return null;
		}
	}

	public static final void writeUTF(DataOutputStream dos, String string)
			throws IOException {
		if (string == null) {
			dos.writeUTF(new String());
		} else {
			dos.writeUTF(string);
		}
	}

	public static final String readUTF(DataInputStream dis) throws IOException {
		final String retVal = dis.readUTF();
		if (retVal.length() == 0)
			return null;
		return retVal;
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing
	 * the provided list of elements.
	 * </p>
	 * 
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty
	 * strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *)               = null
	 * StringUtils.join([], *)                 = ""
	 * StringUtils.join([null], *)             = ""
	 * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
	 * StringUtils.join(["a", "b", "c"], null) = "abc"
	 * StringUtils.join([null, "", "a"], ';')  = ";;a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null array input
	 * @since 2.0
	 */
	public static String join(Object[] array, char separator) {
		if (array == null) {
			return null;
		}

		return join(array, separator, 0, array.length);
	}

	// 时间戳到当前时间转换
	public static String getTime(String format, String str_time) {
		String result = "";
		if (format != null && !format.equals("") && str_time != null
				&& str_time.length() > 0 && !str_time.equals("")) {
			final Long time = Long.parseLong(str_time);
			result = new java.text.SimpleDateFormat(format)
					.format(new Date(time * 1000));
		}
		return result;
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing
	 * the provided list of elements.
	 * </p>
	 * 
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty
	 * strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *)               = null
	 * StringUtils.join([], *)                 = ""
	 * StringUtils.join([null], *)             = ""
	 * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
	 * StringUtils.join(["a", "b", "c"], null) = "abc"
	 * StringUtils.join([null, "", "a"], ';')  = ";;a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass
	 *            in an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to
	 *            pass in an end index past the end of the array
	 * @return the joined String, {@code null} if null array input
	 * @since 2.0
	 */
	public static String join(Object[] array, char separator, int startIndex,
			int endIndex) {
		if (array == null) {
			return null;
		}
		final int noOfItems = (endIndex - startIndex);
		if (noOfItems <= 0) {
			return "";
		}

		final StringBuilder buf = new StringBuilder(noOfItems * 16);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing
	 * the provided list of elements.
	 * </p>
	 * 
	 * <p>
	 * No delimiter is added before or after the list. A {@code null} separator
	 * is the same as an empty String (""). Null objects or empty strings within
	 * the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *)                = null
	 * StringUtils.join([], *)                  = ""
	 * StringUtils.join([null], *)              = ""
	 * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
	 * StringUtils.join(["a", "b", "c"], null)  = "abc"
	 * StringUtils.join(["a", "b", "c"], "")    = "abc"
	 * StringUtils.join([null, "", "a"], ',')   = ",,a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @return the joined String, {@code null} if null array input
	 */
	public static String join(Object[] array, String separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing
	 * the provided list of elements.
	 * </p>
	 * 
	 * <p>
	 * No delimiter is added before or after the list. A {@code null} separator
	 * is the same as an empty String (""). Null objects or empty strings within
	 * the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *)                = null
	 * StringUtils.join([], *)                  = ""
	 * StringUtils.join([null], *)              = ""
	 * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
	 * StringUtils.join(["a", "b", "c"], null)  = "abc"
	 * StringUtils.join(["a", "b", "c"], "")    = "abc"
	 * StringUtils.join([null, "", "a"], ',')   = ",,a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass
	 *            in an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to
	 *            pass in an end index past the end of the array
	 * @return the joined String, {@code null} if null array input
	 */
	public static String join(Object[] array, String separator, int startIndex,
			int endIndex) {
		if (array == null) {
			return null;
		}
		if (separator == null) {
			separator = "";
		}

		// endIndex - startIndex > 0: Len = NofStrings *(len(firstString) +
		// len(separator))
		// (Assuming that all Strings are roughly equally long)
		final int noOfItems = (endIndex - startIndex);
		if (noOfItems <= 0) {
			return "";
		}

		final StringBuilder buf = new StringBuilder(noOfItems * 16);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	public static String getMD5(byte[] source) {
		String s = null;
		final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			final MessageDigest md = MessageDigest
					.getInstance("MD5");
			md.update(source);
			final byte tmp[] = md.digest();
			final char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				final byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static boolean isValidAddress(String address) {
		// Note: Some email provider may violate the standard, so here we only
		// check that
		// address consists of two part that are separated by '@', and domain
		// part contains
		// at least one '.'.
		final int len = address.length();
		final int firstAt = address.indexOf('@');
		final int lastAt = address.lastIndexOf('@');
		final int firstDot = address.indexOf('.', lastAt + 1);
		final int lastDot = address.lastIndexOf('.');
		return firstAt > 0 && firstAt == lastAt && lastAt + 1 < firstDot
				&& firstDot <= lastDot && lastDot < len - 1;
	}

	public static String timeStamp2Date(String timestampString, String format) {
		final Long timestamp = Long.parseLong(timestampString) * 1000;
		final String date = new java.text.SimpleDateFormat(format)
				.format(new Date(timestamp));
		return date;
	}

	public static String replaceBlank(String str) {
		// Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		final Pattern p = Pattern.compile("\\s*|\t");
		final Matcher m = p.matcher(str);
		final String after = m.replaceAll("");
		return after;
	}

	/**
	 * @Title getImgSrc
	 * @Description: 从一个字符串中获取图片地址
	 * @param String
	 * @return String
	 * @throws
	 */

	public static String getImgSrc(String s) {
		String[] ss = null;
		String[] sss = null;
		if (s.contains("src='")) {
			ss = s.split("src='");
			sss = ss[1].split("'");
		} else if (s.contains("src=\"")) {
			ss = s.split("src=\"");
			sss = ss[1].split("\"");
		} else {
			return "";
		}
		return sss[0];
	}

	public String formatDay(String end_time) {
		if ("".equals(end_time) || end_time == null) {
			return "";
		}
		final String split[] = end_time.split("\\.");
		end_time = split[0];
		final Date date = new Date();
		Long now = date.getTime();
		now /= 1000;
		final Long old = Long.parseLong(end_time);
		final Long newTime = old - now;
		String day_str = "";
		String hour_str = "";
		if (newTime <= 0) {
			return "已结束";
		} else {
			final int day = (int) (newTime / (3600 * 24));
			if (day != 0) {
				day_str = Integer.toString(day);
				day_str += "天";
			}
			final int hour = (int) ((newTime - day * 24 * 3600) / 3600);
			hour_str = Integer.toString(hour);
			hour_str += "小时";
		}
		return day_str + " " + hour_str;
	}

	public String replace_html(String strTemp) {
		if (strTemp == null) {
			return "";
		}
		Pattern pattern = Pattern.compile("<a[^<>]*>");
		Matcher mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</a>", "");

		pattern = Pattern.compile("<font[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</font>", "");

		pattern = Pattern.compile("<img[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</img>", "");

		pattern = Pattern.compile("<p[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</p>", "");

		pattern = Pattern.compile("<b[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</b>", "");

		pattern = Pattern.compile("<span[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</span>", "");

		pattern = Pattern.compile("<st1:chsdate[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</st1:chsdate>", "");

		pattern = Pattern.compile("<o:p[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</o:p>", "");

		pattern = Pattern.compile("<strong[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</strong>", "");

		pattern = Pattern.compile("<h3[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</h3>", "");

		pattern = Pattern.compile("<h2[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</h2>", "");

		pattern = Pattern.compile("<h1[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</h1>", "");

		pattern = Pattern.compile("<table[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</table>", "");

		pattern = Pattern.compile("<tbody[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</tbody>", "");

		pattern = Pattern.compile("<tr[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</tr>", "");

		pattern = Pattern.compile("<td[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</td>", "");

		pattern = Pattern.compile("<i[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</i>", "");

		pattern = Pattern.compile("<div[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</div>", "");

		pattern = Pattern.compile("<palign[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</palign>", "");

		pattern = Pattern.compile("<fontsize[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</fontsize>", "");

		pattern = Pattern.compile("<imgsrc[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</imgsrc>", "");

		pattern = Pattern.compile("<hr[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</hr>", "");

		pattern = Pattern.compile("<objectclassid[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</objectclassid>", "");

		pattern = Pattern.compile("<embedsrc[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</embedsrc>", "");

		pattern = Pattern.compile("<paramname[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</paramname>", "");

		pattern = Pattern.compile("<fontcolor[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</fontcolor>", "");

		pattern = Pattern.compile("<pclass[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</pclass>", "");

		pattern = Pattern.compile("<imghight[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</imghight>", "");

		pattern = Pattern.compile("<imgwidth[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</imgwidth>", "");

		pattern = Pattern.compile("<inputtype[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</inputtype>", "");

		pattern = Pattern.compile("<layerid[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</layerid>", "");

		pattern = Pattern.compile("<inputname[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</inputname>", "");

		pattern = Pattern.compile("<optionvalue[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</optionvalue>", "");

		pattern = Pattern.compile("<selectstyle[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</selectstyle>", "");

		pattern = Pattern.compile("<inputmaxlength[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</inputmaxlength>", "");

		pattern = Pattern.compile("<form[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</form>", "");

		pattern = Pattern.compile("<inputtype[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</inputtype>", "");

		pattern = Pattern.compile("<cntr[^<>]*>");
		mat = pattern.matcher(strTemp);

		pattern = Pattern.compile("<center[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</center>", "");

		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</cntr>", "");

		pattern = Pattern.compile("<u[^<>]*>");
		mat = pattern.matcher(strTemp);
		while (mat.find()) {
			strTemp = strTemp.replace(mat.group(), "");
		}
		strTemp = strTemp.replace("</u>", "");

		strTemp = strTemp.replace("#39", "");
		strTemp = strTemp.replace("#60", "");
		strTemp = strTemp.replace("#34", "");
		strTemp = strTemp.replace("&", "");
		strTemp = strTemp.replace("e", "");
		strTemp = strTemp.replace("[1]", "");
		strTemp = strTemp.replace("[2]", "");
		strTemp = strTemp.replace("[3]", "");
		strTemp = strTemp.replace("[4]", "");
		strTemp = strTemp.replace("[5]", "");
		strTemp = strTemp.replace("[6]", "");
		strTemp = strTemp.replace("[7]", "");
		strTemp = strTemp.replace("[8]", "");
		strTemp = strTemp.replace("下一页", "");
		strTemp = strTemp.replace("上一页", "");
		strTemp = strTemp.replace("&nbsp;", "");
		strTemp = strTemp.replace("\n", "").replace("<br/>", "\n")
				.replace("<br />", "\n").replace("<br>", "\n");
		return strTemp;
	}

	/**
	 * emaill格式验证
	 * 
	 * @param @param email
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isEmail(String email) {
		final String check = "^([a-z0-9a-z]+[-|\\.]?)+[a-z0-9a-z]@([a-z0-9a-z]+(-[a-z0-9a-z]+)?\\.)+[a-za-z]{2,}$";
		final Pattern regex = Pattern.compile(check);
		final Matcher matcher = regex.matcher(email);
		final boolean ismatched = matcher.matches();
		return ismatched;
	}

	/**
	 * 手机号码验证
	 * 
	 * @param @param str
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isPhone(String str) {
		final Pattern pattern = Pattern.compile("1[0-9]{10}");
		final Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * MD5加密类
	 */
	public static String MD5(String str) {
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			final byte[] byteDigest = md.digest();
			int i;
			final StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < byteDigest.length; offset++) {
				i = byteDigest[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// //32位加密
			// return buf.toString();
			// 16位的加密
			return buf.toString().substring(8, 24);
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isEmpty(String str) {
		if (TextUtils.isEmpty(str) || "null".equals(str) || "".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEmpty(CharSequence str) {
		if (str == null || "".equals(str) || "null".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEmpty(Object str) {
		if (str == null || "".equals(str) || "null".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 字符串转全角
	 * 
	 * @author wanghongfang @Date 2015-3-20 下午12:01:54
	 *         description:{这里用一句话描述这个方法的作用}
	 * @param {引入参数名  {引入参数说明}
	 * @return {返回值说明}
	 */
	/*
	 * public static String ToDBC(String input) { char[] c =
	 * input.toCharArray(); for (int i = 0; i< c.length; i++) { if (c[i] ==
	 * 12288) { c[i] = (char) 32; continue; }if (c[i]> 65280&& c[i]< 65375) c[i]
	 * = (char) (c[i] - 65248); } return new String(c); }
	 */

	/**
	 * 半角转全角
	 * 
	 * @param input
	 *            String.
	 * @return 全角字符串.
	 */
	public static String ToSBC(String input) {
		final char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000'; // 采用十六进制,相当于十进制的12288

			} else if (c[i] < '\177') { // 采用八进制,相当于十进制的127
				c[i] = (char) (c[i] + 65248);

			}
		}
		return new String(c);
	}

	/**
	 * 全角转半角
	 * 
	 * @param input
	 *            String.
	 * @return 半角字符串
	 */
	public static String ToDBC(String input) {

		final char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);

			}
		}
		final String returnString = new String(c);

		return returnString;
	}

	/**
	 * 删除末尾最后一字
	 * 
	 * @return
	 */
	public static String deletlastchar(String s, int lastindex) {
		return s.substring(0, s.length() - lastindex);
	}

}
