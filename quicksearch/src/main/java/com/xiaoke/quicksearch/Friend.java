package com.xiaoke.quicksearch;

public class Friend implements Comparable<Friend>{
	public String name;
	public String pinyin;
	

	public Friend(String name) {
		super();
		this.name = name;
		
		//初始化的时候就获取拼音
		pinyin = PinYinUtil.getPinYin(name);
	}

	@Override
	public int compareTo(Friend another) {
		return pinyin.compareTo(another.pinyin);
	}
	
}
