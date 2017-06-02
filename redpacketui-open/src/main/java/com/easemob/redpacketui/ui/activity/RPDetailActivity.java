package com.easemob.redpacketui.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.easemob.redpacketsdk.bean.PageInfo;
import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketsdk.constant.RPConstant;
import com.easemob.redpacketsdk.contract.RedPacketDetailContract;
import com.easemob.redpacketsdk.presenter.impl.RedPacketDetailPresenter;
import com.easemob.redpacketsdk.utils.RPPreferenceManager;
import com.easemob.redpacketui.R;
import com.easemob.redpacketui.ui.base.RPBaseActivity;
import com.easemob.redpacketui.ui.fragment.ADPacketFragment;
import com.easemob.redpacketui.ui.fragment.GroupDetailFragment;
import com.easemob.redpacketui.ui.fragment.SingleDetailFragment;
import com.easemob.redpacketui.utils.PageUtil;
import com.easemob.redpacketui.widget.RPTitleBar;

import java.util.ArrayList;


/**
 * Created by max on 16/2/26
 */
public class RPDetailActivity extends RPBaseActivity implements RedPacketDetailContract.View {

    private RedPacketInfo mRedPacketInfo = new RedPacketInfo();

    RedPacketDetailPresenter mRedPacketDetailPresenter;

    @Override
    protected void getBundleExtras(Bundle extras) {
        Intent intent = getIntent();
        mRedPacketInfo = intent.getParcelableExtra(RPConstant.EXTRA_RED_PACKET_INFO);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.rp_activity_red_packet_detail;
    }

    @Override
    protected View getLoadingTargetView() {
        return findViewById(R.id.detail_fragment_container);
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        RPTitleBar titleBar = (RPTitleBar) findViewById(R.id.title_bar);
        if (TextUtils.isEmpty(RPPreferenceManager.getInstance().getOwnerName())) {
            titleBar.setSubTitleVisibility(View.GONE);
        } else {
            String subTitle = String.format(getString(R.string.subtitle_content), RPPreferenceManager.getInstance().getOwnerName());
            titleBar.setSubTitle(subTitle);
        }
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (mRedPacketInfo.redPacketType.equals(RPConstant.RED_PACKET_TYPE_ADVERTISEMENT)) {
            getSupportFragmentManager().beginTransaction().add(R.id.detail_fragment_container, ADPacketFragment.newInstance(mRedPacketInfo)).commitAllowingStateLoss();
        } else {
            mRedPacketDetailPresenter = new RedPacketDetailPresenter();
            mRedPacketDetailPresenter.getPacketDetail(mRedPacketInfo.redPacketId, "", 0, PageUtil.PAGE_LIMIT);
            mRedPacketDetailPresenter.attach(this);
            showLoading();
        }
    }


    @Override
    protected boolean isApplyStatusBarTranslucency() {
        return false;
    }

    @Override
    public void onSinglePacketDetailSuccess(RedPacketInfo redPacketInfo) {
        hideLoading();
        getSupportFragmentManager().beginTransaction().add(R.id.detail_fragment_container, SingleDetailFragment.newInstance(redPacketInfo)).commitAllowingStateLoss();
    }

    @Override
    public void onGroupPacketDetailSuccess(RedPacketInfo headerInfo, ArrayList<RedPacketInfo> listInfo, PageInfo pageInfo) {
        hideLoading();
        getSupportFragmentManager().beginTransaction().add(R.id.detail_fragment_container, GroupDetailFragment.newInstance(headerInfo, listInfo, pageInfo)).commitAllowingStateLoss();
    }

    @Override
    public void onError(String code, String message) {
        hideLoading();
        showToastMsg(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRedPacketDetailPresenter != null) {
            mRedPacketDetailPresenter.detach(true);
            mRedPacketDetailPresenter = null;
        }
    }
}
