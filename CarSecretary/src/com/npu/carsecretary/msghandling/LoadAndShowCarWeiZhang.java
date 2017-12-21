package com.npu.carsecretary.msghandling;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.npu.carsecretary.R;
import com.npu.carsecretary.view.AlertView;
import com.npu.carsecretary.view.OnDismissListener;
import com.npu.carsecretary.view.OnItemClickListener;

public class LoadAndShowCarWeiZhang implements OnItemClickListener,
		OnDismissListener {

	private Context mContext; // 上下文
	private AlertView alertView;
	 //自动填写表单的信息
	private String engineNo = ""; //发动机号
	private String plateNo = ""; //车牌号
	
	

	public LoadAndShowCarWeiZhang(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void showWeiZhangInfo(String plate,String engine) {
		String url = "http://m.cheshouye.com/api/weizhang/";// 从车主页来获取信息
		
		plateNo = plate;
		engineNo = engine;
		
		
		alertView = new AlertView(null, null, "取消", null, null, mContext,
				AlertView.Style.ActionSheet, this);
		ViewGroup extView = (ViewGroup) LayoutInflater.from(mContext).inflate(
				R.layout.alert_form_tocarweizhang, null);
		WebView webView = (WebView) extView
				.findViewById(R.id.webViewToWeiZhang);

		// 设置支持JavaScript脚本
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// 设置可以访问文件
		webSettings.setAllowFileAccess(true);
		// 设置支持缩放
		webSettings.setBuiltInZoomControls(true);

		// WebView加载web资源
		webView.loadUrl(url);
		// 设置WebViewClient
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				
				// 加载完了再调用js代码
				view.loadUrl("javascript: {"
						+ "document.getElementById('plateNo').value = '"
						+ plateNo + "';"
						+ "document.getElementById('engineNo').value = '"
						+ engineNo + "';"
						+ "document.getElementsById('btn_query').click();"
						+ "};");

				// 成功后加载框取消
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// 在这里可以创建一个加载框。。。
				super.onPageStarted(view, url, favicon);
			}
		});
		alertView.addExtView(extView);
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
		if(position == AlertView.CANCELPOSITION){
			alertView.dismiss();
	}

	}

}
