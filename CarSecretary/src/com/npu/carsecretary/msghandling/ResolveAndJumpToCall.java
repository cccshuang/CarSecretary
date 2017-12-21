package com.npu.carsecretary.msghandling;


import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts;
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
import com.npu.carsecretary.util.SelectWhoToCall;
import com.npu.carsecretary.util.VibrateHelp;
import com.npu.carsecretary.view.AlertView;
import com.npu.carsecretary.view.OnDismissListener;
import com.npu.carsecretary.view.OnItemClickListener;

@SuppressWarnings("deprecation")
public class ResolveAndJumpToCall implements OnItemClickListener,
OnDismissListener{

	private Context mContext; // 上下文
	private EventManager mWpEventManager;// 百度语音唤醒服务
	
	private SpeechSynthesizer mTts;// 讯飞语音合成
	private SpeechRecognizer mIat;// 语音识别，不带UI
	public AlertView alertView;// 弹出的对话框
	
	private Handler handler = new Handler();
	
	private String inputName = "";
	private String[] codeAndName;
	private String code = "";
	private String requestMsg = "";
	
	private boolean isNormal = false;
	
	/**
	 * 构造函数
	 * 
	 * @param mContext
	 *            当前的上下文
	 * @param mWpEventManager
	 *            语音唤醒服务
	 */
	public ResolveAndJumpToCall(Context mContext, EventManager mWpEventManager) {
		super();
		this.mContext = mContext;
		this.mWpEventManager = mWpEventManager;

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
	
	private class SelectThreadInCall implements Runnable {

		public void run() {
			
			codeAndName = SelectWhoToCall.getPhoneByName(inputName,mContext);


			handler.post(new Runnable() {
				@Override
				public void run() {
					if(codeAndName[0]!=null && !codeAndName[0].equals("")){
						alertView = new AlertView("电话", codeAndName[1]+": "+codeAndName[0], "取消", null,
								new String[] { "确定" }, mContext, AlertView.Style.Alert,
								ResolveAndJumpToCall.this);
						code = codeAndName[0];
						alertView.show();
						isNormal = true;
						mTts.startSpeaking("您要拨打"+codeAndName[1]+"的号吗？", mTtsListener);	
					}else{
						isNormal = false;
						Toast.makeText(mContext, "没有找到要拨打的电话呀！", Toast.LENGTH_SHORT).show();
						mTts.startSpeaking("没有找到要拨打的电话呀！", mTtsListener);	
					}
						

				}
			});
		}
	}
	
	public void jumpByMenu(){
		Intent intent = new Intent();   					 
		intent.setAction(Intent.ACTION_VIEW);   					 
		intent.setData(Contacts.People.CONTENT_URI);   					 
		mContext.startActivity(intent);
		
	}

	
	/*
	 * 解析并根据解析结果来执行不同操作
	 */
	public void resolveAndJump(JSONObject requestMsg) throws JSONException {

			String service = requestMsg.getString("service").toString();
			String operation = requestMsg.getString("operation").toString();
			JSONObject semantic = requestMsg.getJSONObject("semantic");
			JSONObject slots;
			if(semantic.has("slots")){
				slots = semantic.getJSONObject("slots");							
			}else{
				if(requestMsg.getString("text").contains("打电话")){ //跳转到联系人界面
					Intent intent = new Intent();   					 
					intent.setAction(Intent.ACTION_VIEW);   					 
					intent.setData(Contacts.People.CONTENT_URI);   					 
					mContext.startActivity(intent);
				}else{
					isNormal = false;
					Toast.makeText(mContext, "您要打电话给谁？要描述清楚哦", Toast.LENGTH_SHORT).show();
					mTts.startSpeaking("您要打电话给谁？要描述清楚哦", mTtsListener);	
				}				
				return;
			}
				
		if (operation.equals("CALL")) { // 打电话
				if(slots.has("code")){
					code = slots.getString("code");
					alertView = new AlertView("电话", code, "取消", null,
							new String[] { "确定" }, mContext, AlertView.Style.Alert,
							this);
					alertView.show();
					isNormal = true;
					mTts.startSpeaking("您要拨打的号码为"+code+"吗？", mTtsListener);
				}else{
					if(slots.has("name")){
						inputName = slots.getString("name");
					}				
					new Thread(new SelectThreadInCall()).start();
	
				}
		} 

	}

	@Override
	public void onItemClick(Object o, int position) {
		// TODO Auto-generated method stub

		

		if (position != AlertView.CANCELPOSITION) {
			if(code!=null && !code.equals("")){
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + code));
				//开启系统拨号器
				mContext.startActivity(intent);	
			}else{
				isNormal = false;
				Toast.makeText(mContext, "输入的电话号码不对哦！", Toast.LENGTH_SHORT).show();
				mTts.startSpeaking("输入的电话号码不对哦！", mTtsListener);
			}
			alertView.dismiss();
			stopRecognizer(); // 当对话框消失时停止语音服务
			stopSynthesizer();// 当对话框消失时停止语音合成
			startWakeUp();
				
		}
			
		
		if(position == AlertView.CANCELPOSITION){
			alertView.dismiss();
			stopRecognizer(); // 当对话框消失时停止语音服务
			stopSynthesizer();// 当对话框消失时停止语音合成
			startWakeUp();
			
		}
		

	}

	@Override
	public void onDismiss(Object o) {
		// TODO Auto-generated method stub
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
			if(isNormal){
				stopWakeUp();
				startRecognizer();
			}else{
				stopRecognizer();
				startWakeUp();
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
