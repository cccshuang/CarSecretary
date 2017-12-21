package com.npu.carsecretary;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningTaskInfo;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.npu.carsecretary.R;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.npu.carsecretary.adapter.ChatLVAdapter;
import com.npu.carsecretary.adapter.MenuGVAdapter;
import com.npu.carsecretary.adapter.MenuVPAdapter;
import com.npu.carsecretary.bean.ChatInfo;
import com.npu.carsecretary.msghandling.LoadAndShowCarWeiZhang;
import com.npu.carsecretary.msghandling.ResolveAndJumpToCall;
import com.npu.carsecretary.msghandling.ResolveAndJumpToEmergencyCall;
import com.npu.carsecretary.msghandling.ResolveAndJumpToLaunchApp;
import com.npu.carsecretary.msghandling.ResolveAndJumpToMap;
import com.npu.carsecretary.msghandling.ResolveAndJumpToSendMsg;
import com.npu.carsecretary.msghandling.ResolveAndJumpToWeatherAndAir;
import com.npu.carsecretary.msghandling.ResolveAndOpenMusicPlayer;
import com.npu.carsecretary.msghandling.TalkToRobot;
import com.npu.carsecretary.userinfo.UserInfo;
import com.npu.carsecretary.util.VibrateHelp;
import com.npu.carsecretary.view.AlertView;
import com.npu.carsecretary.view.DropdownListView;
import com.npu.carsecretary.view.DropdownListView.OnRefreshListenerHeader;
import com.npu.carsecretary.view.OnItemClickListener;



/**
 * */
