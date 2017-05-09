package com.qinmr.util.ui.photo.news;

import android.support.v7.widget.RecyclerView;

import com.qinmr.mylibrary.callback.ILoadDataView;
import com.qinmr.mylibrary.listener.OnRequestDataListener;
import com.qinmr.util.R;
import com.qinmr.util.adapter.PhotoListAdapter;
import com.qinmr.util.api.bean.PhotoInfo;
import com.qinmr.util.helper.RecyclerViewHelper;
import com.qinmr.util.ui.base.BaseFragment;

import java.util.List;

import butterknife.BindView;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

/**
 * Created by mrq on 2017/4/17.
 */

public class PhotoNewsFragment extends BaseFragment implements ILoadDataView<List<PhotoInfo>> {

    @BindView(R.id.rv_photo_list)
    RecyclerView mRvPhotoList;

    private PhotoNewsHelper helper;
    private PhotoListAdapter mAdapter;

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_photo_list;
    }

    @Override
    public void initData() {
        helper = new PhotoNewsHelper(this);
    }

    @Override
    public void initViews() {
        mAdapter = new PhotoListAdapter(mContext);
        RecyclerViewHelper.initRecyclerViewV(mContext, mRvPhotoList, new SlideInBottomAnimationAdapter(mAdapter));
        mAdapter.setRequestDataListener(new OnRequestDataListener() {
            @Override
            public void onLoadMore() {
                helper.getMoreData();
            }
        });
    }

    @Override
    public void updateViews() {
        helper.getData();
    }

    @Override
    public void loadData(List<PhotoInfo> data) {
        mAdapter.updateItems(data);
    }

    @Override
    public void loadMoreData(List<PhotoInfo> data) {
        mAdapter.loadComplete();
        mAdapter.addItems(data);
    }

    @Override
    public void loadNoData() {

    }
}
