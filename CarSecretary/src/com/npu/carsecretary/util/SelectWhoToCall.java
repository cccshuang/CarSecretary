package com.npu.carsecretary.util;

import java.util.ArrayList;
import java.util.List;

import com.npu.carsecretary.bean.ContactMsg;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Data;
import android.widget.Toast;


public class SelectWhoToCall {
	
	private static List<ContactMsg> contactMsgList = new ArrayList<ContactMsg>();
	
	public SelectWhoToCall(){
		
	}
	
	public static String[] getPhoneByName(String name,Context mContext){
		String codeAndName[] = new String[2];
		readAllContact(mContext);
		double lastLikeScore = -1.0;
		for(ContactMsg contactMsg : contactMsgList)
		{
			double commonSub = LongestCommonSubsequence.compute(ChineseToPinyin.ToPinyin(name), ChineseToPinyin.ToPinyin(contactMsg.name));
			double length = ChineseToPinyin.ToPinyin(name).length();
			System.out.println(ChineseToPinyin.ToPinyin(name) + " "+ChineseToPinyin.ToPinyin(contactMsg.name)+" "+length + " "+commonSub + " "+commonSub/length);
			double likeScore = commonSub/length;
			if(commonSub/length > 0.80){ //大于60%相似度
				if(likeScore > lastLikeScore){
					codeAndName[0] = contactMsg.phone;
					codeAndName[1] = contactMsg.name;
					lastLikeScore = likeScore;
				}
			}
		}
		
		
		return codeAndName;
		
		
	}

	//读取通讯录的全部的联系人
	//需要先在raw_contact表中遍历id，并根据id到data表中获取数据
	private static void readAllContact(Context mContext){
		Uri uri = Uri.parse("content://com.android.contacts/contacts");	//访问raw_contacts表
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(uri, new String[]{Data._ID}, null, null, null);	//获得_id属性
		while(cursor.moveToNext()){
			ContactMsg contactMsg = new ContactMsg();
			int id = cursor.getInt(0);//获得id并且在data中寻找数据
			uri = Uri.parse("content://com.android.contacts/contacts/"+id+"/data");	//如果要获得data表中某个id对应的数据，则URI为content://com.android.contacts/contacts/#/data
			Cursor cursor2 = resolver.query(uri, new String[]{Data.DATA1,Data.MIMETYPE}, null,null, null);	//data1存储各个记录的总数据，mimetype存放记录的类型，如电话、email等
			while(cursor2.moveToNext()){
				String data = cursor2.getString(cursor2.getColumnIndex("data1"));
				if(cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/name")){		//如果是名字
					contactMsg.name = data;
				}
				else if(cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/phone_v2")){	//如果是电话
					contactMsg.phone = data;
				}
				else if(cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/email_v2")){	//如果是email
					contactMsg.email = data;
				}
				else if(cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/postal-address_v2")){	//如果是地址
					contactMsg.address = data;
				}
				else if(cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/organization")){	//如果是组织
					contactMsg.organization = data;
				}
				
			}
			cursor2.close();
			contactMsgList.add(contactMsg);
		}
		cursor.close();
	}
	
	
}
