package com.npu.carsecretary.msghandling;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.npu.carsecretary.R;
import com.npu.carsecretary.view.AlertView;
import com.npu.carsecretary.view.OnDismissListener;
import com.npu.carsecretary.view.OnItemClickListener;

public class ResolveAndJumpToWeatherAndAir implements OnItemClickListener,
		OnDismissListener {

	private Context mContext; // 上下文
	AlertView alertView;


	public ResolveAndJumpToWeatherAndAir(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void showWeatherInfo(JSONObject jsonMsg) throws JSONException {

		String url = jsonMsg.getJSONObject("webPage").getString("url");

		alertView = new AlertView("天气", null, "取消", null, null, mContext,
				AlertView.Style.ActionSheet, this);
		ViewGroup extView = (ViewGroup) LayoutInflater.from(mContext).inflate(
				R.layout.alert_form_toweather, null);
		WebView webView = (WebView) extView.findViewById(R.id.webViewToWeather);
		webView.getSettings().setJavaScriptEnabled(true);
		// WebView加载web资源
		webView.loadUrl(url);
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
		alertView.addExtView(extView);
		alertView.show();

	}

	@SuppressLint("SetJavaScriptEnabled")
	public void showAirInfo(JSONObject jsonMsg) throws JSONException {
		String url = jsonMsg.getJSONObject("webPage").getString("url");

		alertView = new AlertView("空气质量", null, "取消", null, null, mContext,
				AlertView.Style.ActionSheet, this);
		ViewGroup extView = (ViewGroup) LayoutInflater.from(mContext).inflate(
				R.layout.alert_form_toair, null);
		WebView webView = (WebView) extView.findViewById(R.id.webViewToAir);
		webView.getSettings().setJavaScriptEnabled(true);
		// WebView加载web资源
		webView.loadUrl(url);
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
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

	}

}
