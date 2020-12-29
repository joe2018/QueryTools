package com.hyll.godtools.mapper;

import com.hyll.godtools.pojo.Transport;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface TranspotrMapper extends Mapper<Transport> {

    @Delete("delete from transport where id = #{id}")
    void deleteByID(@Param("id") String id);

    @Delete("delete from transport where batch_number = #{id}")
    void deleteByBatchID(@Param("id") String id);
}
