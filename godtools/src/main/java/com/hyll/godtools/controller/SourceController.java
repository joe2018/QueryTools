package com.hyll.godtools.controller;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.hyll.godtools.config.ResultCode;
import com.hyll.godtools.pojo.Result;
import com.hyll.godtools.pojo.TableType;

import com.hyll.godtools.pojo.TransportEntity;
import com.hyll.godtools.service.TranspotrService;
import com.hyll.godtools.util.ReadExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

@RestController
@Api(tags = "sourceApi")
@RequestMapping("/source")
@Log4j2
@CrossOrigin
public class SourceController {

    @Autowired
    private TranspotrService transpotrService;


    @RequestMapping("index")
    @ResponseBody
    public String index(){
        return "index";
    }

//    @ResponseBody
//    @RequestMapping(value = "/test", method = RequestMethod.GET)
//    public List<String> test(){
//        return ConverterRegistry.;

    /**
     * 上传数据源文件写入数据库
     * @param file 数据源Excel表
     * @return 数据处理结果
     */
    @ResponseBody
    @ApiOperation(value="上传源文件", notes="上传源文件写入数据库", produces="application/json")
    @ApiImplicitParam(name = "file", value = "源文件", paramType = "query", required = true, dataType = "file")
    @RequestMapping(value = "/upfile", method = RequestMethod.POST)
    public Result<T> updateExcel(@RequestParam(value = "file",required = false) MultipartFile file) throws IOException {
        Map<String, String> map = new HashMap<>();
        String fileMD5 = SecureUtil.md5(file.getInputStream());
        if(transpotrService.SeleteByFileMD5(fileMD5)){
            return Result.failure(ResultCode.FILE_ALREADY_EXISTS);
        }
        if (!transpotrService.checkFile(file)){
            return Result.failure(ResultCode.FILE_WRITE_FAILURE);
        }
        try{
            List<TransportEntity> transportEntityList = ReadExcel.readExcel(file);
            transpotrService.inserSqlByEccal(transportEntityList,fileMD5);
            return Result.success();
        }catch (Exception e){
            return Result.failure(ResultCode.FILE_WRITE_ERROR);
        }
    }


    @ResponseBody
    @ApiOperation(value="分页", notes="分页输出", produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "页码",paramType = "path", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量",paramType = "path", required = true, dataType = "int")})
    @RequestMapping(value = "/page/{pageNo}/{pageSize}", method = RequestMethod.GET)
    public Result getPageData(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize){
        Jedis jedis = RedisDS.create().getJedis();
        if (jedis.get("1—"+pageNo+"-"+pageSize) != null){
            JSONObject JsonData = JSONUtil.parseObj(jedis.get("1—"+pageNo+"-"+pageSize));
            return Result.success(JsonData);
        }else {
            return Result.success(transpotrService.PageQuery(pageNo,pageSize,1));
        }


    }

    @ResponseBody
    @ApiOperation(value="操作单分页", notes="分页输出", produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "页码",paramType = "path", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量",paramType = "path", required = true, dataType = "int")})
    @RequestMapping(value = "/batch/{pageNo}/{pageSize}", method = RequestMethod.GET)
    public Result getBatchData(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize){
        Jedis jedis = RedisDS.create().getJedis();
        if (jedis.get("2—"+pageNo+"-"+pageSize) != null){
            JSONObject JsonData = JSONUtil.parseObj(jedis.get("2—"+pageNo+"-"+pageSize));
            return Result.success(JsonData);
        }else {
            return Result.success(transpotrService.PageQuery(pageNo,pageSize,2));
        }

    }

    /**
     * 根据ID列表删除数据
     * @param IDList id列表
     * @return 执行返回 0-成功  1-失败
     */
    @RequestMapping(value = "/delByID", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value="根据ID列表删除数据", notes="根据ID列表删除数据")
    @ApiImplicitParam(name = "IDList", value = "ID列表")
    public Result<T> deleteByIDList(@RequestBody Map<String,List<String>> IDList){
        try{
            transpotrService.delByList(IDList.get("list"));
            return Result.success();
        }catch (Exception e){
            e.printStackTrace();
            return Result.failure(ResultCode.OPERATION_FAILURS);
        }
    }

    /**
     * 每次导入一张EXCEL都会生成一个批次ID（操作ID），根据这个ID删除整个批次
     * @param batchID 批次号
     * @return 执行返回 0-成功  1-失败
     */
    @RequestMapping(value = "/delByBID", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value="根据批次ID删除数据", notes="根据批次ID删除数据")
    @ApiImplicitParam(name = "batchID", value = "批次ID")
    public Result<T> deleteByBatchID(@RequestParam(value = "batchID") String batchID){
        try{
            transpotrService.delByBatchID(batchID);
            return Result.success();
        }catch (Exception e){
            e.printStackTrace();
            return Result.failure(ResultCode.OPERATION_FAILURS);
        }
    }



}
