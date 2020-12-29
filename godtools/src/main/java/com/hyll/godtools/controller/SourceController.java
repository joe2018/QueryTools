package com.hyll.godtools.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.hyll.godtools.pojo.TableType;
import com.hyll.godtools.pojo.Transport;
import com.hyll.godtools.service.TranspotrService;
import com.hyll.godtools.util.ReadExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    /**
     * 上传数据源文件写入数据库
     * @param file 数据源Excel表
     * @return 数据处理结果
     */
    @ResponseBody
    @ApiOperation(value="上传源文件", notes="上传源文件写入数据库", produces="application/json")
    @ApiImplicitParam(name = "file", value = "源文件", paramType = "query", required = true, dataType = "file")
    @RequestMapping(value = "/upfile", method = RequestMethod.POST)
    public Map<String,String> updateExcel(@RequestParam(value = "file",required = false) MultipartFile file){

        Map<String, String> map = new HashMap<>();

        if (!Objects.equals(file.getOriginalFilename(), "")){
            String fileName = file.getOriginalFilename();
            assert fileName != null;
            if (fileName.endsWith(".xlsx") | fileName.endsWith(".xls")){
                try {
                    if(ReadExcel.readExcel(file) != null){
                        if(transpotrService.inserSqlByEccal(ReadExcel.readExcel(file)) == 0){
                            map.put("state","0");
                            map.put("message","文件写入成功！");
                        }else {
                            map.put("state","-1");
                            map.put("message","文件存入错误！");
                        }
                        return map;
                    }
                    else {
                        map.put("state","-1");
                        map.put("message","文件读取错误！");
                        return map;
                    }
                } catch(IOException e){
                    map.put("state","-1");
                    map.put("message","文件读取错误！");
                    return map;
                }
            }else {
                map.put("state","-1");
                map.put("message","上传文件失败，文件格式错误");
                return map;
            }
        }else {
            map.put("state","-1");
            map.put("message","上传文件失败，文件格式错误");
            return map;
        }
    }

    @ResponseBody
    @ApiOperation(value="分页", notes="分页输出", produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "页码",paramType = "path", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量",paramType = "path", required = true, dataType = "int")})
    @RequestMapping(value = "/page/{pageNo}/{pageSize}", method = RequestMethod.GET)
    public JSONObject getPageData(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize){
        Page transpotrPage = transpotrService.PageQuery(pageNo,pageSize,1);
        return getJsonObject(transpotrPage);
    }

    @ResponseBody
    @ApiOperation(value="操作单分页", notes="分页输出", produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "页码",paramType = "path", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量",paramType = "path", required = true, dataType = "int")})
    @RequestMapping(value = "/batch/{pageNo}/{pageSize}", method = RequestMethod.GET)
    public JSONObject getBatchData(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize){
        Page transpotrPage = transpotrService.PageQuery(pageNo,pageSize,2);
        return getJsonObject(transpotrPage);
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
    public int deleteByIDList(@RequestBody Map<String,List<String>> IDList){
        try{
            transpotrService.delByList(IDList.get("list"));
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
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
    public int deleteByBatchID(@RequestParam(value = "batchID") String batchID){
        try{
            transpotrService.delByBatchID(batchID);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * page转json
     * @param transpotrPage 分页查询结果
     * @return 数据Json
     */
    private JSONObject getJsonObject(Page transpotrPage) {
        HashMap<Object, Object> colorMap = new HashMap<>();
        colorMap.put("pageNum", String.valueOf(transpotrPage.getPageNum()));
        colorMap.put("pageSize", String.valueOf(transpotrPage.getPageSize()));
        colorMap.put("startRow", String.valueOf(transpotrPage.getStartRow()));
        colorMap.put("endRow", String.valueOf(transpotrPage.getEndRow()));
        colorMap.put("total", String.valueOf(transpotrPage.getTotal()));
        colorMap.put("pages", String.valueOf(transpotrPage.getPages()));
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("state",0);
        jsonObject.putOpt("info",colorMap);
        jsonObject.append("content", JSONUtil.parse(transpotrPage.toArray()));
        return jsonObject;
    }

}
