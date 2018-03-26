package salam.com.acheri;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by raj on 01-Feb-18.
 */

class PostsAdapter extends RecyclerView.Adapter <PostsAdapter.MyViewAdapter>{

    Context adapter_context;
    ArrayList<HashMap> data;
    String whichclass;


    public class MyViewAdapter extends RecyclerView.ViewHolder {
        TextView tvheading,tvdesc;
        public MyViewAdapter(View view) {
            super(view);

            tvheading = (TextView)view.findViewById(R.id.tv_heading);
            tvdesc = (TextView)view.findViewById(R.id.tv_story);
        }
    }

    public PostsAdapter(Context context, ArrayList list,String comingfrom) {
        adapter_context = context;
        data = list;
        whichclass = comingfrom;
    }

    @Override
    public MyViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(adapter_context)
                .inflate(R.layout.card_layout,parent,false);
        return new MyViewAdapter(itemView);
    }

    @Override
    public void onBindViewHolder(final PostsAdapter.MyViewAdapter holder, final int position) {
        if (whichclass.equals("posts")){

            holder.tvheading.setText(data.get(position).get("post_title").toString());
            holder.tvdesc.setText(data.get(position).get("post_desc").toString());
            holder.tvheading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("post_title").toString());
                    head.putExtra("desc",data.get(position).get("post_desc").toString());
                    adapter_context.startActivity(head);
                }
            });

            holder.tvdesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("post_title").toString());
                    head.putExtra("desc",data.get(position).get("post_desc").toString());
                    adapter_context.startActivity(head);
                    //Toast.makeText(adapter_context,"description",Toast.LENGTH_LONG).show();
                }
            });

        }else if (whichclass.equals("places")){

            holder.tvheading.setText(data.get(position).get("place_title").toString());
            holder.tvdesc.setText(data.get(position).get("place_desc").toString());
            holder.tvheading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("place_title").toString());
                    head.putExtra("desc",data.get(position).get("place_desc").toString());
                    adapter_context.startActivity(head);
                }
            });

            holder.tvdesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("place_title").toString());
                    head.putExtra("desc",data.get(position).get("place_desc").toString());
                    adapter_context.startActivity(head);
                    //Toast.makeText(adapter_context,"description",Toast.LENGTH_LONG).show();
                }
            });
        }else if (whichclass.equals("aliens")){
            holder.tvheading.setText(data.get(position).get("alien_title").toString());
            holder.tvdesc.setText(data.get(position).get("alien_desc").toString());
            holder.tvheading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("alien_title").toString());
                    head.putExtra("desc",data.get(position).get("alien_desc").toString());
                    adapter_context.startActivity(head);
                }
            });

            holder.tvdesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("alien_title").toString());
                    head.putExtra("desc",data.get(position).get("alien_desc").toString());
                    adapter_context.startActivity(head);
                    //Toast.makeText(adapter_context,"description",Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
