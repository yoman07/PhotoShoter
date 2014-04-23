package com.photoshoter.popups;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.photoshoter.R;
import com.photoshoter.models.Message;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by balu on 2014-04-22.
 */
public class MessagesListAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater mInflater;

    private List<Message> messageList = new LinkedList<Message>();

    private Handler handler = new Handler();

    public MessagesListAdapter(Context ctx) {
        this.ctx=ctx;
        mInflater= (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder {

    }

    public void addItem(Message message){
        messageList.add(message);
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }});
    }

    public void addItems(List<Message> messageArray) {
        Iterator<Message> it = messageArray.iterator();
        while(it.hasNext()) {
            Message us = it.next();
            addItem(us);
        }
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.message_list_row, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        return convertView;
    }
}
