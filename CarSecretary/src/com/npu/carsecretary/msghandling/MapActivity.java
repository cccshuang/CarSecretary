package com.npu.carsecretary.msghandling;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.npu.carsecretary.R;
import com.npu.carsecretary.msghandling.MyOritationListener.OnOrientationListener;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


public class MapActivity extends Activity {
	MapView mMapView = null;  
	private BaiduMap mbitmap; 
	
	private Button btmap_putong;
	private Button btmap_weixing;
	private Button btmap_shishi;
	
	private Boolean isFirstin = true;
	
	private Button locationbutton;
	//chongxin daingwei定位的厨师坐标
	private LatLng startAddress;
	private double mlat;
	private double mlon;
	//导航的七点与终点位置address为当前所在省市
	private LatLng mDestLocationData;
	private double mDestLon;
	private double mDestLat;
	private double mStartLon;
	private double mStartLat;
	String address;
	
	private MyOritationListener myOrientationListener;
	
	//dingwei tubiao
	private BitmapDescriptor mIconlocation;
	
	//定位
	public LocationClient mLocationClient;
	public MyLocationListener myListener;
	
	private float mCurrentX;
	
	
	//导航   --真实和模拟导航
	private Button bt_Zhenshi;
	private Button bt_moni;
	
	//public static final String TAG = "NaviSDkDemo";
	//在sd上新建的文件夹名
	private static final String APP_FOLDER_NAME = "chemi";
	private String mSDCardPath = null;
	
	
	//创建地理编码检索实例
	private GeoCoder mSearch; 
    
	private Button bt_search;
	private EditText Edit_search;
	
	private final static String authBaseArr[] =
        { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION };
	private final static int authBaseRequestCode = 1;
    private final static int authComRequestCode = 2;
    private boolean hasInitSuccess = false;
	
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static List<Activity> activityList = new LinkedList<Activity>();
    private final static String authComArr[] = { Manifest.permission.READ_PHONE_STATE };
    
    
    private boolean isEnd = false;
    //判断是定位还是导航
    private String isdingwei = "ROUTE";
    //获取传入的起点和终点的位置信息
    private String startA = "重庆市大足区";
    private String endA = "山东省济南市";
    
    //传入的是模拟导航和真实导航
    private boolean isTruedaohang = false;
    
    //判断传入时查询的是起点还是终点s = 1时是终点  s = 2时是起点
    private int s = 0;
    // 导航的起始点
    BNRoutePlanNode sNode = null;
    BNRoutePlanNode eNode = null;
    
    private String Slocation="";
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent intent= getIntent();
        String operation = intent.getStringExtra("operation");
        isdingwei = operation;
        if(isdingwei.equals("ROUTE")){
        	startA = intent.getStringExtra("startLoc");
        	endA = intent.getStringExtra("endLoc");
        	//Toast.makeText(this, startA+" "+endA, Toast.LENGTH_LONG).show();
        }else if(operation.equals("POSITION")){
        	Slocation = intent.getStringExtra("location");
       
        }
        
        
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext()); 
        
        activityList.add(this);
        setContentView(R.layout.activity_map);  
        
       
        
        
        
        
        //获取地图控件引用  
        initview();
        
        intitLocation();
        
        
        
        btmap_putong = (Button) findViewById(R.id.map_1);
    	btmap_weixing = (Button) findViewById(R.id.map_2);
    	btmap_shishi = (Button) findViewById(R.id.map_3);
    	locationbutton = (Button) findViewById(R.id.myloction_button);
    	
    	bt_moni = (Button) findViewById(R.id.bt_moni);
    	bt_Zhenshi = (Button) findViewById(R.id.bt_zhenshi);
    	
    	
    	
