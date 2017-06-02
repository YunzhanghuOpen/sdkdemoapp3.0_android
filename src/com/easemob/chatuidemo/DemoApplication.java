/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.easemob.chat.EMChatManager;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.utils.EaseUserUtils;
import com.easemob.redpacketsdk.RPInitRedPacketCallback;
import com.easemob.redpacketsdk.RPValueCallback;
import com.easemob.redpacketsdk.RedPacket;
import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketsdk.bean.TokenData;
import com.easemob.redpacketsdk.constant.RPConstant;
// ================== fabric start
//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;
// ================== fabric end




public class DemoApplication extends Application {

	public static Context applicationContext;
	private static DemoApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";
	
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	

	@Override
	public void onCreate() {
		super.onCreate();
// ================== fabric start
//		Fabric.with(this, new Crashlytics());
// ================== fabric end
		MultiDex.install(this);
        applicationContext = this;
        instance = this;

        //init demo helper
        DemoHelper.getInstance().init(applicationContext);
		//red packet code : 初始化红包SDK，开启日志输出开关
		RedPacket.getInstance().initRedPacket(applicationContext, RPConstant.AUTH_METHOD_EASE_MOB, new RPInitRedPacketCallback() {
			@Override
			public void initTokenData(RPValueCallback<TokenData> callback) {
				TokenData tokenData = new TokenData();
				tokenData.imUserId = EMChatManager.getInstance().getCurrentUser();
				//此处使用环信id代替了appUserId 开发者可传入App的appUserId
				tokenData.appUserId = EMChatManager.getInstance().getCurrentUser();
				tokenData.imToken = EMChatManager.getInstance().getAccessToken();
				//同步或异步获取TokenData 获取成功后回调onSuccess()方法
				callback.onSuccess(tokenData);
			}

			@Override
			public RedPacketInfo initCurrentUserSync() {
				//这里需要同步设置当前用户id、昵称和头像url
				String currentAvatarUrl = "";
				String currentNickname = EMChatManager.getInstance().getCurrentUser();
				EaseUser easeUser = EaseUserUtils.getUserInfo(currentNickname);
				if (easeUser != null) {
					currentAvatarUrl = TextUtils.isEmpty(easeUser.getAvatar()) ? "none" : easeUser.getAvatar();
					currentNickname = TextUtils.isEmpty(easeUser.getNick()) ? easeUser.getUsername() : easeUser.getNick();
				}
				RedPacketInfo redPacketInfo = new RedPacketInfo();
				redPacketInfo.currentUserId = EMChatManager.getInstance().getCurrentUser();
				redPacketInfo.currentAvatarUrl = currentAvatarUrl;
				redPacketInfo.currentNickname = currentNickname;
				return redPacketInfo;
			}
		});
		RedPacket.getInstance().setDebugMode(true);
		//end of red packet code
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}



	public static DemoApplication getInstance() {
		return instance;
	}
 
}
