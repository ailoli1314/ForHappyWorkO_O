package com.example.forhappywork.baseadapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forhappywork.baseviewset.BaseAdapterViewSet;
import com.example.viewsethelp.bindhelp.ViewSetHelp;

import java.util.ArrayList;

/**
 * Created by v_zhangguibin on 2024/8/22.
 * 简单的通用adapter
 * 支持不同布局的item
 * 布局展示、逻辑等操作在data中实现
 */
public class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public ArrayList<BaseAdapterViewSet> mData = null;

    public BaseAdapter(ArrayList<BaseAdapterViewSet> datas) {
        mData = datas;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).layoutId();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(viewType, null);
        return new BaseViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (mData.isEmpty()) {
            return;
        }
        ViewSetHelp.bind(mData.get(position), holder.itemView);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void destoryItems() {
        if (mData == null) {
            return;
        }
        for (BaseAdapterViewSet viewSet : mData) {
            viewSet.destory();
        }
    }
}