//    	bt_search = (Button) findViewById(R.id.bt_chaxun);
//    	Edit_search = (EditText) findViewById(R.id.edittext_didian);
    	
    	
    	
    	
    	
    	
    	
    	//地理位置的查询
    	//searchGeocoder("aaa");
    	
    	mSearch = GeoCoder.newInstance();
    	OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {  
    		
    	    public void onGetGeoCodeResult(GeoCodeResult result) {  
    	        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
    	            //没有检索到结果  
    	        	Toast.makeText(MapActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
    	        	return;
    	        }  
    	        //获取地理编码结果  
    	        if(s == 1)
    	        {
    	        	if(startA != "当前位置")
			        {
			        	s = 2;
			        	 mDestLat = result.getLocation().latitude;
		        	     mDestLon = result.getLocation().longitude;
		        	     mDestLocationData = new LatLng(mDestLat, mDestLon);
//		        	     Toast.makeText(MainActivity.this, " "+mDestLat, Toast.LENGTH_SHORT).show();
//		        	     Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();
			        	mSearch.geocode(new GeoCodeOption().city("").address(startA));
			        	addDestInfoOverLay(mDestLocationData);

			        }
    	        	else
    	        	{
    	        		mDestLat = result.getLocation().latitude;
            	        mDestLon = result.getLocation().longitude;
            	        mDestLocationData = new LatLng(mDestLat, mDestLon);
            	        addDestInfoOverLay(mDestLocationData);
            	        
            	        if(mDestLocationData == null)
        				{
        					Toast.makeText(MapActivity.this, "请先设置目的地", Toast.LENGTH_SHORT).show();
        				}
        				else
        				{
        					routeplanToNavi(false);
        				}
    	        	}
    	        	
    	        }else
    	        if(s == 3)
    	        {
//    	        	Toast.makeText(MainActivity.this, "6", Toast.LENGTH_SHORT).show();
    	        	routeplanToNavi(false);
    	        }else
    	        if(s == 2)
    	        {
    	        	mStartLat = result.getLocation().latitude;
        	        mStartLon = result.getLocation().longitude;
        	        startAddress = new LatLng(mStartLat, mStartLon);
//        	        Toast.makeText(MainActivity.this, " "+mStartLat, Toast.LENGTH_SHORT).show();
//        	        Toast.makeText(MainActivity.this, "3", Toast.LENGTH_SHORT).show();
        	        if(mDestLocationData == null)
    				{
        	        	mSearch.geocode(new GeoCodeOption().city("").address(startA));
//        	        	Toast.makeText(MainActivity.this, "4", Toast.LENGTH_SHORT).show();
    					Toast.makeText(MapActivity.this, "请先设置目的地", Toast.LENGTH_SHORT).show();
    				}
    				else
    				{
    					s = 3;
//    					Toast.makeText(MainActivity.this, "5", Toast.LENGTH_SHORT).show();
    					mSearch.geocode(new GeoCodeOption().city("").address(startA));
    					
    				}
    	        }else
    	        if(s == 4)
    	        {
    	        	mStartLat = result.getLocation().latitude;
        	        mStartLon = result.getLocation().longitude;
        	        startAddress = new LatLng(mStartLat, mStartLon);
        	        addDestInfoOverLay(startAddress);
        	        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(startAddress);
					mbitmap.animateMapStatus(msu);
					isFirstin = false;
    	        }
    	        

    	        
    	        
				
//				addDestInfoOverLay(mDestLocationData);
//				
//				MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
//				mbitmap.animateMapStatus(msu);
//				 msu = MapStatusUpdateFactory.newLatLng(mDestLocationData);
//				
//				mbitmap.animateMapStatus(msu);
    	        //Toast.makeText(MainActivity.this, "搜索成功"+"Lat:"+mDestLat+"Lon:"+mDestLon, Toast.LENGTH_SHORT).show();
    	    }  
    	 
    	    @Override  
    	    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
    	        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
    	            //没有找到检索结果  
    	        	Toast.makeText(MapActivity.this,"未查询到结果", Toast.LENGTH_SHORT).show();
    	        	return;
    	        }  
    	        //获取反向地理编码结果  
    	        address = result.getAddress().substring(0, result.getAddress().indexOf("市")+1);
    	        
    	        mSearch.geocode(new GeoCodeOption()  
		        .city("")
		        .address(endA));
    	       
    	        //Toast.makeText(MainActivity.this, address, Toast.LENGTH_SHORT).show();
    	        
    	    }  
    	};
    	
    	
    	mSearch.setOnGetGeoCodeResultListener(listener);
    	

    	
    	//Toast.makeText(MainActivity.this, ""+mDestLat, Toast.LENGTH_SHORT).show();
    	
    	
    	
    	//设置各个按钮的点击事件
    	
