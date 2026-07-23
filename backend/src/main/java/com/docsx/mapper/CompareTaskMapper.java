package com.docsx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docsx.model.entity.CompareTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Mapper
public interface CompareTaskMapper extends BaseMapper<CompareTask> {

    @Select("SELECT * FROM compare_task WHERE session_id = #{sessionId} LIMIT 1")
    CompareTask selectBySessionId(String sessionId);
}
