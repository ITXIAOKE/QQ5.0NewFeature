package com.xiaoke.quicksearch;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.text.TextUtils;

public class PinYinUtil {
	/**
	 * 获取汉字的拼音,由于获取拼音的原理是读取并解析xml，会消耗一定效率，最好不要频繁调用
	 * @param chinese
	 * @return
	 */
	public static String getPinYin(String chinese){
		if(TextUtils.isEmpty(chinese)) return null;
		
		//汉语拼音输出格式化
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);//设置拼音是大写字母
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);//设置不带声调
		
		//黑马  -> heima
		//由于不支持同时对多个汉字进行获取，所以将汉字字符串转为字符数组，逐一获取，最后拼接
		StringBuilder builder = new StringBuilder();
		char[] charArray = chinese.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			//黑   马 ->heima
			//1.过滤掉空格的字符,选择忽略
			if(Character.isWhitespace(c))continue;
			
			//黑acv**马O(∩_∩)O~
			//2.判断是否是汉字,此处作简单判断，汉字占2字节，一个字节是-128~127,
			if(c > 127){
				//那么有可能是汉字
				try {
					//由于多音字的存在,  单 ： {dan, shan} 
					String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(c, format);
					if(pinyinArr!=null){
						//此处暂时取第0个,首先大部分汉字都是一个拼音，如果真的是多音字，那么也只能取第0个，因为不知道取哪个
						//如果需要精确到某个拼音，那么只能是后台提供强大的数据库支持；
						//单田芳     D
						builder.append(pinyinArr[0]);
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
					//如果抛出异常，可能不是正确的汉字，肯定没有拼音，选择忽略
				}
			}else {
				//绝对不是汉字，一般是英文字母，以及键盘上能够直接输入的字符
				//对于这样的字符是可以参与排序的，所以选择直接拼接
				builder.append(c);
			}
		}
		return builder.toString();
	}
}