//    	btmap_putong.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				mbitmap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//				
//			}
//		});
        btmap_weixing.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				
				
				if(btmap_weixing.getText()=="普通")
				{
					mbitmap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
					btmap_weixing.setText("卫星");
				}else
				{
					
					mbitmap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
					btmap_weixing.setText("普通");
				}
				
				
				
			}
			
		});
        btmap_shishi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mbitmap.isTrafficEnabled())
				{
					mbitmap.setTrafficEnabled(false);
					btmap_shishi.setText("实时(off)");
				}else
				{
					mbitmap.setTrafficEnabled(true);
					btmap_shishi.setText("实时(on)");
				}
				
			}
		});
        
        locationbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LatLng latLng = new LatLng(mlat, mlon);
				MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
				mbitmap.animateMapStatus(msu);
				 msu = MapStatusUpdateFactory.newLatLng(latLng);
				
				mbitmap.animateMapStatus(msu);
			}
		});
        
        
        
        //模拟导航和真实导航按钮
        bt_moni.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mDestLocationData == null)
				{
					Toast.makeText(MapActivity.this, "请先设置目的地", Toast.LENGTH_SHORT).show();
				}
				else
				{
					routeplanToNavi(false);
				}
			}
		});
    	bt_Zhenshi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mDestLocationData == null)
				{
					Toast.makeText(MapActivity.this, "请先设置目的地", Toast.LENGTH_SHORT).show();
				}
				else
				{
					routeplanToNavi(true);
				}
			}
		});
    	
    	
//    	bt_search.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				
//				mSearch.reverseGeoCode(new ReverseGeoCodeOption()
//				.location(startAddress));
//				
//				
//				
//				
//					
//				
//				
//				
//			}
//		});
        
        
        //地图长按实现终点的确定
        mbitmap.setOnMapLongClickListener(new OnMapLongClickListener() {
			
			@Override
			public void onMapLongClick(LatLng arg0) {
				// TODO Auto-generated method stub
				
				//Toast.makeText(MainActivity.this, "目的地设置成功！", Toast.LENGTH_SHORT).show();
				mDestLocationData = arg0;
				
				
				
				//测试定位地点
//				mSearch.geocode(new GeoCodeOption()  
//		        .city("陕西省西安市")
//		        .address("莲湖区劳动南路150号"));
//				mSearch.reverseGeoCode(new ReverseGeoCodeOption()
//				.location(mDestLocationData));
//				Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
				
				
				//终点的图标
				addDestInfoOverLay(arg0);
			}
		});
        //初始化导航相关
        if(initDirs())
        {
        	initNavi();
        }
    }  
    

    
    
    
    
    private void routeplanToNavi(boolean mock) {

    	
    	CoordinateType coType = CoordinateType.BD09LL;
        
    	
    		sNode = new BNRoutePlanNode(startAddress.longitude, startAddress.latitude, "当前位置", null, coType);
            eNode = new BNRoutePlanNode(mDestLocationData.longitude, mDestLocationData.latitude, "目的地点", null, coType);
       
     
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, mock, new DemoRoutePlanListener(sNode));
        
    	}
        

    
    	
        	
    }
    public class DemoRoutePlanListener implements RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
             */

