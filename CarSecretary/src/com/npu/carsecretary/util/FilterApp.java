package com.npu.carsecretary.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.npu.carsecretary.bean.AppInfo;

public class FilterApp {
	
	public static AppInfo filterApp(Context mContext,String appName){
		AppInfo targetApp = null;
		ArrayList<AppInfo> appList = getInstalledApps(mContext);
		double lastLikeScore = -1.0;
		for(AppInfo appInfo:appList){
			double commonSub = LongestCommonSubsequence.compute(ChineseToPinyin.ToPinyin(appName), ChineseToPinyin.ToPinyin(appInfo.appname));
			double length = ChineseToPinyin.ToPinyin(appName).length();
			System.out.println(appInfo.appname + " "+length + " "+commonSub + " "+commonSub/length);
			double likeScore = commonSub/length;
			if(likeScore > 0.80){ //大于75%相似度
				if(likeScore > lastLikeScore){
					targetApp = appInfo;
					lastLikeScore = likeScore;
				}
			}
		}
		
		return targetApp;
		
	}
	
	

	private static ArrayList<AppInfo> getInstalledApps(Context mContext) {
		ArrayList<AppInfo> res = new ArrayList<AppInfo>();
		List<PackageInfo> packs = mContext.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			if (p.versionName == null) {
				continue;
			}
			AppInfo newInfo = new AppInfo();
			newInfo.appname = p.applicationInfo.loadLabel(
					mContext.getPackageManager()).toString();
			newInfo.pname = p.packageName;
			newInfo.versionName = p.versionName;
			newInfo.versionCode = p.versionCode;
			newInfo.icon = p.applicationInfo.loadIcon(mContext
					.getPackageManager());
			System.out.println(newInfo.appname);
			res.add(newInfo);
		}
		return res;
	}

}
