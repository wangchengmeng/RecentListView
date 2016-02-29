package com.example.administrator.recentlistview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.recentlistview.model.Model;
import com.example.administrator.recentlistview.view.RecentListView;

import java.util.ArrayList;
import java.util.List;

public class SimpleActivity extends AppCompatActivity {

    @butterknife.Bind(R.id.rlv_recent_list)
    RecentListView mRlvRecentList;
    private List<Model> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        initVariables();
        initDatas();
        initViews();
    }

    private void initVariables() {
        butterknife.ButterKnife.bind(this);

    }

    private void initDatas() {
        //这里是模拟数据
        for (int i = 0; i < 10; i++) {
            Model businessModel = new Model();
            businessModel.setImageId(R.mipmap.img01);
            mDatas.add(businessModel);
        }
    }

    private void initViews() {
        mRlvRecentList.fillData(mDatas);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        butterknife.ButterKnife.unbind(this);
    }
}
