package com.npu.carsecretary.userinfo;

import com.npu.carsecretary.MainActivity;
import com.npu.carsecretary.R;
import com.npu.carsecretary.view.AlertView;
import com.npu.carsecretary.view.OnItemClickListener;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class UserCarInfo extends Activity{

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_car);
        preferences = getSharedPreferences("user_car_info", MODE_PRIVATE);
		editor = preferences.edit();

		final EditText user_info_car_adsetxt = (EditText)findViewById(R.id.user_info_car_adsetxt);
		final EditText user_info_car_engtxt = (EditText)findViewById(R.id.user_info_car_engineExt);
		
		Button user_info_car_btn = (Button)findViewById(R.id.user_info_car_btn);
		user_info_car_btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String user_info_car_num = user_info_car_adsetxt.getText().toString();
				String user_info_car_eng = user_info_car_engtxt.getText().toString();
				
				if(user_info_car_num==null || user_info_car_num.trim().equals("")||user_info_car_eng == null || user_info_car_eng.trim().equals("")){
					user_info_car_adsetxt.setError("车牌号码和发动机号码都不能为空 ");
					return;	
				}
	
				
				
				//存入车牌号码
				editor.putString("User_car_num", user_info_car_num);
				//存入车牌号码
				editor.putString("User_car_eng", user_info_car_eng);
				editor.commit();
				// 创建一个Intent
				Intent intent = new Intent(UserCarInfo.this,
						UserCarInfoFull.class);
				// 启动intent对应的Activity
				startActivity(intent);
				UserCarInfo.this.finish();
			}
		});
		user_info_car_adsetxt.setText(preferences.getString("User_car_num", null));
		user_info_car_engtxt.setText(preferences.getString("User_car_eng", null));
		 Button goMain_bt = (Button) findViewById(R.id.goMain_bt);
		 goMain_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserCarInfo.this,
						UserInfo.class);
				// 启动intent对应的Activity
				startActivity(intent);
				UserCarInfo.this.finish();
			}
		});
		 
		 
		 
		 
			
		 
    
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (keyCode) {
    	 case KeyEvent.KEYCODE_BACK:
    		 finish();
             break;
    	}
    	return false;	
    }
  
}

