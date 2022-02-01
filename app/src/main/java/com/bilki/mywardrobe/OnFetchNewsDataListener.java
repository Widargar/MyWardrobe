package com.bilki.mywardrobe;

import com.bilki.mywardrobe.NewsModels.NewsHeadlines;

import java.util.List;

public interface OnFetchNewsDataListener<NewsApiResponse> {

    void onFetchData(List<NewsHeadlines> list, String message);
    void onError(String message);

}
