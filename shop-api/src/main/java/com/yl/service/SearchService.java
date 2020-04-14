package com.yl.service;

import com.yl.bean.PmsSearchParam;
import com.yl.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
