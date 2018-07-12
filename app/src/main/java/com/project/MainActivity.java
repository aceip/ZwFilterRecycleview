package com.project;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jzxiang.pickerview.data.Type;
import com.project.adapter.NomalAdapter;
import com.project.manager.FilterManager;
import com.project.manager.IListView;
import com.project.model.FilterCheckData;
import com.project.model.FilterCheckDataItem;
import com.project.model.FilterData;
import com.project.model.Params;
import com.project.view.ZwFilterRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    private ZwFilterRefreshView view;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.view);
        setListView();
    }

    private void setListView()
    {
        List<FilterCheckData> checkDatas = getFilterCheckData();

        view.getFilterManager()
                //.addCheckData(data).addCheckData(data2)//添加侧拉栏中数据
                .addCheckDatas(checkDatas)
                .setOnFilterConfirmClickListener(new IListView.OnFilterConfirmClickListener() {
                    @Override
                    public void onFilterConfirmClick(FilterData data) {
                        Toast.makeText(context,"点击了确认",Toast.LENGTH_SHORT).show();
                        List<FilterCheckData> list = data.getCheckDatas();
                    }
                })
                .setOnFilterResetClickListener(new IListView.OnFilterResetClickListener() {
                    @Override
                    public void onFilterResetClick(FilterData data) {
                        Toast.makeText(context,"点击了重置",Toast.LENGTH_SHORT).show();
                        List<FilterCheckData> list = data.getCheckDatas();
                    }
                });

        //设置侧拉栏样式
        view.getFilterManager().getFiltersDialog()
                .setTimeDialogTheme(R.color.colorAccent)//设置时间选择器颜色
                .addTimeSection(getSupportFragmentManager(), Type.YEAR_MONTH)//添加时间段
                .addSearchTxt();//添加搜索框

        final List<String> spinnerData = new ArrayList<>();
        spinnerData.add("综合");spinnerData.add("时间正序");spinnerData.add("时间倒序");spinnerData.add("热门搜索");

        //设置顶部栏样式
        view.getViewBarManager()
                //.setBarTxtSize(15)
                //.setBarImgCom(getResources().getDrawable(R.mipmap.ic_tri_down),getResources().getDrawable(R.mipmap.ic_tri_up))
                .setComSpinnerData(spinnerData)
                .setFilterTxt("筛选")
                .setOnSortClickListener(new IListView.OnSortClickListener() {
                    @Override
                    public void onSortClick(int status) {
                        Toast.makeText(context,"点击了排序："+status,Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnComSpinnerSelectedListenner(new IListView.OnComSpinnerSelectedListener() {
                    @Override
                    public void onComSpinnerSelected(int index) {
                        Params params = view.getBarCurData();
                        Toast.makeText(context,"点击了："+spinnerData.get(index),Toast.LENGTH_SHORT).show();

                    }
                })
                .setComSpinnerSelectedIndex(0);

        view.updateView();

        //设置数据
        List<Map<String,String>> list = getData();
        NomalAdapter adapter = new NomalAdapter(context,list);
        view.getRecyclerView().setLayoutManager(new LinearLayoutManager(context));
        view.getRecyclerView().setAdapter(adapter);

        //设置刷新加载监听
        view.getRefreshLayout().setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束刷新
                        view.getRefreshLayout().finishRefresh();
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束加载更多
                        view.getRefreshLayout().finishLoadMore();
                        view.getRefreshLayout().setCanLoadMore(false);
                    }
                }, 2000);
            }
        });
    }


    //设置列表数据
    public List<Map<String,String>> getData()
    {
        final List<Map<String,String>> data = new ArrayList<>();
        for( int i=0;i<30;i++ )
        {
            Map<String,String> map = new HashMap<>();
            map.put("id","文本"+i);
            data.add(map);
        }

        return data;
    }

    //设置筛选菜单中单选/多选的数据
    public List<FilterCheckData> getFilterCheckData()
    {
        FilterCheckData data = new FilterCheckData();
        FilterCheckData data2 = new FilterCheckData();
        List<FilterCheckDataItem> ls = new ArrayList<>();List<FilterCheckDataItem> ls2 = new ArrayList<>();
        FilterCheckDataItem dataItem1 = new FilterCheckDataItem(),dataItem2 = new FilterCheckDataItem(),dataItem3 = new FilterCheckDataItem(),dataItem4 = new FilterCheckDataItem();
        dataItem1.setShowName("语文"); dataItem1.setChecked(false);
        dataItem2.setShowName("数学"); dataItem2.setChecked(false);
        dataItem3.setShowName("英语"); dataItem3.setChecked(false);
        dataItem4.setShowName("历史"); dataItem4.setChecked(true);
        ls.add(dataItem1);ls.add(dataItem2);
        ls2.add(dataItem3);ls2.add(dataItem4);
        data.setList(ls);
        data.setTitle("科目2");
        data.setSort(2);
        data2.setList(ls2);
        data2.setTitle("科目");
        data2.setSort(1);
        data2.setType(FilterManager.FILTER_TYPE_SIN_ELECTION);

        List<FilterCheckData> list = new ArrayList<>();
        list.add(data);
        list.add(data2);
        return list;
    }

}