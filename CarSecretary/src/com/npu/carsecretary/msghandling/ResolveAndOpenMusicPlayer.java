package com.npu.carsecretary.msghandling;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.npu.carsecretary.MainActivity;
import com.npu.carsecretary.R;
import com.npu.carsecretary.bean.MusicInfo;
import com.npu.carsecretary.util.Player;
import com.npu.carsecretary.view.AlertView;
import com.npu.carsecretary.view.OnDismissListener;
import com.npu.carsecretary.view.OnItemClickListener;

public class ResolveAndOpenMusicPlayer implements View.OnTouchListener{

	private Context mContext; // 上下文
	private Context appContext;
	private ArrayList<MusicInfo> downloadUrlList = new ArrayList<MusicInfo>();
	
	private WindowManager wm;
	private WindowManager.LayoutParams layoutParams;
	
	private ViewGroup extView;
	private ImageView img_play;
	private ImageView img_pre;
	private ImageView img_next;
	private ImageView img_toSmallBt;
	private Button img_del;
	private TextView txt_music_name;
	private SeekBar musicProgress;
	
	private ViewGroup smallView;
	private ImageView small_bt;
	private TextView txt_music_smallName;
	
	private Player player;
	
	private boolean isSmall = false;

	private boolean isPlaying = false;
	private boolean isfirstPlay = true;//一首歌是第一次播放还是暂停后继续放
	
	private int curUrlPos = 0;
	

	

	public ResolveAndOpenMusicPlayer(Context mContext,Context appContext) {
		super();
		this.mContext = mContext;
		this.appContext = appContext;
	}
	
	public void stopMusic(){
		if(player.mediaPlayer.isPlaying()){
			img_play.performClick();
		}
		
	}

	/*
	 * 解析并根据解析结果来执行不同操作
	 */
	public void resolveAndOpen(JSONObject requestMsg) throws JSONException {

		String service = requestMsg.getString("service").toString();
		String operation = requestMsg.getString("operation").toString();
		JSONObject data = requestMsg.getJSONObject("data");
		JSONArray result;
		if (data.has("result")) {
			//Toast.makeText(mContext, data.getString("result")+" data", Toast.LENGTH_SHORT).show();
			if(data.getString("result").equals("{}")){
				Toast.makeText(mContext, "不好意思，没有找到歌曲", Toast.LENGTH_SHORT).show();
				return;
			}
			result = data.getJSONArray("result");
			if(result == null || result.length() == 0){
				Toast.makeText(mContext, "不好意思，没有找到歌曲", Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			Toast.makeText(mContext, "不好意思，没有找到歌曲", Toast.LENGTH_SHORT).show();
			return;
		}

		for (int i = 0; i < result.length(); i++) {
			JSONObject musicObj = (JSONObject) result.get(i);
			String musicUrl = "";
			String musicName = "未知歌曲";
			String musicSinger = "未知歌手";
			if (musicObj.has("downloadUrl")) {
				musicUrl = musicObj.getString("downloadUrl");

			}
			if (musicObj.has("name")) {
				musicName = musicObj.getString("name");

			}
			if (musicObj.has("singer")) {
				musicSinger = musicObj.getString("singer");
			}
			MusicInfo musicInfo = new MusicInfo(musicName, musicUrl, musicSinger);
			downloadUrlList.add(musicInfo);
			if(result.length() > 1){
				curUrlPos = 1;
			}
		}
		
		wm = (WindowManager) appContext.getSystemService(
				Context.WINDOW_SERVICE);
		//Toast.makeText(mContext, "162", Toast.LENGTH_SHORT).show();
		layoutParams = new WindowManager.LayoutParams();
		layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.alpha = 1.0f;	
		layoutParams.format = PixelFormat.RGBA_8888;
		// 这里是关键，使控件始终在最上方
		layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
				| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		
		// 创建自定义的View
		
		

				extView = (ViewGroup) LayoutInflater.from(mContext).inflate(
						R.layout.music_player, null);
				txt_music_name = (TextView)extView.findViewById(R.id.music_name);
				img_play = (ImageView) extView.findViewById(R.id.music_play);
				img_pre = (ImageView) extView.findViewById(R.id.music_pre);
				img_next = (ImageView) extView.findViewById(R.id.music_next);
				img_toSmallBt = (ImageView) extView.findViewById(R.id.music_tosmall);
				img_del = (Button) extView.findViewById(R.id.music_del);
				musicProgress = (SeekBar)extView.findViewById(R.id.music_progress);
				player = new Player(musicProgress);
				musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
				img_play.setOnClickListener(new controlOnClickListenner());
				img_pre.setOnClickListener(new controlOnClickListenner());
				img_next.setOnClickListener(new controlOnClickListenner());
				img_toSmallBt.setOnClickListener(new controlOnClickListenner());
				img_del.setOnClickListener(new controlOnClickListenner());
				extView.setOnTouchListener(this);
				
				

				smallView = (ViewGroup) LayoutInflater.from(mContext).inflate(
						R.layout.music_small_bt, null);
				small_bt = (ImageView) smallView.findViewById(R.id.music_small_img);
				txt_music_smallName = (TextView)smallView.findViewById(R.id.musicname_small_txt);
				small_bt.setOnTouchListener(this);
				small_bt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						wm.removeView(smallView);
						isSmall = false;
						layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
						layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
						layoutParams.alpha = 1.0f;
						//txt_music_smallName.setText(downloadUrlList.get(curUrlPos).name+"-"+downloadUrlList.get(curUrlPos).singer);
						wm.addView(extView, layoutParams);

					}
				});
				
