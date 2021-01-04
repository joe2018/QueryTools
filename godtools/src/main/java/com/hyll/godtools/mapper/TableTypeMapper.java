package com.hyll.godtools.mapper;

import com.hyll.godtools.pojo.TableType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface TableTypeMapper  extends Mapper<TableType> {

    @Update("UPDATE tabletype SET batch_type = #{batch_type} WHERE id = #{id};")
    void upDate(TableType tableType);


    @Delete("delete from tabletype where batch_number = #{id}")
    void deleteByBatchID(@Param("id") String id);

    @Select("select * from tabletype where file_md5 = #{file_md5}")
    List<TableType> SeleteByFileMD5(@Param("file_md5") String file_md5);
}
