package com.npu.carsecretary.userinfo;

import android.app.ActionBar;

import com.npu.carsecretary.MainActivity;
import com.npu.carsecretary.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class UserInfo extends Activity {

	SharedPreferences preferences_home;
	SharedPreferences preferences_firm;
	SharedPreferences preferences_car;
	SharedPreferences preferences_school;
	SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.user_info_main);  
 
		preferences_home = getSharedPreferences("user_home_info", MODE_PRIVATE);
		preferences_firm = getSharedPreferences("user_firm_info", MODE_PRIVATE);
		preferences_car = getSharedPreferences("user_car_info", MODE_PRIVATE);
		preferences_school = getSharedPreferences("user_school_info", MODE_PRIVATE);

		TextView user_func_home_text = (TextView)findViewById(R.id.user_func_home_text);
		user_func_home_text.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if(preferences_home.getString("User_home_address", null)==null 
						|| preferences_home.getString("User_home_address_detail", null)==null)
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserHomeInfo.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
				}
				else
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserHomeInfoFull.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
					
				}
				
			}
		});
		ImageView user_func_home_green = (ImageView)findViewById(R.id.user_func_home_green);
		user_func_home_green.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				
				if(preferences_home.getString("User_home_address", null)==null 
						|| preferences_home.getString("User_home_address_detail", null)==null)
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserHomeInfo.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
				}
				else
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserHomeInfoFull.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
				}
			}
		});
		
		TextView user_func_firm_text = (TextView)findViewById(R.id.user_func_firm_text);
		user_func_firm_text.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				
				if(preferences_firm.getString("User_firm_address", null)==null 
						|| preferences_firm.getString("User_firm_address_detail", null)==null)
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserFirmInfo.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
				}
				else
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserFirmInfoFull.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
				}
			}
		});
		ImageView user_func_firm_img = (ImageView)findViewById(R.id.user_func_firm_img);
		user_func_firm_img.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				
				if(preferences_firm.getString("User_firm_address", null)==null 
						|| preferences_firm.getString("User_firm_address_detail", null)==null)
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserFirmInfo.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
				}
				else
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserFirmInfoFull.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
				}
			}
		});
		
		TextView user_func_car_text = (TextView)findViewById(R.id.user_func_car_text);
		user_func_car_text.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				
				if(preferences_car.getString("User_car_num", null)==null)
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserCarInfo.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
				}
				else
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserCarInfoFull.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
				}
			}
		});
		ImageView user_func_car_orange = (ImageView)findViewById(R.id.user_func_car_orange);
		user_func_car_orange.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				
				if(preferences_car.getString("User_car_num", null)==null)
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserCarInfo.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
				}
				else
				{
					// 创建一个Intent
					Intent intent = new Intent(UserInfo.this,
							UserCarInfoFull.class);
					// 启动intent对应的Activity
					startActivity(intent);
					UserInfo.this.finish();
				}
			}
		});
		
		 Button goMain_bt = (Button) findViewById(R.id.goMain_bt);
		 goMain_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				UserInfo.this.finish();
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