				wm.addView(extView, layoutParams);
				img_play.performClick();
				//Toast.makeText(mContext, "162", Toast.LENGTH_SHORT).show();
		

	}
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// getRawX/Y 是获取相对于Device的坐标位置 注意区别getX/Y[相对于View]
			layoutParams.x = (int) event.getRawX();
			layoutParams.y = (int) event.getRawY();
			// 更新"桌面歌词"的位置
			if (isSmall) {
				wm.updateViewLayout(smallView, layoutParams);
			} else {
				wm.updateViewLayout(extView, layoutParams);
			}
			// 下面的removeView 可以去掉"桌面歌词"
			// wm.removeView(myView);
			break;
		case MotionEvent.ACTION_MOVE:
			layoutParams.x = (int) event.getRawX();
			layoutParams.y = (int) event.getRawY();
			if (isSmall) {
				wm.updateViewLayout(smallView, layoutParams);
			}
			else {
				wm.updateViewLayout(extView, layoutParams);
			}
			break;
		}
		return false;
	}

	private class controlOnClickListenner implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.music_play:// 播放
				if (!isPlaying) {// 未播放时
					isPlaying = true;
					img_play.setImageDrawable(mContext.getResources().getDrawable(
							R.drawable.widget_pause_button));
					if(isfirstPlay){
								
						if(!downloadUrlList.get(curUrlPos).url.equals("")){
							txt_music_name.setText(downloadUrlList.get(curUrlPos).name+"_"+downloadUrlList.get(curUrlPos).singer);
							txt_music_smallName.setText(downloadUrlList.get(curUrlPos).name+"_"+downloadUrlList.get(curUrlPos).singer);
							new Thread(new Runnable() {

								@Override
								public void run() {
									player.playUrl(downloadUrlList.get(curUrlPos).url);
								}
							}).start();
							isfirstPlay = false;
						}else{
							Toast.makeText(mContext, "播放失败", Toast.LENGTH_SHORT).show();
						}
						
					}else{
						player.reStart();
					}
					
				} else {// 正在播放时
					isPlaying = false;
					img_play.setImageDrawable(mContext.getResources().getDrawable(
							R.drawable.widget_play_button));
					player.pause();
				}
				break;
			case R.id.music_pre:// 上一首
				if(player.mediaPlayer.isPlaying()){
					player.mediaPlayer.stop();
					isPlaying = false;
				}
				curUrlPos = (curUrlPos-1+downloadUrlList.size())%downloadUrlList.size();
				Log.e(curUrlPos+"", "Pos");
				isfirstPlay = true;
				img_play.performClick();
				

				break;
			case R.id.music_next:// 下一首
				if(player.mediaPlayer.isPlaying()){
					player.mediaPlayer.stop();
					isPlaying = false;
				}
				curUrlPos = (curUrlPos+1)%downloadUrlList.size();
				isfirstPlay = true;
				img_play.performClick();
				break;
			case R.id.music_del:
				if (player != null) {
					player.stop();
					player = null;
				}
				wm.removeView(extView);
				break;
			case R.id.music_tosmall:
				// Toast.makeText(MainActivity.this, "hao",
				// Toast.LENGTH_LONG).show();
				wm.removeView(extView);
				isSmall = true;
				layoutParams.width = 240;
				layoutParams.height = 240;
				layoutParams.alpha = 0.8f;
				wm.addView(smallView, layoutParams);
				break;

			default:
				break;
			}

		}

	}
	
	class SeekBarChangeEvent implements OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
			this.progress = progress * player.mediaPlayer.getDuration()
					/ seekBar.getMax();
			if(progress >= 98){
				isPlaying = false;	
				isfirstPlay = true;
				img_play.setImageDrawable(mContext.getResources().getDrawable(
						R.drawable.widget_play_button));
				img_next.performClick();
			}
			
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
			player.mediaPlayer.seekTo(progress);
		}

	}

}