public class MainActivity extends Activity implements OnClickListener,
		OnRefreshListenerHeader,OnItemClickListener, OnDismissListener{

	private DropdownListView mListView; // 可下拉刷新的对话气泡ListView
	private ChatLVAdapter mLvAdapter;

	private EditText input; // 输入框
	private Button send; // 发送文字信息按钮
	private Button send_voice; // 发送
	private Button to_userinfo;

	private ViewPager mViewPager;// 菜单栏的滑动
	private LinearLayout mDotsLayout; // 表示菜单图标所在的点
	private LinearLayout chat_menu_container; // 菜单栏容器
	private Button open_menu; // 菜单图标
	private int columns = 4; // 菜单列数
	private int rows = 2; // 菜单行数
	
	private AlertView mAlertView;//避免创建重复View，先创建View，然后需要的时候show出来，推荐这个做法

	private List<View> views = new ArrayList<View>();
	private List<String> staticMenusList;
	private LinkedList<ChatInfo> infos = new LinkedList<ChatInfo>();

	private SimpleDateFormat sd;
	// 返回主线程更新数据
	private static Handler handler = new Handler();

	private String requestMsg = ""; // 用户发送的信息
	private String replyMsg = ""; // 机器人回复的信息
	private String tempRequestToTuling = "";
	private String curCityName = ""; //当前所在城市
	private String curPosition = ""; //当前所在位置

	private SpeechSynthesizer mTts;// 讯飞语音合成服务
	private RecognizerDialog iatDialog; // 讯飞语音识别服务
	private EventManager mWpEventManager;// 百度语音唤醒服务
	private TextUnderstander txtUndestander;
	
	public LocationClient mLocationClient = null;//定位服务
	public MyLocationListenner myListener = new MyLocationListenner();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		initStaticMenus();
		initViews();

		// String param = "appid=53c4c169,lib_name=libmsc_cfl_1102.so";
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=57f4e6eb");

		// 设置讯飞语音合成
		mTts = SpeechSynthesizer.createSynthesizer(this, null);
		set_mTts();

		// 设置讯飞语音识别
		iatDialog = new RecognizerDialog(this, null);
		setIatDialog();

		// 设置并开启百度语音唤醒服务
		mWpEventManager = EventManagerFactory.create(MainActivity.this, "wp"); // 创建唤醒事件管理器
		setWakeUp();
		startWakeUp();
		
		txtUndestander = TextUnderstander.createTextUnderstander(this, null);
	    
		//启动定位服务
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener( myListener );
		setLocationOption();
		mLocationClient.start();
	
	}
	
	// mLocationClient.stop();
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			curCityName = location.getCity();
			curPosition = location.getAddrStr();
			if(curCityName.equals("")||curPosition.equals("")){
				Toast.makeText(MainActivity.this, "获取定位失败！", Toast.LENGTH_SHORT).show();
			}
			
			mLocationClient.stop();
		}
	}

	// 设置相关参数
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
	}


	/*
	 * 初始菜单
	 */
	private void InitViewPager() {
		// 获取页数
		for (int i = 0; i < getPagerCount(); i++) {
			views.add(viewPagerItem(i));
			LayoutParams params = new LayoutParams(16, 16);
			mDotsLayout.addView(dotsItem(i), params);
		}
		MenuVPAdapter mVpAdapter = new MenuVPAdapter(views);
		mViewPager.setAdapter(mVpAdapter);
		mDotsLayout.getChildAt(0).setSelected(true);
	}

	/*
	 * 初始化刚进入应用时的页面
	 */
	@SuppressLint("SimpleDateFormat")
	private void initViews() {
		mListView = (DropdownListView) findViewById(R.id.message_chat_listview);
		sd = new SimpleDateFormat("HH:mm");
		// 模拟收到信息
		infos.add(getChatInfoFrom("主人你好，我是小米！"));
		infos.add(getChatInfoFrom("对我说'小米同学'便可唤醒我哦！"));
		mLvAdapter = new ChatLVAdapter(this, infos);
		mListView.setAdapter(mLvAdapter);
		// 菜单图标
		open_menu = (Button) findViewById(R.id.open_menu);
		// 菜单布局
		chat_menu_container = (LinearLayout) findViewById(R.id.chat_menu_container);
		mViewPager = (ViewPager) findViewById(R.id.menu_viewpager);
		mViewPager.setOnPageChangeListener(new PageChange());
		// 菜单下小圆点
		mDotsLayout = (LinearLayout) findViewById(R.id.menu_dots_container);
		input = (EditText) findViewById(R.id.input_sms);
		input.setOnClickListener(this);
		send = (Button) findViewById(R.id.send_sms);
		send_voice = (Button) findViewById(R.id.send_voice);
		InitViewPager();
		// 菜单按钮
		open_menu.setOnClickListener(this);
		// 发送
		send.setOnClickListener(this);
		// 开启语音button
		send_voice.setOnClickListener(this);

		mListView.setOnRefreshListenerHead(this);

		// 点击屏幕底部菜单栏消失
		mListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					hideSoftInputView();
					if (chat_menu_container.getVisibility() == View.VISIBLE) {
						chat_menu_container.setVisibility(View.GONE);
					}
				}

				return false;
			}
		});
		
		to_userinfo = (Button) findViewById(R.id.goUserinfo_bt);
		to_userinfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						UserInfo.class);
				// 启动intent对应的Activity
				startActivity(intent);
			}
		});

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.input_sms:// 输入框
			if (chat_menu_container.getVisibility() == View.VISIBLE) {
				chat_menu_container.setVisibility(View.GONE);
			}
			break;
		case R.id.open_menu:// 菜单
			hideSoftInputView();// 隐藏软键盘
			if (chat_menu_container.getVisibility() == View.GONE) {
				chat_menu_container.setVisibility(View.VISIBLE);
			} else if (chat_menu_container.getVisibility() == View.VISIBLE) {
				chat_menu_container.setVisibility(View.GONE);
			}
			break;
		case R.id.send_sms:// 发送文字
			requestMsg = input.getText().toString();
			tempRequestToTuling = requestMsg;
			if (!TextUtils.isEmpty(requestMsg)) {
				infos.add(getChatInfoTo(requestMsg)); // 包装信息
				mLvAdapter.setList(infos); // 加入对话气泡的适配器
				mLvAdapter.notifyDataSetChanged(); // 通知对话气泡刷新
				mListView.setSelection(infos.size() - 1);
				input.setText("");
				boolean isIcon = checkIfMenu(requestMsg);
				if(!isIcon){
					txtUndestander.understandText(requestMsg, txtUnderstanderListenner);
				}	
				//new Thread(new MsgThread()).start(); // 开子线程与智能机器人网络通信
			} else {
				Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show(); // 判空
			}
			break;
		case R.id.send_voice: // 发送语音
			hideSoftInputView();
			if (chat_menu_container.getVisibility() == View.VISIBLE) {
				chat_menu_container.setVisibility(View.GONE);
			}
			send_voice.setBackgroundResource(R.drawable.speak_now);
			stopWakeUp(); // 停止语音唤醒，防止录音时冲突
			// 执行录音时的震动和提示声
			VibrateHelp.playSound(this,0);
			VibrateHelp.vSimple(this, 30);
			showIatDialog();
			break;

		default:
			break;
		}
	}
	
	private boolean checkIfMenu(String msg){
		boolean is = false;
		if(msg.contains("导航")){
			msg = "导航";
		}
		if(msg.contains("回家")){
			msg = "回家";
		}
		if(msg.contains("去公司")){
			msg = "去公司";
		}
		if(msg.contains("违章查询")){
			msg = "违章查询";
		}
		if(msg.contains("应急电话")){
			msg = "应急电话";
		}
		switch (msg) {
		case "导航":
			new ResolveAndJumpToMap(MainActivity.this,mWpEventManager).jumpByMenuRo(curPosition);
			is =  true;
			break;
		case "电话":
			new ResolveAndJumpToCall(MainActivity.this,mWpEventManager).jumpByMenu();
			is =  true;
			break;
		case "短信":
			new ResolveAndJumpToSendMsg(MainActivity.this,mWpEventManager).jumpByMenu();
			is =  true;
			break;
		case "天气":
			txtUndestander.understandText(curCityName + "的天气", txtUnderstanderListenner);
			is =  false;
			break;
		case "空气质量":
			txtUndestander.understandText(curCityName + "的空气质量", txtUnderstanderListenner);
			is =  false; //因为还要借助语义理解
			break;
		case "定位":
			new ResolveAndJumpToMap(MainActivity.this,mWpEventManager).jumpByMenuPo(curPosition);
			is =  true;
			break;
		case "应急电话":
			new ResolveAndJumpToEmergencyCall(MainActivity.this).showEmergencyCall();
			is =  true;
			break;
		case "音乐":
			txtUndestander.understandText("播放音乐。", txtUnderstanderListenner);
			is =  false; //因为还要借助语义理解
			break;
		case "违章查询":
			SharedPreferences preferences0;
			SharedPreferences.Editor editor0;
			preferences0 = getSharedPreferences("user_car_info",MODE_PRIVATE);
			editor0 = preferences0.edit();
			String plateNo = preferences0.getString("User_car_num", "");
			String engineNo = preferences0.getString("User_car_eng", "");
			if(!plateNo.equals("")){
				plateNo = plateNo.substring(1,plateNo.length());
			}
			new LoadAndShowCarWeiZhang(MainActivity.this).showWeiZhangInfo(plateNo,engineNo);
			is =  true;
			break;
		case "回家":
			SharedPreferences preferences1;
			SharedPreferences.Editor editor1;
			preferences1 = getSharedPreferences("user_home_info", MODE_PRIVATE);
			editor1 = preferences1.edit();
			String endLocHome = preferences1.getString("User_home_address", "");
			new ResolveAndJumpToMap(MainActivity.this,mWpEventManager).jumpByMenuRo(curPosition,endLocHome);
			is =  true;
			break;
		case "去公司":
			SharedPreferences preferences2;
			SharedPreferences.Editor editor2;
			preferences2 = getSharedPreferences("user_firm_info", MODE_PRIVATE);
			editor2 = preferences2.edit();
			String endLocFirm = preferences2.getString("User_firm_address", "");
			new ResolveAndJumpToMap(MainActivity.this,mWpEventManager).jumpByMenuRo(curPosition,endLocFirm);
			is =  true;
			break;		

		default:
			break;
		}
		
		return is;
		
	}

	private View viewPagerItem(int position) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.menu_gridview, null);// 菜单布局
		GridView gridview = (GridView) layout.findViewById(R.id.chart_menu_gv);

		List<String> subList = new ArrayList<String>();
		subList.addAll(staticMenusList
				.subList(position * (columns * rows),
						(columns * rows) * (position + 1) > staticMenusList
								.size() ? staticMenusList.size()
								: (columns * rows) * (position + 1)));

		MenuGVAdapter mGvAdapter = new MenuGVAdapter(subList, this);
		gridview.setAdapter(mGvAdapter);
		gridview.setNumColumns(columns);
		// 单击菜单执行的操作
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				requestMsg = ((TextView) ((LinearLayout) view).getChildAt(1))
						.getText().toString();
				tempRequestToTuling = requestMsg;
				if (!TextUtils.isEmpty(requestMsg)) {
					infos.add(getChatInfoTo(requestMsg));
					mLvAdapter.setList(infos);
					mLvAdapter.notifyDataSetChanged();
					mListView.setSelection(infos.size() - 1);
					input.setText("");
					//new Thread(new MsgThread()).start();
					boolean isIcon = checkIfMenu(requestMsg);
					if(!isIcon){
					txtUndestander.understandText(requestMsg, txtUnderstanderListenner);
					}
				}
						
			}
		});

		return gridview;
	}

	private ImageView dotsItem(int position) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dot_image, null);
		ImageView iv = (ImageView) layout.findViewById(R.id.menu_dot);
		iv.setId(position);
		return iv;
	}

	/**
	 * 根据菜单数量以及GridView设置的行数和列数计算Pager数量
	 * 
	 * @return
	 */
	private int getPagerCount() {
		int count = staticMenusList.size();
		return count % (columns * rows) == 0 ? count / (columns * rows) : count
				/ (columns * rows) + 1;
	}

	/**
	 * 初始化菜单列表staticMenusList
	 */
	private void initStaticMenus() {
		try {
			staticMenusList = new ArrayList<String>();
			String[] menus = getAssets().list("menu_pic/png");
			// 将Assets中的菜单名称转为字符串一一添加进staticMenusList
			for (int i = 0; i < menus.length; i++) {

				staticMenusList.add(menus[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 表情页改变时，dots效果也要跟着改变
	 * */
	class PageChange implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
				mDotsLayout.getChildAt(i).setSelected(false);
			}
			mDotsLayout.getChildAt(arg0).setSelected(true);
		}

	}

	/**
	 * 发送的信息
	 * 
	 * @param message
	 * @return
	 */
	private ChatInfo getChatInfoTo(String message) {
		ChatInfo info = new ChatInfo();
		info.content = message;
		info.fromOrTo = 1;
		info.time = sd.format(new Date());
		return info;
	}

	/**
	 * 接收的信息
	 * 
	 * @param message
	 * @return
	 */
	private ChatInfo getChatInfoFrom(String message) {
		ChatInfo info = new ChatInfo();
		info.content = message;
		info.fromOrTo = 0;
		info.time = sd.format(new Date());
		return info;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mLvAdapter.setList(infos);
				mLvAdapter.notifyDataSetChanged();
				mListView.onRefreshCompleteHeader();
				break;
			}
		}
	};

	/*
	 * 弹出语音识别对话框，开启语音识别服务，等待用户语音输入
	 */
	private void showIatDialog() {
		if (mTts.isSpeaking()) { // 停止当前语音播放
			mTts.stopSpeaking();
		}
		iatDialog.show();
	}

	/*
	 * 设置语音识别参数及语义理解参数
	 */
	private void setIatDialog() {
		iatDialog.setParameter(SpeechConstant.DOMAIN, "iat");
		iatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		iatDialog.setParameter(SpeechConstant.ACCENT, "mandarin ");
		iatDialog.setParameter(SpeechConstant.RESULT_TYPE, "json"); // 返回值的类型
		iatDialog.setParameter(SpeechConstant.NLP_VERSION, "2.0"); // 语义理解参数
		iatDialog.setParameter(SpeechConstant.PARAMS, "sch=1"); // 语义理解参数

		// iatDialog.setParameter(SpeechConstant.ASR_PTT, "o");//设置是否有标点符号

		iatDialog.setListener(recognizerDialogListener); // 开始听写

		iatDialog.setOnDismissListener(new OnDismissListener() { // 点击对话框外，在这里即取消识别后要执行的事件
					@Override
					public void onDismiss(DialogInterface arg0) {
						// TODO Auto-generated method stub
						send_voice.setBackgroundResource(R.drawable.speak_wait);
						startWakeUp(); // 如果取消了录音则重新开启语音唤醒服务
					}
				});

	}

	/*
	 * 语音识别监听器，在这里处理识别的结果
	 */
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {

		@Override
		public void onError(SpeechError arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onResult(RecognizerResult recognizerResult, boolean isLast) {
			// TODO Auto-generated method stub

			// 识别数据可能多次返回，处理返回的字符串，进行语义理解处理
			if (!isLast) {
				requestMsg += recognizerResult.getResultString();
			} else {
				requestMsg += recognizerResult.getResultString();
				try {

					JSONObject jsonMsg = new JSONObject(requestMsg);
					tempRequestToTuling = jsonMsg.getString("text");
					checkGoToWhere(jsonMsg,true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					/************************** DEBUG ****************************************/
//					infos.add(getChatInfoTo(e.toString()));
//					mLvAdapter.setList(infos);
//					mLvAdapter.notifyDataSetChanged();
//					mListView.setSelection(infos.size() - 1);
//					input.setText("");
//					new Thread(new MsgThread()).start();
					/************************** DEBUG ****************************************/
				}
				
				
				requestMsg = "";

			}

		}

	};
	
	protected void onResume() {
		super.onResume();
		startWakeUp();
		//Toast.makeText(this, "reusme", Toast.LENGTH_LONG).show();
	};
	protected void onPause() {
		super.onPause();
		stopWakeUp();
		//Toast.makeText(this, "Pause", Toast.LENGTH_LONG).show();
	};
	
	

	/*
	 * 语音合成参数
	 */
	private void set_mTts() {
		// 设置发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, "nannan");

		// 设置语速
		mTts.setParameter(SpeechConstant.SPEED, "80");

		// 设置音调
		mTts.setParameter(SpeechConstant.PITCH, "80");

		// 设置音量0-100
		mTts.setParameter(SpeechConstant.VOLUME, "100");

		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 如果不需要保存保存合成音频，请注释下行代码
		// mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
		// "./sdcard/iflytek.pcm");

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
			startWakeUp();

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

	/*
	 * 用来注册和设置百度语音唤醒服务
	 */
	private void setWakeUp() {

		// 注册唤醒事件监听器
		mWpEventManager.registerListener(new EventListener() {
			@Override
			public void onEvent(String name, String params, byte[] data,
					int offset, int length) {
				try {
					JSONObject json = new JSONObject(params);
					if ("wp.data".equals(name)) { // 每次唤醒成功,将会回调name=wp.data的事件,被激活的唤醒词在params的word字段
						String word = json.getString("word"); // 唤醒词
//						Toast.makeText(MainActivity.this, word,
//								Toast.LENGTH_SHORT).show();
						// 每次当语音唤醒成功，停止语音唤醒，防止录音端口占用，并开启讯飞语音识别服务
						hideSoftInputView();
						if (chat_menu_container.getVisibility() == View.VISIBLE) {
							chat_menu_container.setVisibility(View.GONE);
						}
						send_voice.setBackgroundResource(R.drawable.speak_now);

						stopWakeUp();
				
						// 执行录音时的震动和提示声
						VibrateHelp.playSound(MainActivity.this,0);
						VibrateHelp.vSimple(MainActivity.this, 30);
						showIatDialog();
					} else if ("wp.exit".equals(name)) { // 唤醒已经停止
//						Toast.makeText(MainActivity.this, "唤醒已经停止",
//								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					throw new AndroidRuntimeException(e);
				}
			}
		});
	}

	/*
	 * 用于开启百度语音唤醒服务
	 */
	private void startWakeUp() {
		// 通知唤醒管理器, 启动唤醒功能
		HashMap params = new HashMap();
		params.put("kws-file", "assets:///WakeUp.bin"); // 设置唤醒资源，唤醒词需要到百度语音开放平台导出
		mWpEventManager.send("wp.start", new JSONObject(params).toString(),
				null, 0, 0);
	}

	/*
	 * 用于停止百度语音唤醒服务
	 */
	private void stopWakeUp() {
		mWpEventManager.send("wp.stop", null, null, 0, 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTts.stopSpeaking();
		mTts.destroy();// 退出时释放连接
		stopWakeUp();
		super.onDestroy();
	}

	@Override
	public void onRefresh() {

		new Thread() {
			@Override
			public void run() {
				try {
					sleep(1000);
					Message msg = mHandler.obtainMessage(0);
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
	}

	/*
	 * 隐藏软键盘
	 */
	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/*
	 * 与图灵机器人连接的内部类
	 */
	private class MsgThread implements Runnable {

		public void run() {
			try {
					replyMsg = TalkToRobot.receiveMsg(tempRequestToTuling);		

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tempRequestToTuling = "";
			requestMsg = "";

			handler.post(new Runnable() {
				@Override
				public void run() {
					infos.add(getChatInfoFrom(replyMsg));
					mLvAdapter.setList(infos);
					mLvAdapter.notifyDataSetChanged();
					mListView.setSelection(infos.size() - 1);
					mTts.startSpeaking(replyMsg, mTtsListener);
					replyMsg = "";
					

				}
			});
		}
	}
	
	/**
	 * 文本语义理解监听
	 */
	TextUnderstanderListener txtUnderstanderListenner = new TextUnderstanderListener(){

		@Override
		public void onError(SpeechError arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onResult(UnderstanderResult understanderResult) {
			// TODO Auto-generated method stub
			// 识别数据可能多次返回，处理返回的字符串，进行语义理解处理
				requestMsg = understanderResult.getResultString();
				try {

					JSONObject jsonMsg = new JSONObject(requestMsg);
					checkGoToWhere(jsonMsg,false);
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					/************************** DEBUG ****************************************/
//					infos.add(getChatInfoTo(e.toString()));
//					mLvAdapter.setList(infos);
//					mLvAdapter.notifyDataSetChanged();
//					mListView.setSelection(infos.size() - 1);
//					input.setText("");
//					new Thread(new MsgThread()).start();
					/************************** DEBUG ****************************************/
				}
				
				
				requestMsg = "";

		}
			
	
		
	};

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(Object o, int position) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
		
	}
	
	private void checkGoToWhere(JSONObject jsonMsg,boolean isVoice) throws JSONException{
		String rc = jsonMsg.getString("rc").toString();

		if (rc.equals("1") || rc.equals("2")) { // 语义理解系统级错误
			infos.add(getChatInfoFrom("对不起，这次的服务出了点小错误，请谅解！"));
			mLvAdapter.setList(infos);
			mLvAdapter.notifyDataSetChanged();
			mListView.setSelection(infos.size() - 1);
			mTts.startSpeaking("对不起，这次的服务出了点小错误，请谅解！", mTtsListener);
			return;
		} else if (rc.equals("3") || rc.equals("4")) { // 语义解析服务出错或无对应领域
			

			boolean isMenu = checkIfMenu(tempRequestToTuling);
			if(isVoice){
				infos.add(getChatInfoTo(tempRequestToTuling));
				mLvAdapter.setList(infos);
				mLvAdapter.notifyDataSetChanged();
				mListView.setSelection(infos.size() - 1);
				input.setText("");	
			}
			if(!isMenu){
				if(tempRequestToTuling.contains("空气质量")||tempRequestToTuling.contains("pm2.5")){
					txtUndestander.understandText(curCityName + "的空气质量", txtUnderstanderListenner);
				}else{
					new Thread(new MsgThread()).start();
				}
				
			}
			
//			infos.add(getChatInfoFrom("不好意思，这个问题我不太懂"));
//			mLvAdapter.setList(infos);
//			mLvAdapter.notifyDataSetChanged();
//			mListView.setSelection(infos.size() - 1);
//			mTts.startSpeaking("不好意思，这个问题我不太懂", mTtsListener);
			return;

		} else if (rc.equals("0")) { // 成功
			String textTo = jsonMsg.getString("text").toString();
			boolean isMenu = checkIfMenu(textTo);
			if(isVoice){
				infos.add(getChatInfoTo(textTo));
				mLvAdapter.setList(infos);
				mLvAdapter.notifyDataSetChanged();
				mListView.setSelection(infos.size() - 1);
				input.setText("");
				
			}
			
			
			String service = jsonMsg.getString("service")
					.toString();
			String operation = jsonMsg.getString("operation")
					.toString();
			

			switch (operation) {
			case "ANSWER":
				if(isMenu){
					return;
				}
				String textMsg = "";
				if(jsonMsg.has("answer")){
					JSONObject answer = jsonMsg.getJSONObject("answer");
					textMsg = answer.getString("text");
				}else{
					textMsg = "这个问题我不是很懂！";
				}
				if(textMsg.equals("")) textMsg = "这个问题我不是很懂！";
				infos.add(getChatInfoFrom(textMsg));
				mLvAdapter.setList(infos);
				mLvAdapter.notifyDataSetChanged();
				mListView.setSelection(infos.size() - 1);
				input.setText("");	
				mTts.startSpeaking(textMsg, mTtsListener);
				break;
			case "ROUTE": // 获取某个地点位置
				if(isMenu){
					return;
				}
				new ResolveAndJumpToMap(MainActivity.this,mWpEventManager).resolveAndJump(jsonMsg,curCityName,curPosition);
				break;
			case "POSITION"://获取某个地点位置
				new ResolveAndJumpToMap(MainActivity.this,mWpEventManager).resolveAndJump(jsonMsg,curCityName,curPosition);
				break;
			case "CALL"://拨打电话
				//Toast.makeText(this, "call", Toast.LENGTH_SHORT).show();
				new ResolveAndJumpToCall(MainActivity.this,mWpEventManager).resolveAndJump(jsonMsg);
				break;
			case "SEND":
				new ResolveAndJumpToSendMsg(MainActivity.this,mWpEventManager).resolveAndJump(jsonMsg);
				break;
			case "LAUNCH":
				new ResolveAndJumpToLaunchApp(MainActivity.this,mWpEventManager).resolveAndJump(jsonMsg);
				break;
			case "QUERY":
				JSONObject semantic = jsonMsg.getJSONObject("semantic");
				JSONObject slots = semantic.getJSONObject("slots");
				JSONObject location = slots.getJSONObject("location");
				String cityInPara = location.getString("city");
				if(service.equals("weather")){
					if(!cityInPara.equals("CURRENT_CITY")){
						new ResolveAndJumpToWeatherAndAir(MainActivity.this).showWeatherInfo(jsonMsg);
					}else{
						txtUndestander.understandText(curCityName + "的天气", txtUnderstanderListenner);
					}
					
					
				}else if(service.equals("pm25")){
					//Toast.makeText(MainActivity.this, jsonMsg.toString(), Toast.LENGTH_LONG).show();
					if(!cityInPara.equals("CURRENT_CITY")){
						new ResolveAndJumpToWeatherAndAir(MainActivity.this).showAirInfo(jsonMsg);
					}else{
						txtUndestander.understandText(curCityName + "的空气质量", txtUnderstanderListenner);
					}
					
					
				}
				break;
			case "PLAY":
				if(service.equals("music")){
					JSONObject data = jsonMsg.getJSONObject("data");
					JSONArray result;
					if (data.has("result")) {				
						if(data.getString("result").equals("{}")){
							infos.add(getChatInfoFrom("不好意思，暂时还没有该歌曲资源"));
							mLvAdapter.setList(infos);
							mLvAdapter.notifyDataSetChanged();
							mListView.setSelection(infos.size() - 1);
							input.setText("");	
							mTts.startSpeaking("不好意思，暂时还没有该歌曲资源", mTtsListener);
							break;
						}else{
							new ResolveAndOpenMusicPlayer(MainActivity.this,getApplicationContext()).resolveAndOpen(jsonMsg);
						}
					}
				
				
				}
			break;
				
				

			default:
				break;
			}
		}
	}
	
	int backKeyClick = 0;
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	switch (keyCode) {
	    	 case KeyEvent.KEYCODE_BACK:
	    		 if(backKeyClick == 0){
	    			 AlertView alertViewExit = new AlertView("提示","确认退出程序？" , "取消", null,
		    					new String[] { "确定" }, MainActivity.this, AlertView.Style.Alert,
		    					new OnItemClickListener() {
									
									@Override
									public void onItemClick(Object o, int position) {
										// TODO Auto-generated method stub
										if (position != AlertView.CANCELPOSITION){
											finish();
										}
										if (position == AlertView.CANCELPOSITION){
											backKeyClick = 0;
										}
									}
								}); 
		    		alertViewExit.show();
		    		backKeyClick++;	    			 
	    		 }
	    		
	             break;
	    	}
	    	return false;	
	    }



}
