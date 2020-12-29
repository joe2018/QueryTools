package com.hyll.godtools.mapper;

import com.hyll.godtools.pojo.TableType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface TableTypeMapper  extends Mapper<TableType> {

    @Update("UPDATE tabletype SET batch_type = #{batch_type} WHERE id = #{id};")
    void upDate(TableType tableType);


    @Delete("delete from tabletype where batch_number = #{id}")
    void deleteByBatchID(@Param("id") String id);
}
