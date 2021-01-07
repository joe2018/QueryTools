package com.hyll.godtools.mapper;


import com.hyll.godtools.pojo.TransportEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
@org.apache.ibatis.annotations.Mapper
public interface TranspotrMapper extends Mapper<TransportEntity> {

    @Delete("delete from transport where order_number = #{order_number}")
    void deleteByID(@Param("order_number") String order_number);

    @Delete("delete from transport where batch_number = #{id}")
    void deleteByBatchID(@Param("id") String id);

    /**
     * 批量插入
     * @param transportEntityList
     */
    void batchInsertTranspotrMapper(List<TransportEntity> transportEntityList);

    /**
     * 查询出库里所有数据，进行缓存
     * @return
     */
    @Select("select id,batch_number,order_number,customer_name,tracking_number,loading_time,license_plate,oil_card,occurrence_time\n" +
            "       from transport")
    List<TransportEntity> findListTransportEntity();

    /**
     * 通过ids数组返回对应的记录
     * @param ids
     * @return
     */
    List<TransportEntity> findListTransportEntityById(List<String> ids);
}
