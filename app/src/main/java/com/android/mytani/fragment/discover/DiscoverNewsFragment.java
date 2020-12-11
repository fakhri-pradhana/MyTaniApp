package com.android.mytani.fragment.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.mytani.R;
import com.android.mytani.Utils;
import com.android.mytani.adapter.NewsAdapter;
import com.android.mytani.api.ApiClient;
import com.android.mytani.api.ApiInterface;
import com.android.mytani.models.Article;
import com.android.mytani.models.News;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverNewsFragment extends Fragment {

    public static final String API_KEY = "01a3e250d5644cf2b1d99228cd189039";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private TextView topHeadline;
    private SwipeRefreshLayout swipeRefreshLayout;
//    private TextView errorTitle, errorMessage;
//    private Button btnRetry;

    public DiscoverNewsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_news, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.hasFixedSize();
        topHeadline = view.findViewById(R.id.topheadelines);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        LoadJson();
        return view;
    }

    public void LoadJson(){

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        String country = Utils.getCountry();

        Call<News> call;
        call = apiInterface.getNews(country, API_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticle() != null){

                    if (!articles.isEmpty()){
                        articles.clear();
                    }

                    articles = response.body().getArticle();
                    newsAdapter = new NewsAdapter(articles, getContext());
                    recyclerView.setAdapter(newsAdapter);
                    newsAdapter.notifyDataSetChanged();

                    initListener();

                    topHeadline.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);


                } else {
                    Toast.makeText(getActivity(), "No Result", Toast.LENGTH_SHORT).show();
                }
            }

            private void initListener() {
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

            }
        });

    }
}