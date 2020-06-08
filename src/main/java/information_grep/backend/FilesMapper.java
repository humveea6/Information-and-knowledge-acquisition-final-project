package information_grep.backend;

import information_grep.backend.Files;
import information_grep.backend.FilesExample;
import information_grep.backend.FilesWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FilesMapper {
    long countByExample(FilesExample example);

    int deleteByExample(FilesExample example);

    int deleteByPrimaryKey(String id);

    int insert(FilesWithBLOBs record);

    int insertSelective(FilesWithBLOBs record);

    List<FilesWithBLOBs> selectByExampleWithBLOBs(FilesExample example);

    List<Files> selectByExample(FilesExample example);

    FilesWithBLOBs selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FilesWithBLOBs record, @Param("example") FilesExample example);

    int updateByExampleWithBLOBs(@Param("record") FilesWithBLOBs record, @Param("example") FilesExample example);

    int updateByExample(@Param("record") Files record, @Param("example") FilesExample example);

    int updateByPrimaryKeySelective(FilesWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(FilesWithBLOBs record);

    int updateByPrimaryKey(Files record);
}