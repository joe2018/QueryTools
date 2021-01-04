package com.hyll.godtools.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hyll.godtools.mapper.TableTypeMapper;
import com.hyll.godtools.mapper.TranspotrMapper;
import com.hyll.godtools.pojo.TableType;

import com.hyll.godtools.pojo.TransportEntity;
import com.hyll.godtools.service.TranspotrService;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


@Service
@Log4j2
public class TranspotrServiceImpl implements TranspotrService {

    @Autowired
    TranspotrMapper transpotrMapper;

    @Autowired
    TableTypeMapper tableTypeMapper;

    /**
     * 初始化线程池，同时执行10个线程
     */



    @Override
    @Transactional
    public void inserSqlByEccal(List<TransportEntity> list,String fileMD5) {
            TableType tableType = new TableType();
            //参数1为终端ID
            //参数2为数据中心ID
            ExecutorService executor = ThreadUtil.newExecutor(50);
            Snowflake snowflake = IdUtil.createSnowflake(1, 1);
            String batchId = Convert.toStr(snowflake.nextId());
            Date date = DateTime.now();
            if(!batchId.isEmpty()){
                list.forEach(item ->{
                    executor.execute(() -> {
                        if(transpotrMapper.select(item).size() == 0){
                            item.setLoading_date(item.getLoading_time());
                            item.setOccurrence_time(date);
                            item.setBatch_number(batchId);
                            transpotrMapper.insert(item);
                        }
                    });
                });
                executor.shutdown();
                tableType.setBatch_number(batchId);
                tableType.setBatch_time(date);
                tableType.setFile_md5(fileMD5);
                tableTypeMapper.insertSelective(tableType);
            }
    }

    @Override
    public Map PageQuery(int pageNum, int pageSize, int typeId) {
        Jedis jedis = RedisDS.create().getJedis();
        Page entityPage;
        String Key;
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        //用page类型或者pageInfo类型目的就是获取总页数以及总条数
        if (typeId == 1){
            entityPage = (Page<TransportEntity>)transpotrMapper.selectAll();
            Key = "1—"+pageNum+"-"+pageSize;
        }else {
            entityPage = (Page<TableType>)tableTypeMapper.selectAll();
            Key = "2—"+pageNum+"-"+pageSize;
        }
        jedis.del(Key);
        JSON jsonObject = JSONUtil.parse(getResultsMap(entityPage));
        jedis.setex(Key,5, jsonObject.toString());
        return getResultsMap(entityPage);
    }

    @Override
    public Map<Integer, List<TransportEntity>> compareByExcel(List<TransportEntity> list){
        Map<Integer, List<TransportEntity>> resultMap = new HashMap<>();
        list.forEach(item ->{
            Example example = new Example(TransportEntity.class);
            Example.Criteria criteria = example.createCriteria();
            List<TransportEntity> dataList = new ArrayList<>();
            criteria.andEqualTo("tracking_number",item.getTracking_number());
            criteria.andEqualTo("loading_date",item.getLoading_date());
            List<TransportEntity> sourceData = transpotrMapper.selectByExample(example);
            if (!sourceData.isEmpty()){
                dataList.add(sourceData.get(0));
            }
            dataList.add(item);
            resultMap.put(resultMap.size(), dataList);
        });
        return resultMap;
    }

    @Override
    @Transactional
    public int delByList(List<String> list) {
        list.forEach(item ->{
            transpotrMapper.deleteByID(item);
        });
        return 0;
    }

    @Override
    @Transactional
    public int delByBatchID(String BatchID) {
        transpotrMapper.deleteByBatchID(BatchID);
        tableTypeMapper.deleteByBatchID(BatchID);
        return 0;
    }



    public Boolean SeleteByFileMD5(String fileMD5){
        return tableTypeMapper.SeleteByFileMD5(fileMD5).size() > 0;
    }

    public Boolean checkFile(MultipartFile file){
        if (file.isEmpty()) {
            return false;
        }
        String fileName = FileNameUtil.extName(file.getOriginalFilename());
        assert fileName != null;
        return fileName.equals("xlsx") | fileName.equals("xls");
    }

    /**
     * page转map
     * @param transpotrPage 分页查询结果
     * @return 数据map
     */
    public Map<String,Object> getResultsMap(Page<T> transpotrPage) {
        Map <String,Object> resultsMap = new HashMap<>();
        HashMap<Object, Object> colorMap = Convert.convert(new TypeReference<HashMap<Object, Object>>() {}, transpotrPage);
        resultsMap.put("info",colorMap);
        resultsMap.put("content", Convert.toList(transpotrPage.toArray()));
        return resultsMap;
    }

}
