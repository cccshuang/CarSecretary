package com.npu.carsecretary.userinfo;

import android.app.ActionBar;
import com.npu.carsecretary.R;
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
import android.widget.TextView;
import android.widget.Toast;

public class UserHomeInfo extends Activity{

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_home);

		//获取只能被本应用程序读、写的SharedPreferences对象
		preferences = getSharedPreferences("user_home_info", MODE_PRIVATE);
		editor = preferences.edit();
		final EditText user_info_home_adsetxt = (EditText)findViewById(R.id.user_info_home_adsetxt);
		final EditText user_info_home_detetxt = (EditText)findViewById(R.id.user_info_home_detetxt);
		user_info_home_adsetxt.setText(preferences.getString("User_home_address", null));
		user_info_home_detetxt.setText(preferences.getString("User_home_address_detail", null));
		Button user_info_home_btn = (Button)findViewById(R.id.user_info_home_btn);
		user_info_home_btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String user_info_home_address = user_info_home_adsetxt.getText().toString();
				String user_info_home_addressDetail = user_info_home_detetxt.getText().toString();
				if(user_info_home_address==null || user_info_home_address.trim().equals("")){
					user_info_home_adsetxt.setError("请输入家的地址");
					return;
					
				}
				if(user_info_home_addressDetail==null || user_info_home_addressDetail.trim().equalsIgnoreCase("")){
					user_info_home_detetxt.setError("请输入家的详细地址");
					return;
					
				}
				//存入家的地址
				editor.putString("User_home_address", user_info_home_address);
				//存入家的地址详情
				editor.putString("User_home_address_detail", user_info_home_addressDetail);
				editor.commit();
				// 创建一个Intent
				Intent intent = new Intent(UserHomeInfo.this,
						UserHomeInfoFull.class);
				// 启动intent对应的Activity
				startActivity(intent);
				UserHomeInfo.this.finish();
			}
		});
		Button goMain_bt = (Button) findViewById(R.id.goMain_bt);
		 goMain_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserHomeInfo.this,
						UserInfo.class);
				// 启动intent对应的Activity
				startActivity(intent);
				UserHomeInfo.this.finish();
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
