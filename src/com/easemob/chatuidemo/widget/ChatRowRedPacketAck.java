package com.easemob.chatuidemo.widget;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.R;
import com.easemob.easeui.widget.chatrow.EaseChatRow;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

public class ChatRowRedPacketAck extends EaseChatRow {

    private TextView mTvMessage;

    public ChatRowRedPacketAck(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        if (message.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {
            inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ?
                    R.layout.em_row_red_packet_ack_message : R.layout.em_row_red_packet_ack_message, this);
        }
    }

    @Override
    protected void onFindViewById() {
        mTvMessage = (TextView) findViewById(R.id.ease_tv_money_msg);
    }

    @Override
    protected void onSetUpView() {
        String currentUser = EMChatManager.getInstance().getCurrentUser();
        String senderNickname = message.getStringAttribute(RPConstant.MESSAGE_ATTR_RED_PACKET_SENDER_NICKNAME, "");//红包发送者
        String receiverNickname = message.getStringAttribute(RPConstant.MESSAGE_ATTR_RED_PACKET_RECEIVER_NICKNAME, "");//红包接收者
        String senderId;
        if (message.direct == EMMessage.Direct.SEND) {
            if (message.getChatType().equals(EMMessage.ChatType.GroupChat)) {
                senderId = message.getStringAttribute(RPConstant.MESSAGE_ATTR_RED_PACKET_SENDER_ID, "");
                if (senderId.equals(currentUser)) {
                    mTvMessage.setText(R.string.msg_take_red_packet);
                } else {
                    mTvMessage.setText(String.format(getResources().getString(R.string.msg_take_someone_red_packet), senderNickname));
                }
            } else {
                mTvMessage.setText(String.format(getResources().getString(R.string.msg_take_someone_red_packet), senderNickname));
            }
        } else {
            mTvMessage.setText(String.format(getResources().getString(R.string.msg_someone_take_red_packet), receiverNickname));
        }
    }

    @Override
    protected void onUpdateView() {

    }

    @Override
    protected void onBubbleClick() {
    }

}
