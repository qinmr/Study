package com.qinmr.mvp.ui.news.special;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qinmr.mvp.R;
import com.qinmr.mvp.adapter.SpecialAdapter;
import com.qinmr.mvp.adapter.item.SpecialItem;
import com.qinmr.mvp.api.bean.SpecialInfo;
import com.qinmr.mvp.helper.RecyclerViewHelper;
import com.qinmr.mvp.ui.base.BaseSwipeBackActivity;
import com.qinmr.mvp.util.DefIconFactory;
import com.qinmr.recycler.adapter.BaseAdapter;

import java.util.List;

import butterknife.BindView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SpecialActivity extends BaseSwipeBackActivity implements ISpecialView {

    private static final String SPECIAL_KEY = "SpecialKey";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_news_list)
    RecyclerView mRvNewsList;
    @BindView(R.id.fab_coping)
    FloatingActionButton mFabCoping;

    private String mSpecialId;
    private final int[] mSkipId = new int[20];
    private LinearLayoutManager mLayoutManager;

    BaseAdapter mSpecialAdapter;

    public static void launch(Context context, String newsId) {
        Intent intent = new Intent(context, SpecialActivity.class);
        intent.putExtra(SPECIAL_KEY, newsId);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.activity_special;
    }

    @Override
    public void initData() {
        mSpecialId = getIntent().getStringExtra(SPECIAL_KEY);
        mPresenter = new SpecialPensenter(this, mSpecialId);
    }

    @Override
    public void initViews() {
        initToolBar(mToolbar, true, "");
        mSpecialAdapter = new SpecialAdapter(this);
        ScaleInAnimationAdapter animAdapter = new ScaleInAnimationAdapter(mSpecialAdapter);
        RecyclerViewHelper.initRecyclerViewV(this, mRvNewsList, new AlphaInAnimationAdapter(animAdapter));
        mLayoutManager = (LinearLayoutManager) mRvNewsList.getLayoutManager();
    }

    @Override
    public void updateViews(boolean isRefresh) {
        mPresenter.getData(isRefresh);
    }

    @Override
    public void loadData(List<SpecialItem> specialItems) {
        mSpecialAdapter.updateItems(specialItems);
//        handleTagLayout(specialItems);
    }

    @Override
    public void loadBanner(SpecialInfo specialBean) {
        View headView = LayoutInflater.from(this).inflate(R.layout.head_special, null);
        ImageView mIvBanner = (ImageView) headView.findViewById(R.id.iv_banner);
        // 加载图片
//        ImageLoader.loadFitCenter(this, specialBean.getBanner(), mIvBanner, DefIconFactory.provideIcon());
        Glide.with(this).load(specialBean.getBanner()).fitCenter().dontAnimate().placeholder(DefIconFactory.provideIcon()).into(mIvBanner);
        // 添加导语
        if (!TextUtils.isEmpty(specialBean.getDigest())) {
            ViewStub stub = (ViewStub) headView.findViewById(R.id.vs_digest);
            assert stub != null;
            stub.inflate();
            TextView tvDigest = (TextView) headView.findViewById(R.id.tv_digest);
            tvDigest.setText(specialBean.getDigest());
        }
//        mTagLayout = (TagLayout) headView.findViewById(R.id.tag_layout);
        mSpecialAdapter.addHeaderView(headView);
    }

    /**
     * 处理导航标签
     *
     * @param specialItems
     */
    private void _handleTagLayout(List<SpecialItem> specialItems) {
        Observable.from(specialItems)
                .compose(this.<SpecialItem>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<SpecialItem, Boolean>() {
                    int i = 0;
                    int index = mSpecialAdapter.getHeaderViewsCount();  // 存在一个 HeadView 所以从1算起

                    @Override
                    public Boolean call(SpecialItem specialItem) {
                        if (specialItem.isHeader) {
                            // 记录头部的列表索引值，用来跳转
                            mSkipId[i++] = index;
                        }
                        index++;
                        return specialItem.isHeader;
                    }
                })
                .map(new Func1<SpecialItem, String>() {
                    @Override
                    public String call(SpecialItem specialItem) {
                        return clipHeadStr(specialItem.header);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
//                        mTagLayout.addTag(s);
                    }
                });
//        mTagLayout.setTagClickListener(new OnTagClickListener() {
//            @Override
//            public void onTagClick(int position, String text, @TagView.TagMode int tagMode) {
//                // 跳转到对应position,比scrollToPosition（）精确
//                mLayoutManager.scrollToPositionWithOffset(mSkipId[position], 0);
//            }
//        });
    }

    private String clipHeadStr(String headStr) {
        String head = null;
        int index = headStr.indexOf(" ");
        if (index != -1) {
            head = headStr.substring(index, headStr.length());
        }
        return head;
    }

}