//            for (Activity ac : activityList) {
//
//                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {
//
//                    return;
//                }
//            }
            Intent intent = new Intent(MapActivity.this, BNDemoGuideActivity.class);
            
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
           // Toast.makeText(MainActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    
    
    private boolean initDirs()
    {
    	
    	mSDCardPath = getSdcardDir();
    	if(mSDCardPath == null){
    		return false;
    	}
    	File f = new File(mSDCardPath,APP_FOLDER_NAME);
    	if(!f.exists())
    	{
    		try{
    			f.mkdirs();
    		}catch(Exception e){
    			e.printStackTrace();
    			return false;
    		}
    	}
    	return true;
    }
    
    private boolean hasBasePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

	String authinfo = null;
	
	
	private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                     //showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                     //showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };
    
    public void showToastMsg(final String msg) {
        MapActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
             //showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
             //showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };
    
    
    private boolean hasCompletePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }
    
    private void initSetting() {
        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }
    
    private void initNavi(){
    	
    	
    	BNOuterTTSPlayerCallback ttsCallback = null;

       

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    //authinfo = "key校验成功!";
                } else {
                    //authinfo = "key校验失败, " + msg;
                }
                MapActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //Toast.makeText(MainActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
                //Toast.makeText(MainActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                hasInitSuccess = true;
                initSetting();
            }

            public void initStart() {
                //Toast.makeText(MainActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                //Toast.makeText(MainActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }

        }, null, ttsHandler, ttsPlayStateListener);

    }
    
   
    
    
    
    protected void addDestInfoOverLay(LatLng arg0) {
		// TODO Auto-generated method stub
		mbitmap.clear();
		OverlayOptions options = new MarkerOptions().position(arg0)//
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.dest))//
				.zIndex(5);
		mbitmap.addOverlay(options);
	}

	private void intitLocation() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this);
		myListener = new MyLocationListener();
		mLocationClient.registerLocationListener(myListener);
		
		
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy
				);
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		
		mLocationClient.setLocOption(option);
		//chushihua tubiao
		mIconlocation = BitmapDescriptorFactory.fromResource(R.drawable.location);
		
		myOrientationListener = new MyOritationListener(getBaseContext());
		
		myOrientationListener.setOnorientationListener(new OnOrientationListener() {
			
			@Override
			public void onOrientationChange(float x) {
				
				mCurrentX = x;
				
			}
		});
		
		
//		if(isdingwei == "1")
//		{
//			mSearch.reverseGeoCode(new ReverseGeoCodeOption()
//			.location(startAddress));
//		}
	}

	private void initview()
    {
    	mMapView = (MapView) findViewById(R.id.bmapView);  
    	mbitmap = mMapView.getMap();
    	MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
    	mbitmap.setMapStatus(msu);
    }
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
        mSearch.destroy();
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	
    	//kaiqi dingwei
    	mbitmap.setMyLocationEnabled(true);
    	if(!mLocationClient.isStarted())
    	mLocationClient.start();
    	//开启方向传感器
    	myOrientationListener.start();
    	
    }
    
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
        }  
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	
    	//tingzhi dingwei
    	mbitmap.setMyLocationEnabled(false);
    	mLocationClient.stop();
    	
    	//停止方向传感器
    	myOrientationListener.stop();
    }
    
    
    private class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			// TODO Auto-generated method stub
			MyLocationData data = new MyLocationData.Builder()//
			.direction(mCurrentX)//
			.accuracy(arg0.getRadius())//
			.latitude(arg0.getLatitude())//
			.longitude(arg0.getLongitude())//
			.build();
			mbitmap.setMyLocationData(data);
			//自定义图标
			MyLocationConfiguration config = new MyLocationConfiguration(com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL, true, mIconlocation);
			
			mbitmap.setMyLocationConfigeration(config);
			
			mlat = arg0.getLatitude();
			mlon = arg0.getLongitude();
			
			
			
			
			
			
			//chushi dingwei 
			if(isFirstin)
			{
				if(((!isdingwei.equals("ROUTE"))&&Slocation.contains("当前位置"))||isdingwei.equals("ROUTE"))
				{
					startAddress = new LatLng(mlat, mlon);
					LatLng latLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
					MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(startAddress);
					mbitmap.animateMapStatus(msu);
					isFirstin = false;

				}
				else
					
				{
					s = 4;
					mSearch.geocode(new GeoCodeOption().city("").address(Slocation));
					
				}

				
				
				
				
				//起点为startAddress  终点为mDestLocationData
				if(isdingwei.equals("ROUTE"))
				{
					Toast.makeText(MapActivity.this, "正在加载，请稍候...", Toast.LENGTH_LONG).show();
					s = 1;
					mSearch.reverseGeoCode(new ReverseGeoCodeOption()
					.location(startAddress));
				//	Toast.makeText(MapActivity.this, "111", Toast.LENGTH_SHORT).show();
//					mSearch.geocode(new GeoCodeOption().city("").address(endA));
				}
			}
			
		}

    }
}
    
    

