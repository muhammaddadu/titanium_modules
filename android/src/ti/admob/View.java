/**
 * Copyright (c) 2011 by Studio Classics. All Rights Reserved.
 * Author: Brian Kurzius
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.admob;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;

public class View extends TiUIView {
	private static final String TAG = "AdMobView";
	AdView adView;
	int prop_top;
	int prop_left;
	int prop_right;
	String prop_color_bg;
	String prop_color_bg_top;
	String prop_color_border;
	String prop_color_text;
	String prop_color_link;
	String prop_color_url;

	public View(final TiViewProxy proxy) {
		super(proxy);
		Log.d(TAG, "Creating an adMob ad view");
		// get the publisher id that was set in the module
		Log.d(TAG, "AdmobModule.PUBLISHER_ID: " + AdmobModule.PUBLISHER_ID);
	}

	private void createAdView() {
		Log.d(TAG, "createAdView()");
		// create the adView
		adView = new AdView(proxy.getActivity());
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AdmobModule.PUBLISHER_ID);
		// set the listener
		adView.setAdListener(new AdListener() {
			public void onAdLoaded() {
				Log.d(TAG, "onAdLoaded()");
				proxy.fireEvent(AdmobModule.AD_RECEIVED, new KrollDict());
			}
			
			public void onAdFailedToLoad(int errorCode) {
				Log.d(TAG, "onAdFailedToLoad(): " + errorCode);
				proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, new KrollDict());
			}
		});
		adView.setPadding(prop_left, prop_top, prop_right, 0);
		// Add the AdView to your view hierarchy.
		// The view will have no size until the ad is loaded.
		setNativeView(adView);
		loadAd(AdmobModule.TESTING);
	}

	// load the adMob ad
	public void loadAd(final Boolean testing) {
		proxy.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				final AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
				Log.d(TAG, "requestAd(Boolean testing) -- testing:" + testing);
				if (testing) {
					adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
				}
				Bundle bundle = createAdRequestProperties();
				if (bundle.size() > 0) {
					Log.d(TAG, "extras.size() > 0 -- set ad properties");
					adRequestBuilder.addNetworkExtras(new AdMobExtras(bundle));
				}
				adView.loadAd(adRequestBuilder.build());
			}
		});
		
	}

	@Override
	public void processProperties(KrollDict d) {
		super.processProperties(d);
		Log.d(TAG, "process properties");
		if (d.containsKey("publisherId")) {
			Log.d(TAG, "has publisherId: " + d.getString("publisherId"));
			AdmobModule.PUBLISHER_ID = d.getString("publisherId");
		}
		if (d.containsKey("testing")) {
			Log.d(TAG, "has testing param: " + d.getBoolean("testing"));
			AdmobModule.TESTING = d.getBoolean("testing");
		}
		if (d.containsKey(AdmobModule.PROPERTY_COLOR_BG)) {
			Log.d(TAG, "has PROPERTY_COLOR_BG: " + d.getString(AdmobModule.PROPERTY_COLOR_BG));
			prop_color_bg = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BG));
		}
		if (d.containsKey(AdmobModule.PROPERTY_COLOR_BG_TOP)) {
			Log.d(TAG, "has PROPERTY_COLOR_BG_TOP: " + d.getString(AdmobModule.PROPERTY_COLOR_BG_TOP));
			prop_color_bg_top = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BG_TOP));
		}
		if (d.containsKey(AdmobModule.PROPERTY_COLOR_BORDER)) {
			Log.d(TAG, "has PROPERTY_COLOR_BORDER: " + d.getString(AdmobModule.PROPERTY_COLOR_BORDER));
			prop_color_border = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BORDER));
		}
		if (d.containsKey(AdmobModule.PROPERTY_COLOR_TEXT)) {
			Log.d(TAG, "has PROPERTY_COLOR_TEXT: " + d.getString(AdmobModule.PROPERTY_COLOR_TEXT));
			prop_color_text = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_TEXT));
		}
		if (d.containsKey(AdmobModule.PROPERTY_COLOR_LINK)) {
			Log.d(TAG, "has PROPERTY_COLOR_LINK: " + d.getString(AdmobModule.PROPERTY_COLOR_LINK));
			prop_color_link = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_LINK));
		}
		if (d.containsKey(AdmobModule.PROPERTY_COLOR_URL)) {
			Log.d(TAG, "has PROPERTY_COLOR_URL: " + d.getString(AdmobModule.PROPERTY_COLOR_URL));
			prop_color_url = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_URL));
		}
		// check for deprecated color values

		if (d.containsKey(AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED)) {
			Log.d(TAG, "has PROPERTY_COLOR_TEXT_DEPRECATED: " + d.getString(AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED));
			prop_color_text = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED));
		}
		if (d.containsKey(AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED)) {
			Log.d(TAG, "has PROPERTY_COLOR_LINK_DEPRECATED: " + d.getString(AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED));
			prop_color_link = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED));
		}

		// now create the adView
		this.createAdView();
	}

	public void pause() {
		Log.d(TAG, "pause");
		adView.pause();
	}

	public void resume() {
		Log.d(TAG, "resume");
		adView.resume();
	}

	public void destroy() {
		Log.d(TAG, "destroy");
		adView.destroy();
	}

	// pass the method the TESTING flag
	public void requestAd() {
		Log.d(TAG, "requestAd()");
		// pass the module TESTING flag
		loadAd(AdmobModule.TESTING);
	}

	// pass true to requestAd(Boolean testing) -- this overrides how the module was set
	public void requestTestAd() {
		Log.d(TAG, "requestTestAd()");
		loadAd(true);
	}

	// helper methods

	// create the adRequest extra props
	// http://code.google.com/mobile/ads/docs/bestpractices.html#adcolors
	private Bundle createAdRequestProperties() {
		Bundle bundle = new Bundle();
		if (prop_color_bg != null) {
			Log.d(TAG, "color_bg: " + prop_color_bg);
			bundle.putString("color_bg", prop_color_bg);
		}
		if (prop_color_bg_top != null)
			bundle.putString("color_bg_top", prop_color_bg_top);
		if (prop_color_border != null)
			bundle.putString("color_border", prop_color_border);
		if (prop_color_text != null)
			bundle.putString("color_text", prop_color_text);
		if (prop_color_link != null)
			bundle.putString("color_link", prop_color_link);
		if (prop_color_url != null)
			bundle.putString("color_url", prop_color_url);
		return bundle;
	}

	// modifies the color prop -- removes # and changes constants into hex values
	private String convertColorProp(String color) {
		color = color.replace("#", "");
		if (color.equals("white"))
			color = "FFFFFF";
		if (color.equals("red"))
			color = "FF0000";
		if (color.equals("blue"))
			color = "0000FF";
		if (color.equals("green"))
			color = "008000";
		if (color.equals("yellow"))
			color = "FFFF00";
		if (color.equals("black"))
			color = "000000";
		return color;
	}

}