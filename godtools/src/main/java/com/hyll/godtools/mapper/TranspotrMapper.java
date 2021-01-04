package com.hyll.godtools.mapper;


import com.hyll.godtools.pojo.TransportEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface TranspotrMapper extends Mapper<TransportEntity> {

    @Delete("delete from transport where order_number = #{order_number}")
    void deleteByID(@Param("order_number") String order_number);

    @Delete("delete from transport where batch_number = #{id}")
    void deleteByBatchID(@Param("id") String id);
}
