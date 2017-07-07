package com.layer.ui.conversation;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.ui.util.ConversationItemFormatter;

import java.util.HashSet;
import java.util.Set;

public class ConversationItemViewModel extends BaseObservable {
    //View Logic
    protected final ConversationItemFormatter mConversationItemFormatter;
    protected LayerClient mLayerClient;

    // View Data
    public Conversation mConversation;
    protected Set<Identity> mParticipantsMinusAuthenticatedUser;

    // Listeners
    protected OnConversationItemClickListener mOnConversationItemClickListener;


    public ConversationItemViewModel(LayerClient layerClient, ConversationItemFormatter conversationItemFormatter, OnConversationItemClickListener onConversationItemClickListener) {
        mConversationItemFormatter = conversationItemFormatter;
        mLayerClient = layerClient;
        mOnConversationItemClickListener = onConversationItemClickListener;
        mParticipantsMinusAuthenticatedUser = new HashSet<>();
    }

    public void setConversation(Conversation conversation) {
        mConversation = conversation;
        mParticipantsMinusAuthenticatedUser.clear();
        mParticipantsMinusAuthenticatedUser.addAll(conversation.getParticipants());

        if (mParticipantsMinusAuthenticatedUser.contains(mLayerClient.getAuthenticatedUser())) {
            mParticipantsMinusAuthenticatedUser.remove(mLayerClient.getAuthenticatedUser());
        }
        notifyChange();
    }

    // Getters

    @Bindable
    public Conversation getConversation() {
        return mConversation;
    }

    @Bindable
    public String getTitle() {
        return mConversationItemFormatter.getConversationTitle(mLayerClient, mConversation, mConversation.getParticipants());
    }

    @Bindable
    public String getSubtitle() {
        return mConversationItemFormatter.getLastMessagePreview(mConversation);
    }

    @Bindable
    public String getRightAccessoryText() {
        return mConversationItemFormatter.getTimeStamp(mConversation);
    }

    @Bindable
    public boolean isUnread() {
        return mConversation.getTotalUnreadMessageCount() > 0;
    }

    @Bindable
    public OnConversationItemClickListener getOnConversationItemClickListener() {
        return mOnConversationItemClickListener;
    }

    @Bindable
    public Set<Identity> getParticipantsMinusAuthenticatedUser() {
        return mParticipantsMinusAuthenticatedUser;
    }

    // Actions

    public View.OnClickListener onClickConversation() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnConversationItemClickListener != null) {
                    mOnConversationItemClickListener.onConversationClick(mConversation);
                }
            }
        };
    }
}
