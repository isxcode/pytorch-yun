package com.isxcode.torch.modules.datasource.service;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.datasource.entity.DatabaseDriverEntity;
import com.isxcode.torch.modules.datasource.repository.DatabaseDriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseDriverService {

    private final DatabaseDriverRepository databaseDriverRepository;

    public DatabaseDriverEntity getDriver(String driverId) {

        return databaseDriverRepository.findById(driverId).orElseThrow(() -> new IsxAppException("数据源驱动不存在"));
    }

    public String getDriverName(String driverId) {

        DatabaseDriverEntity databaseDriver = databaseDriverRepository.findById(driverId).orElse(null);
        return databaseDriver == null ? driverId : databaseDriver.getName();
    }
}
