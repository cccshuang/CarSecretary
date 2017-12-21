package com.npu.carsecretary.adapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.npu.carsecretary.R;

public class MenuGVAdapter extends BaseAdapter {
	private static final String TAG = "MenuGVAdapter";
	private List<String> list;
	private Context mContext;
    private String[] picToTxtArray = {
    		"导航","电话","短信","天气","空气质量","定位","应急电话","歌曲","违章查询","回家","去公司"
    		
    };
	

	public MenuGVAdapter(List<String> list, Context mContext) {
		super();
		this.list = list;
		this.mContext = mContext;
	}

	public void clear() {
		this.mContext = null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHodler hodler;
		if (convertView == null) {
			hodler = new ViewHodler();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_image, null);
			hodler.iv = (ImageView) convertView.findViewById(R.id.menu_img);
			hodler.tv = (TextView) convertView.findViewById(R.id.menu_text);
			convertView.setTag(hodler);
		} else {
			hodler = (ViewHodler) convertView.getTag();
		}
		try {
			Bitmap mBitmap = BitmapFactory.decodeStream(mContext.getAssets().open("menu_pic/png/" + list.get(position)));
			hodler.iv.setImageBitmap(mBitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String pngName = list.get(position);
		pngName = pngName.substring(9, pngName.length()-4);
	
		int pngNum = Integer.parseInt(pngName);
	    hodler.tv.setText(picToTxtArray[pngNum]);
		
		
		

		return convertView;
	}

	class ViewHodler {
		ImageView iv;
		TextView tv;
	}
}
