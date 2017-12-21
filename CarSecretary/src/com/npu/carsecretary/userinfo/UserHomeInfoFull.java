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

public class UserHomeInfoFull extends Activity{

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_home_full);

		preferences = getSharedPreferences("user_home_info", MODE_PRIVATE);
		editor = preferences.edit();
		final TextView user_info_home_full_adstxt = (TextView)findViewById(R.id.user_info_home_full_adstxt);
		final TextView user_info_home_full_dettxt = (TextView)findViewById(R.id.user_info_home_full_dettxt);
		user_info_home_full_adstxt.setText(preferences.getString("User_home_address", null));
		user_info_home_full_dettxt.setText(preferences.getString("User_home_address_detail", null))	;
		TextView user_info_home_full_edit = (TextView)findViewById(R.id.user_info_home_full_edit);
		TextView user_info_home_full_delete = (TextView)findViewById(R.id.user_info_home_full_delete);
		user_info_home_full_edit.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				// 创建一个Intent
				Intent intent = new Intent(UserHomeInfoFull.this,
						UserHomeInfo.class);
				// 启动intent对应的Activity
				startActivity(intent);
				UserHomeInfoFull.this.finish();
			}
		});
		
		user_info_home_full_delete.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				editor.remove("User_home_address");
				editor.remove("User_home_address_detail");
				editor.commit();
				
				// 创建一个Intent
				Intent intent = new Intent(UserHomeInfoFull.this,
						UserHomeInfo.class);
				// 启动intent对应的Activity
				startActivity(intent);
				UserHomeInfoFull.this.finish();
			}
		});
		Button goMain_bt = (Button) findViewById(R.id.goMain_bt);
		 goMain_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserHomeInfoFull.this,
						UserInfo.class);
				// 启动intent对应的Activity
				startActivity(intent);
				UserHomeInfoFull.this.finish();
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
