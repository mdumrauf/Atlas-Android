package com.layer.ui.messagetypes.text;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.layer.ui.R;
import com.layer.ui.messagetypes.CellFactory;
import com.layer.ui.util.Util;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

public class TextCellFactory extends
        CellFactory<TextCellFactory.CellHolder, TextCellFactory.TextInfo> implements View.OnLongClickListener {
    public final static String MIME_TYPE = "text/plain";

    public TextCellFactory() {
        super(256 * 1024);
    }

    @Override
    public boolean isBindable(Message message) {
        return isType(message);
    }

    @Override
    public CellHolder createCellHolder(ViewGroup cellView, boolean isMe, LayoutInflater layoutInflater) {
        View v = layoutInflater.inflate(R.layout.ui_message_item_cell_text, cellView, true);
        v.setBackgroundResource(isMe ? R.drawable.ui_message_item_cell_me : R.drawable.ui_message_item_cell_them);
        ((GradientDrawable) v.getBackground()).setColor(isMe ? mMessageStyle.getMyBubbleColor() : mMessageStyle.getOtherBubbleColor());

        TextView t = (TextView) v.findViewById(R.id.cell_text);
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, isMe ? mMessageStyle.getMyTextSize() : mMessageStyle.getOtherTextSize());
        t.setTextColor(isMe ? mMessageStyle.getMyTextColor() : mMessageStyle.getOtherTextColor());
        t.setLinkTextColor(isMe ? mMessageStyle.getMyTextColor() : mMessageStyle.getOtherTextColor());
        t.setTypeface(isMe ? mMessageStyle.getMyTextTypeface() : mMessageStyle.getOtherTextTypeface(), isMe ? mMessageStyle.getMyTextStyle() : mMessageStyle.getOtherTextStyle());
        return new CellHolder(v);
    }

    @Override
    public TextInfo parseContent(LayerClient layerClient, Message message) {
        MessagePart part = message.getMessageParts().get(0);
        String text = part.isContentReady() ? new String(part.getData()) : "";
        String name;
        Identity sender = message.getSender();
        if (sender != null) {
            name = Util.getDisplayName(sender) + ": ";
        } else {
            name = "";
        }
        return new TextInfo(text, name);
    }

    @Override
    public void bindCellHolder(CellHolder cellHolder, final TextInfo parsed, Message message, CellHolderSpecs specs) {
        cellHolder.mTextView.setText(parsed.getString());
        cellHolder.mTextView.setTag(parsed);
        cellHolder.mTextView.setOnLongClickListener(this);
    }

    public boolean isType(Message message) {
        return message.getMessageParts().size() == 1 &&  message.getMessageParts().get(0).getMimeType().equals(MIME_TYPE);
    }

    @Override
    public String getPreviewText(Context context, Message message) {
        if (isType(message)) {
            MessagePart part = message.getMessageParts().get(0);
            // For large text content, the MessagePart may not be downloaded yet.
            return part.isContentReady() ? new String(part.getData()) : "";
        }
        else {
            throw new IllegalArgumentException("Message is not of the correct type - Text");
        }
    }

    /**
     * Long click copies message text and sender name to clipboard
     */
    @Override
    public boolean onLongClick(View v) {
        TextInfo parsed = (TextInfo) v.getTag();
        String text = parsed.getClipboardPrefix() + parsed.getString();
        Util.copyToClipboard(v.getContext(), R.string.layer_ui_text_cell_factory_clipboard_description, text);
        Toast.makeText(v.getContext(), R.string.layer_ui_text_cell_factory_copied_to_clipboard, Toast.LENGTH_SHORT).show();
        return true;
    }

    public static class CellHolder extends CellFactory.CellHolder {
        TextView mTextView;

        public CellHolder(View view) {
            mTextView = (TextView) view.findViewById(R.id.cell_text);
        }
    }

    public static class TextInfo implements CellFactory.ParsedContent {
        private final String mString;
        private final String mClipboardPrefix;
        private final int mSize;

        public TextInfo(String string, String clipboardPrefix) {
            mString = string;
            mClipboardPrefix = clipboardPrefix;
            mSize = mString.getBytes().length + mClipboardPrefix.getBytes().length;
        }

        public String getString() {
            return mString;
        }

        public String getClipboardPrefix() {
            return mClipboardPrefix;
        }

        @Override
        public int sizeOf() {
            return mSize;
        }
    }
}
