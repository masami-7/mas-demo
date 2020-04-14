package com.yl.service;

import com.yl.bean.WareInfo;
import com.yl.bean.WareOrderTask;
import com.yl.bean.WareSku;

import java.util.List;
import java.util.Map;

/**
 * @param
 * @return
 */
public interface WareService {
    public Integer  getStockBySkuId(String skuid);

    public boolean  hasStockBySkuId(String skuid, Integer num);

    public List<WareInfo> getWareInfoBySkuid(String skuid);

    public void addWareInfo();

    public Map<String,List<String>> getWareSkuMap(List<String> skuIdlist);

    public void addWareSku(WareSku wareSku);

    public void deliveryStock(WareOrderTask taskExample) ;

    public WareOrderTask saveWareOrderTask(WareOrderTask wareOrderTask);

    public  List<WareOrderTask>   checkOrderSplit(WareOrderTask wareOrderTask);

    public void lockStock(WareOrderTask wareOrderTask);

    public List<WareOrderTask> getWareOrderTaskList(WareOrderTask wareOrderTask);

    public List<WareSku> getWareSkuList();

    public List<WareInfo> getWareInfoList();
}
