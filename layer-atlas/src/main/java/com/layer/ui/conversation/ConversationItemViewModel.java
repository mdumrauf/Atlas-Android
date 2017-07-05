package com.layer.ui.conversation;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.ui.util.ConversationFormatter;

import java.util.HashSet;
import java.util.Set;

public class ConversationItemViewModel extends BaseObservable {
    //View Logic
    protected final ConversationFormatter mConversationFormatter;
    protected LayerClient mLayerClient;

    // View Data
    public Conversation mConversation;
    protected Set<Identity> mParticipants;

    // Listeners
    protected OnConversationItemClickListener mOnConversationItemClickListener;


    public ConversationItemViewModel(LayerClient layerClient, ConversationFormatter conversationFormatter, OnConversationItemClickListener onConversationItemClickListener) {
        mConversationFormatter = conversationFormatter;
        mLayerClient = layerClient;
        mOnConversationItemClickListener = onConversationItemClickListener;
        mParticipants = new HashSet<>();
    }

    public void setConversation(Conversation conversation) {
        mConversation = conversation;
        mParticipants.clear();
        mParticipants.addAll(conversation.getParticipants());

        if (mParticipants.contains(mLayerClient.getAuthenticatedUser())) {
            mParticipants.remove(mLayerClient.getAuthenticatedUser());
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
        return mConversationFormatter.getConversationTitle(mLayerClient, mConversation, mConversation.getParticipants());
    }

    @Bindable
    public String getSubtitle() {
        return mConversationFormatter.getLastMessagePreview(mConversation);
    }

    @Bindable
    public String getRightAccessoryText() {
        return mConversationFormatter.getTimeStamp(mConversation);
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
    public Set<Identity> getParticipants() {
        return mParticipants;
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
