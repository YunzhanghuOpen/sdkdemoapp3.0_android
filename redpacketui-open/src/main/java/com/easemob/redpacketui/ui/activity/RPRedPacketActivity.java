package com.easemob.redpacketui.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.easemob.redpacketsdk.RPCallback;
import com.easemob.redpacketsdk.RPTokenCallback;
import com.easemob.redpacketsdk.RedPacket;
import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketsdk.constant.RPConstant;
import com.easemob.redpacketsdk.utils.RPPreferenceManager;
import com.easemob.redpacketui.R;
import com.easemob.redpacketui.callback.RetryTokenListener;
import com.easemob.redpacketui.ui.base.RPBaseActivity;
import com.easemob.redpacketui.ui.fragment.SendGroupPacketFragment;
import com.easemob.redpacketui.ui.fragment.SendSinglePacketFragment;
import com.easemob.redpacketui.widget.RPTitleBar;

/**
 * Created by max on 16/2/26
 */
public class RPRedPacketActivity extends RPBaseActivity implements RetryTokenListener {


    private RPTitleBar mTitleBar;

    private String mCurrentUserId;

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.rp_activity_red_packet;
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        RedPacketInfo redPacketInfo = getIntent().getParcelableExtra(RPConstant.EXTRA_RED_PACKET_INFO);
        mCurrentUserId = redPacketInfo.senderId;
        int chatType = redPacketInfo.chatType;
        if (chatType == RPConstant.CHAT_TYPE_SINGLE) {
            getSupportFragmentManager().beginTransaction().add(R.id.money_fragment_container, SendSinglePacketFragment.newInstance(redPacketInfo)).commit();
        } else if (chatType == RPConstant.CHAT_TYPE_GROUP) {
            getSupportFragmentManager().beginTransaction().add(R.id.money_fragment_container, SendGroupPacketFragment.newInstance(redPacketInfo)).commit();
        }
        mTitleBar = (RPTitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSoftKeyboard();
                finish();
            }
        });
        if (chatType == RPConstant.CHAT_TYPE_GROUP) {
            mTitleBar.setTitle(getString(R.string.title_send_group_money));
        }
        setSubTitle(mTitleBar);
        mTitleBar.setRightImageLayoutVisibility(View.GONE);
        onRetryToken(null);
    }

    private void setSubTitle(RPTitleBar titleBar) {
        if (titleBar == null) {
            return;
        }
        if (TextUtils.isEmpty(RPPreferenceManager.getInstance().getOwnerName())) {
            titleBar.setSubTitleVisibility(View.GONE);
        } else {
            titleBar.setSubTitleVisibility(View.VISIBLE);
            String subTitle = String.format(getString(R.string.subtitle_content), RPPreferenceManager.getInstance().getOwnerName());
            titleBar.setSubTitle(subTitle);
        }
    }


    @Override
    protected boolean isApplyStatusBarTranslucency() {
        return false;
    }


    @Override
    public void onRetryToken(final RPCallback callback) {
        RedPacket.getInstance().initRPToken(mCurrentUserId, new RPTokenCallback() {

            @Override
            public void onTokenSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onSettingSuccess() {
                setSubTitle(mTitleBar);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                if (callback != null) {
                    callback.onError(errorCode, errorMsg);
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RedPacket.getInstance().detachTokenPresenter();
    }
}
