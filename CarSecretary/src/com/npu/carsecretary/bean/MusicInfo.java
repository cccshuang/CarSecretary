package com.npu.carsecretary.bean;

public class MusicInfo {

	public MusicInfo(String name, String url, String singer) {
		super();
		this.name = name;
		this.url = url;
		this.singer = singer;
	}
	public MusicInfo(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}
	public String name = "";
	public String url = "";
	public String singer = "";
	

}
