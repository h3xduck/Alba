package com.marsanpat.alba.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.marsanpat.alba.Database.Message;
import com.marsanpat.alba.R;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.WordViewHolder> {

    class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageItemView;

        private WordViewHolder(View itemView) {
            super(itemView);
            messageItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Message> messages; // Cached copy of words

    MessageListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        if (messages != null) {
            Message current = messages.get(position);
            holder.messageItemView.setText(current.getMessage());
        } else {
            // Covers the case of data not being ready yet.
            holder.messageItemView.setText("No Message");
        }
    }

    void setMessages(List<Message> words){
        messages = words;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // this.messages has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (messages != null)
            return messages.size();
        else return 0;
    }
}