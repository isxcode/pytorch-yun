package com.isxcode.torch.modules.datasource.source.impl;

import com.isxcode.torch.api.datasource.constants.DatasourceDriver;
import com.isxcode.torch.api.datasource.constants.DatasourceType;
import com.isxcode.torch.api.datasource.dto.ConnectInfo;
import com.isxcode.torch.api.datasource.dto.QueryColumnDto;
import com.isxcode.torch.api.datasource.dto.QueryTableDto;
import com.isxcode.torch.api.work.res.GetDataSourceDataRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.backend.api.base.properties.IsxAppProperties;
import com.isxcode.torch.common.utils.aes.AesUtils;
import com.isxcode.torch.modules.datasource.service.DatabaseDriverService;
import com.isxcode.torch.modules.datasource.source.Datasource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StarRocksService extends Datasource {

    public StarRocksService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties,
        AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.STAR_ROCKS;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.STAR_ROCKS_DRIVER;
    }

    @Override
    public List<QueryTableDto> queryTable(ConnectInfo connectInfo) throws IsxAppException {
        throw new RuntimeException("该数据源暂不支持");
    }

    @Override
    public List<QueryColumnDto> queryColumn(ConnectInfo connectInfo) throws IsxAppException {
        throw new RuntimeException("该数据源暂不支持");
    }

    @Override
    public Long getTableTotalSize(ConnectInfo connectInfo) throws IsxAppException {
        return 0L;
    }

    @Override
    public Long getTableTotalRows(ConnectInfo connectInfo) throws IsxAppException {
        return 0L;
    }

    @Override
    public Long getTableColumnCount(ConnectInfo connectInfo) throws IsxAppException {
        return 0L;
    }

    @Override
    public String getPageSql(String sql) throws IsxAppException {
        return sql + " LIMIT '${page}' OFFSET '${pageSize}'";
    }

    @Override
    public GetDataSourceDataRes getTableData(ConnectInfo connectInfo) throws IsxAppException {
        return null;
    }

    @Override
    public void refreshTableInfo(ConnectInfo connectInfo) throws IsxAppException {

    }

}
