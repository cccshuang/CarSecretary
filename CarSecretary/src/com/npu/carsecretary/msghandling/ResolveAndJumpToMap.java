package com.npu.carsecretary.msghandling;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.ViewGroup;

import android.widget.EditText;

import android.widget.Toast;

import com.baidu.speech.EventManager;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.npu.carsecretary.R;
import com.npu.carsecretary.util.VibrateHelp;
import com.npu.carsecretary.view.AlertView;
import com.npu.carsecretary.view.OnDismissListener;
import com.npu.carsecretary.view.OnItemClickListener;

public class ResolveAndJumpToMap implements OnItemClickListener,
		OnDismissListener {

	private Context mContext; // 上下文
	private EventManager mWpEventManager;// 百度语音唤醒服务

	private SpeechSynthesizer mTts;// 讯飞语音合成
	private SpeechRecognizer mIat;// 语音识别，不带UI
	private AlertView alertView;// 弹出的对话框
	private EditText etStartLoc;// 拓展View内容 导航
	private EditText etEndLoc;// 拓展View内容 导航
	private EditText etLocation;// 拓展View内容 定位
	// private InputMethodManager imm;

	// 当前执行操作的标记
	private boolean isRoute = false;
	private boolean isLocation = false;
	private boolean isMsgEmpty = false;
	private String requestMsg = "";
	

	/**
	 * 构造函数
	 * 
	 * @param mContext
	 *            当前的上下文
	 * @param mWpEventManager
	 *            语音唤醒服务
	 */
	public ResolveAndJumpToMap(Context mContext, EventManager mWpEventManager) {
		super();
		this.mContext = mContext;
		this.mWpEventManager = mWpEventManager;

		// imm = (InputMethodManager) mContext
		// .getSystemService(Context.INPUT_METHOD_SERVICE);

		// 设置语音识别参数
		mIat = SpeechRecognizer.createRecognizer(mContext, null);
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "plain"); // 返回值的类型

		// 设置语音合成参数
		mTts = SpeechSynthesizer.createSynthesizer(mContext, null);
		mTts.setParameter(SpeechConstant.VOICE_NAME, "nannan");// 设置发音人
		mTts.setParameter(SpeechConstant.SPEED, "80");// 设置语速
		mTts.setParameter(SpeechConstant.PITCH, "80");// 设置音调
		mTts.setParameter(SpeechConstant.VOLUME, "100");// 设置音量0-100
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
	}
	
	public void jumpByMenuRo(String startLoc){
		isRoute = true; // 标记当前为导航

		alertView = new AlertView("导航", null, "取消", null,
				new String[] { "确定" }, mContext, AlertView.Style.Alert,
				this);
		ViewGroup extView = (ViewGroup) LayoutInflater.from(mContext)
				.inflate(R.layout.alertext_form_tomap_route, null);
		etStartLoc = (EditText) extView.findViewById(R.id.etStartLoc);
		etEndLoc = (EditText) extView.findViewById(R.id.etEndLoc);
		etStartLoc.setText(startLoc);
		etEndLoc.setText("");
		alertView.addExtView(extView);
		alertView.show();
		
	}
	
	public void jumpByMenuRo(String startLoc,String endLoc){
		isRoute = true; // 标记当前为导航

		alertView = new AlertView("导航", null, "取消", null,
				new String[] { "确定" }, mContext, AlertView.Style.Alert,
				this);
		ViewGroup extView = (ViewGroup) LayoutInflater.from(mContext)
				.inflate(R.layout.alertext_form_tomap_route, null);
		etStartLoc = (EditText) extView.findViewById(R.id.etStartLoc);
		etEndLoc = (EditText) extView.findViewById(R.id.etEndLoc);
		etStartLoc.setText(startLoc);
		etEndLoc.setText(endLoc);
		alertView.addExtView(extView);
		alertView.show();
		
	}
	
	public void jumpByMenuPo(String startPos){
		isLocation = true; // 标记当前为导航

		alertView = new AlertView("定位", null, "取消", null,
				new String[] { "确定" }, mContext, AlertView.Style.Alert,
				this);
		ViewGroup extView = (ViewGroup) LayoutInflater.from(mContext)
				.inflate(R.layout.alertext_form_tomap_location, null);
		etLocation = (EditText) extView.findViewById(R.id.etLocation);
		etLocation.setText(startPos);
		alertView.addExtView(extView);
		alertView.show();
		
	}
	

	/*
	 * 解析并根据解析结果来执行不同操作
	 */
	public void resolveAndJump(JSONObject requestMsg,String curCity,String curLoc) throws JSONException {

			String service = requestMsg.getString("service").toString();
			String operation = requestMsg.getString("operation").toString();
			JSONObject semantic = requestMsg.getJSONObject("semantic");
			JSONObject slots = semantic.getJSONObject("slots");
		
		
		if (operation.equals("ROUTE")) { // 导航
			// 返回的数据：city和poi可能分别为CURRENT_CITY和CURRENT_POI的情况，跳转后有地图Activity进行替换
			String startLocStr = ""; // 起点位置
			String startStrShow = "";
			JSONObject startLoc = slots.getJSONObject("startLoc");
			if (startLoc.has("province")) { // 省
				startLocStr += startLoc.getString("province");
				startStrShow += startLoc.getString("province");
			}
			if (startLoc.has("city")) { // 市
				startLocStr += startLoc.getString("city");
				if (!startLoc.getString("city").equals("CURRENT_CITY")) {
					startStrShow += startLoc.getString("city");
				} else {
					//startStrShow += "当前城市";
					startStrShow += curCity;
				}
			}
			if (startLoc.has("area")) { // 区
				startLocStr += startLoc.getString("area");
				startStrShow += startLoc.getString("area");
			}
			if (startLoc.has("poi")) { // 位置点
				startLocStr += startLoc.getString("poi");
				if (!startLoc.getString("poi").equals("CURRENT_POI")) {
					startStrShow += startLoc.getString("poi");
				} else {
					//startStrShow += "当前位置";
					startStrShow = curLoc;
				}

			}
			String endLocStr = ""; // 终点位置
			String endStrShow = "";
			JSONObject endLoc = slots.getJSONObject("endLoc");
			if (endLoc.has("province")) {
				endLocStr += endLoc.getString("province");
				endStrShow += endLoc.getString("province");
			}
			if (endLoc.has("city")) {
				endLocStr += endLoc.getString("city");
				if (!endLoc.getString("city").equals("CURRENT_CITY")) {
					endStrShow += endLoc.getString("city");
				} else {
					//endStrShow += "当前城市";
					endStrShow += curCity;
				}
			}
			if (endLoc.has("area")) {
				endLocStr += endLoc.getString("area");
				endStrShow += endLoc.getString("area");
			}
			if (endLoc.has("poi")) {
				endLocStr += endLoc.getString("poi");
				if (!endLoc.getString("poi").equals("CURRENT_POI")) {
					endStrShow += endLoc.getString("poi");
				} else {
					//endStrShow += "当前位置";
					endStrShow = curLoc;
				}
			}
			isRoute = true; // 标记当前为导航

			alertView = new AlertView("导航", null, "取消", null,
					new String[] { "确定" }, mContext, AlertView.Style.Alert,
					this);
			ViewGroup extView = (ViewGroup) LayoutInflater.from(mContext)
					.inflate(R.layout.alertext_form_tomap_route, null);
			etStartLoc = (EditText) extView.findViewById(R.id.etStartLoc);
			etEndLoc = (EditText) extView.findViewById(R.id.etEndLoc);
			etStartLoc.setText(startStrShow);
			etEndLoc.setText(endStrShow);
			alertView.addExtView(extView);
			alertView.show();

			if (!startStrShow.equals("") && !endStrShow.equals("")) {
				mTts.startSpeaking("你是不是要从" + startStrShow + "到" + endStrShow
						+ "呀?", mTtsListener);
			} else {
				mTts.startSpeaking("请您输入起始地和目的地，点击确定进行导航！", mTtsListener);
			}

		} else if (operation.equals("POSITION")) { // 寻找某一位置
			String locationStr = ""; // 要找的位置
			String locationShow = "";
		
			JSONObject location = slots.getJSONObject("location");
			if (location.has("province")) { // 省
				locationStr += location.getString("province");
				locationShow += location.getString("province");
			}
			if (location.has("city")) { // 市
				locationStr += location.getString("city");
				if (!location.getString("city").equals("CURRENT_CITY")) {
					locationShow += location.getString("city");
				} else {
					//locationShow += "当前城市";
					locationShow += curCity;
				}
			}
			if (location.has("area")) { // 区
				locationStr += location.getString("area");
				locationShow += location.getString("area");
			}
			if (location.has("poi")) { // 位置点
				locationStr += location.getString("poi");
				if (!location.getString("poi").equals("CURRENT_POI")) {
					locationShow += location.getString("poi");
				} else {
					//locationShow += "当前位置";
					locationShow = curLoc;
				}
			}
			isLocation = true;// 标记当前操作为定位

			alertView = new AlertView("定位", null, "取消", null,
					new String[] { "确定" }, mContext, AlertView.Style.Alert,
					this);
			ViewGroup extView = (ViewGroup) LayoutInflater.from(mContext)
					.inflate(R.layout.alertext_form_tomap_location, null);
			etLocation = (EditText) extView.findViewById(R.id.etLocation);
			etLocation.setText(locationShow);
			alertView.addExtView(extView);
			alertView.show();

			if (!locationStr.equals("")) {
				mTts.startSpeaking("您是不是要寻找" + locationShow + "的位置呀?",
						mTtsListener);
			} else {
				mTts.startSpeaking("请您输入位置地点，点击确定进行定位！", mTtsListener);
			}

			// Toast.makeText(mContext, locationStr, Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onItemClick(Object o, int position) {
		// TODO Auto-generated method stub

		

		if (position != AlertView.CANCELPOSITION) {
			try {
				// 不关闭
				Field field = alertView.getClass().getDeclaredField("isShowing");
				field.setAccessible(true);
				field.set(alertView, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (isRoute) {

				// Toast.makeText(mContext, "确定导航", Toast.LENGTH_SHORT).show();
				String startLocStr = etStartLoc.getText().toString();
				String endLocStr = etEndLoc.getText().toString();

				if (!startLocStr.equals("") && !endLocStr.equals("")) {
					// 关闭对话框
					try {
						Field field = alertView.getClass().getDeclaredField(
								"isShowing");
						field.setAccessible(true);
						field.set(alertView, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					alertView.dismiss();

					stopRecognizer(); // 当对话框消失时停止语音服务
					stopSynthesizer();// 当对话框消失时停止语音合成
					startWakeUp(); // 日后注释掉
					// 跳转到导航界面，同时传值
					 Intent intent = new Intent();
					 intent.putExtra("operation", "ROUTE");
					 intent.putExtra("startLoc", startLocStr);
					 intent.putExtra("endLoc", endLocStr);
					 intent.setClass(mContext, MapActivity.class);
					 mContext.startActivity(intent);

				} else {
					isMsgEmpty = true;
					stopRecognizer();
					mTts.startSpeaking("起始和目的地都不能为空哦！", mTtsListener);
					Toast.makeText(mContext, "起始和目的地都不能为空哦！",
							Toast.LENGTH_SHORT).show();

				}

			}
			if (isLocation) {
				// Toast.makeText(mContext, "确定定位", Toast.LENGTH_SHORT).show();

				String locationStr = etLocation.getText().toString();
				if (!locationStr.equals("")) {
					// 关闭对话框
					try {
						Field field = alertView.getClass().getDeclaredField(
								"isShowing");
						field.setAccessible(true);
						field.set(alertView, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					alertView.dismiss();
					stopRecognizer(); // 当对话框消失时停止语音服务
					stopSynthesizer();// 当对话框消失时停止语音合成
					startWakeUp(); // 日后注释掉
					 //跳转到导航界面，同时传值
					 Intent intent = new Intent();
					 intent.putExtra("operation", "POSITION");
					 intent.putExtra("location", locationStr);
					 intent.setClass(mContext, MapActivity.class);
					 mContext.startActivity(intent);

				} else {
					isMsgEmpty = true;
					stopRecognizer();
					mTts.startSpeaking("位置不能为空哦！", mTtsListener);

					Toast.makeText(mContext, "位置不能为空哦！", Toast.LENGTH_SHORT)
							.show();
				}

			}
		}
		
		if(position == AlertView.CANCELPOSITION){
			// 关闭对话框
			try {
				Field field = alertView.getClass().getDeclaredField("isShowing");
				field.setAccessible(true);
				field.set(alertView, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			alertView.dismiss();
			stopRecognizer(); // 当对话框消失时停止语音服务
			stopSynthesizer();// 当对话框消失时停止语音合成
			startWakeUp();
			
		}
		

	}

	@Override
	public void onDismiss(Object o) {
		// TODO Auto-generated method stub
		// 关闭对话框
		try {
			Field field = alertView.getClass().getDeclaredField("isShowing");
			field.setAccessible(true);
			field.set(alertView, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		alertView.dismiss();
		stopRecognizer(); // 当对话框消失时停止语音服务
		stopSynthesizer();// 当对话框消失时停止语音合成
		startWakeUp();

	}

	/*
	 * 语音合成监听器，在这里处理语音合成结果
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		// 缓冲进度回调，arg0为缓冲进度，arg1为缓冲音频在文本中开始的位置，arg2为缓冲音频在文本中结束的位置，arg3为附加信息
		@Override
		public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
			// TODO Auto-generated method stub

		}

		// 会话结束回调接口，没有错误时error为空
		@Override
		public void onCompleted(SpeechError error) {
			// TODO Auto-generated method stub
			if (isMsgEmpty) {// 因为输入为空进来的
				isMsgEmpty = false;
				startRecognizer();

			} else {
				stopWakeUp();
				startRecognizer();
			}

		}

		// 开始播放
		@Override
		public void onSpeakBegin() {
			// TODO Auto-generated method stub

		}

		// 停止播放
		@Override
		public void onSpeakPaused() {
			// TODO Auto-generated method stub

		}

		// 播放进度回调,arg0为播放进度0-100；arg1为播放音频在文本中开始的位置，arg2为播放音频在文本中结束的位置。
		@Override
		public void onSpeakProgress(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		// 恢复播放回调接口
		@Override
		public void onSpeakResumed() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			// TODO Auto-generated method stub

		}

	};

	private RecognizerListener mRecoListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onEndOfSpeech() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(SpeechError arg0) {
			// TODO Auto-generated method stub
			startWakeUp();
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onResult(RecognizerResult result, boolean isLast) {
			// TODO Auto-generated method stub

			// Toast.makeText(mContext, result.getResultString(),
			// Toast.LENGTH_LONG).show();
			if (!isLast) {
				requestMsg += result.getResultString();
			} else {
				requestMsg += result.getResultString();

				try {
					JSONObject jsonMsg = new JSONObject(requestMsg);

					String rc = jsonMsg.getString("rc").toString();
					if (rc.equals("0") || rc.equals("4")) { // 成功
						String text = jsonMsg.getString("text").toString();
						if (text.contains("确定")) {
							Toast.makeText(mContext, "确定", Toast.LENGTH_SHORT)
							.show();
							VibrateHelp.playSound(mContext, 1);
							VibrateHelp.vSimple(mContext, 50);
							onItemClick(alertView, 0);
												
						} else if (text.contains("取消")) {
							Toast.makeText(mContext, "取消", Toast.LENGTH_SHORT)
							.show();
							VibrateHelp.playSound(mContext, 2);
							VibrateHelp.vSimple(mContext, 50);
							onDismiss(alertView);
							
						}
//						Toast.makeText(mContext, text, Toast.LENGTH_LONG)
//								.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				requestMsg = "";

			}

		}

		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub

		}

	};

	/**
	 * 开启语音识别服务
	 */
	private void startRecognizer() {
		stopWakeUp();
		mIat.startListening(mRecoListener);
	}

	/**
	 * 停止语音识别服务
	 */
	private void stopRecognizer() {
		if(mIat.isListening()){
			mIat.stopListening();
			mIat.cancel();
		}
		
	}

	/**
	 * 停止语音合成
	 */
	private void stopSynthesizer() {
		if(mTts.isSpeaking()){
			mTts.stopSpeaking();
			mTts.destroy();
		}
		
	}

	/**
	 * 开启语音唤醒
	 */
	private void startWakeUp() {
		stopRecognizer();
		HashMap params = new HashMap();
		params.put("kws-file", "assets:///WakeUp.bin"); // 设置唤醒资源，唤醒词需要到百度语音开放平台导出
		mWpEventManager.send("wp.start", new JSONObject(params).toString(),
				null, 0, 0);
	}

	/**
	 * 关闭语音唤醒
	 */
	private void stopWakeUp() {
		mWpEventManager.send("wp.stop", null, null, 0, 0);
	}

}
