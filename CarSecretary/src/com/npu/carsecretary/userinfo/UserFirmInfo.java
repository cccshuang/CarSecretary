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

public class UserFirmInfo extends Activity {
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_firm);

		preferences = getSharedPreferences("user_firm_info", MODE_PRIVATE);
		editor = preferences.edit();
		final EditText user_info_firm_adsetxt = (EditText)findViewById(R.id.user_info_firm_adsetxt);
		final EditText user_info_firm_detetxt = (EditText)findViewById(R.id.user_info_firm_detetxt);
		user_info_firm_adsetxt.setText(preferences.getString("User_firm_address", null));
		user_info_firm_detetxt.setText(preferences.getString("User_firm_address_detail", null));
		Button user_info_firm_btn = (Button)findViewById(R.id.user_info_firm_btn);
		user_info_firm_btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String user_info_firm_address = user_info_firm_adsetxt.getText().toString();
				String user_info_firm_addressDetail = user_info_firm_detetxt.getText().toString();
				if(user_info_firm_address==null || user_info_firm_address.trim().equals("")){
					user_info_firm_adsetxt.setError("请输入公司的地址");
					return;
					
				}
				if(user_info_firm_addressDetail==null || user_info_firm_addressDetail.trim().equalsIgnoreCase("")){
					user_info_firm_detetxt.setError("请输入公司的详细地址");
					return;
					
				}
				//存入公司的地址
				editor.putString("User_firm_address", user_info_firm_address);
				//存入公司的地址详情
				editor.putString("User_firm_address_detail", user_info_firm_addressDetail);
				editor.commit();
				// 创建一个Intent
				Intent intent = new Intent(UserFirmInfo.this,
						UserFirmInfoFull.class);
				// 启动intent对应的Activity
				startActivity(intent);
				UserFirmInfo.this.finish();
			}
		});
		 Button goMain_bt = (Button) findViewById(R.id.goMain_bt);
		 goMain_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserFirmInfo.this,
						UserInfo.class);
				// 启动intent对应的Activity
				startActivity(intent);
				UserFirmInfo.this.finish();
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
