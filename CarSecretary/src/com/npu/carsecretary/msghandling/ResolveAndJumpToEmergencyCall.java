package com.npu.carsecretary.msghandling;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.npu.carsecretary.R;
import com.npu.carsecretary.view.AlertView;
import com.npu.carsecretary.view.OnDismissListener;
import com.npu.carsecretary.view.OnItemClickListener;

public class ResolveAndJumpToEmergencyCall  implements OnItemClickListener,
OnDismissListener {

	private Context mContext; // 上下文
	AlertView alertView ;
	
	private String emergencyCallNum[] = new String[]{
			"122","120","110","119","95105988","95500","95518","95511","4008181010","4008108208"
	};
	
	public ResolveAndJumpToEmergencyCall(Context mContext) {
		super();
		this.mContext = mContext;
	}


	public void showEmergencyCall(){
		alertView =  new AlertView("应急电话", null, "取消", new String[]{"事故", "急救", "匪警", "火警"},
	                new String[]{"中石化免费救援", "太平洋车险免费救援",
	                        "人保车险免费救援", "平安车险免费救援", "大陆汽车救援", "中联车盟道路救援"},
	                mContext, AlertView.Style.ActionSheet, this);
		alertView.show();
		
	}

	@Override
	public void onDismiss(Object o) {
		// TODO Auto-generated method stub
		alertView.dismiss();
		
	}

	@Override
	public void onItemClick(Object o, int position) {
		// TODO Auto-generated method stub
		if(position !=  AlertView.CANCELPOSITION){
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + emergencyCallNum[position]));
			//开启系统拨号器
			mContext.startActivity(intent);	
		}
		
	}

}
