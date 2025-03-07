package com.isxcode.torch.modules.ai.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.ai.constant.AiStatus;
import com.isxcode.torch.api.ai.req.AddAiReq;
import com.isxcode.torch.api.ai.req.PageAiReq;
import com.isxcode.torch.api.ai.req.UpdateAiReq;
import com.isxcode.torch.api.ai.res.PageAiRes;

import javax.transaction.Transactional;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import com.isxcode.torch.modules.ai.mapper.AiMapper;
import com.isxcode.torch.modules.ai.repository.AiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AiBizService {

    private final AiRepository aiRepository;

    private final AiMapper aiMapper;

    public void addAi(AddAiReq addAiReq) {

        // 检测数据源名称重复
        Optional<AiEntity> aiEntityByName = aiRepository.findByName(addAiReq.getName());
        if (aiEntityByName.isPresent()) {
            throw new IsxAppException("Ai名称重复");
        }

        AiEntity aiEntity = aiMapper.addAiReqToAiEntity(addAiReq);
        if (addAiReq.getAuthConfig() != null) {
            aiEntity.setAuthConfig(JSON.toJSONString(addAiReq.getAuthConfig()));
        }
        aiEntity.setCheckDateTime(LocalDateTime.now());
        aiEntity.setStatus(AiStatus.ENABLE);
        aiRepository.save(aiEntity);
    }

    public void updateAi(UpdateAiReq updateAiReq) {

        // 检测数据源名称重复
        Optional<AiEntity> aiEntityByName = aiRepository.findByName(updateAiReq.getName());
        if (aiEntityByName.isPresent() && !aiEntityByName.get().getId().equals(updateAiReq.getId())) {
            throw new IsxAppException("ai名称重复");
        }

        AiEntity aiEntity = aiMapper.updateAiReqToAiEntity(updateAiReq);
        if (updateAiReq.getAuthConfig() != null) {
            aiEntity.setAuthConfig(JSON.toJSONString(updateAiReq.getAuthConfig()));
        }
        aiRepository.save(aiEntity);
    }

    public Page<PageAiRes> pageAi(PageAiReq pageAiReq) {

        Page<AiEntity> aiEntityPage = aiRepository.searchAll(pageAiReq.getSearchKeyWord(),
            PageRequest.of(pageAiReq.getPage(), pageAiReq.getPageSize()));

        return aiEntityPage.map(aiMapper::aiEntityToPageAiRes);
    }

    // public void deleteDatasource(DeleteDatasourceReq deleteDatasourceReq) {
    //
    // datasourceRepository.deleteById(deleteDatasourceReq.getDatasourceId());
    // }
    //
    // public TestConnectRes testConnect(GetConnectLogReq testConnectReq) {
    //
    // AiEntity datasourceEntity = datasourceService.getDatasource(testConnectReq.getDatasourceId());
    //
    // // 测试连接
    // datasourceEntity.setCheckDateTime(LocalDateTime.now());
    //
    // if (DatasourceType.KAFKA.equals(datasourceEntity.getDbType())) {
    // try {
    // datasourceService.checkKafka(JSON.parseObject(datasourceEntity.getKafkaConfig(),
    // KafkaConfig.class));
    // datasourceEntity.setStatus(DatasourceStatus.ACTIVE);
    // datasourceEntity.setConnectLog("测试连接成功！");
    // datasourceRepository.save(datasourceEntity);
    // return new TestConnectRes(true, "连接成功");
    // } catch (Exception e) {
    // log.debug(e.getMessage(), e);
    // datasourceEntity.setStatus(DatasourceStatus.FAIL);
    // datasourceEntity.setConnectLog("测试连接失败：" + e.getMessage());
    // datasourceRepository.save(datasourceEntity);
    // return new TestConnectRes(false, e.getMessage());
    // }
    // } else {
    // ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
    // Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
    // connectInfo.setLoginTimeout(5);
    // try (Connection connection = datasource.getConnection(connectInfo)) {
    // if (connection != null) {
    // datasourceEntity.setStatus(DatasourceStatus.ACTIVE);
    // datasourceEntity.setConnectLog("测试连接成功！");
    // datasourceRepository.save(datasourceEntity);
    // return new TestConnectRes(true, "连接成功");
    // } else {
    // datasourceEntity.setStatus(DatasourceStatus.FAIL);
    // datasourceEntity.setConnectLog("测试连接失败: 请检查连接协议");
    // datasourceRepository.save(datasourceEntity);
    // return new TestConnectRes(false, "请检查连接协议");
    // }
    // } catch (IsxAppException exception) {
    // log.debug(exception.getMessage(), exception);
    //
    // datasourceEntity.setStatus(DatasourceStatus.FAIL);
    // datasourceEntity.setConnectLog("测试连接失败：" + exception.getMsg());
    // datasourceRepository.save(datasourceEntity);
    // return new TestConnectRes(false, exception.getMessage());
    // } catch (Exception e) {
    // log.debug(e.getMessage(), e);
    //
    // datasourceEntity.setStatus(DatasourceStatus.FAIL);
    // datasourceEntity.setConnectLog("测试连接失败：" + e.getMessage());
    // datasourceRepository.save(datasourceEntity);
    // return new TestConnectRes(false, e.getMessage());
    // }
    // }
    // }
    //
    // public CheckConnectRes checkConnect(CheckConnectReq checkConnectReq) {
    //
    // AiEntity datasourceEntity = datasourceMapper.checkConnectReqToAiEntity(checkConnectReq);
    // if (Strings.isNotEmpty(checkConnectReq.getPasswd())) {
    // datasourceEntity.setPasswd(aesUtils.encrypt(checkConnectReq.getPasswd()));
    // }
    //
    // if (DatasourceType.KAFKA.equals(datasourceEntity.getDbType())) {
    // try {
    // datasourceService.checkKafka(checkConnectReq.getKafkaConfig());
    // return new CheckConnectRes(true, "连接成功");
    // } catch (Exception e) {
    // log.debug(e.getMessage(), e);
    // return new CheckConnectRes(false, e.getMessage());
    // }
    // } else {
    // ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
    // Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
    // try (Connection connection = datasource.getConnection(connectInfo)) {
    // if (connection != null) {
    // return new CheckConnectRes(true, "连接成功");
    // } else {
    // return new CheckConnectRes(false, "请检查连接协议");
    // }
    // } catch (IsxAppException exception) {
    // log.debug(exception.getMessage(), exception);
    // return new CheckConnectRes(false, exception.getMsg());
    // } catch (Exception e) {
    // log.debug(e.getMessage(), e);
    // return new CheckConnectRes(false, e.getMessage());
    // }
    // }
    // }
    //
    // public GetConnectLogRes getConnectLog(GetConnectLogReq getConnectLogReq) {
    //
    // AiEntity datasource = datasourceService.getDatasource(getConnectLogReq.getDatasourceId());
    //
    // return GetConnectLogRes.builder().connectLog(datasource.getConnectLog()).build();
    // }
    //
    // public void uploadDatabaseDriver(MultipartFile driverFile, String dbType, String name, String
    // remark) {
    //
    // // 判断驱动文件夹是否存在，没有则创建
    // String driverDirPath = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) +
    // File.separator + "jdbc"
    // + File.separator + TENANT_ID.get();
    // if (!new File(driverDirPath).exists()) {
    // try {
    // Files.createDirectories(Paths.get(driverDirPath));
    // } catch (IOException e) {
    // log.debug(e.getMessage(), e);
    // throw new IsxAppException("上传驱动，目录创建失败");
    // }
    // }
    //
    // // 保存驱动文件
    // try (InputStream inputStream = driverFile.getInputStream()) {
    // Files.copy(inputStream, Paths.get(driverDirPath).resolve(driverFile.getOriginalFilename()),
    // StandardCopyOption.REPLACE_EXISTING);
    // } catch (IOException e) {
    // log.debug(e.getMessage(), e);
    // throw new IsxAppException("上传许可证失败");
    // }
    //
    // // 初始化驱动对象
    // DatabaseDriverEntity databaseDriver =
    // DatabaseDriverEntity.builder().name(name).dbType(dbType).driverType("TENANT_DRIVER").remark(remark)
    // .isDefaultDriver(false).fileName(driverFile.getOriginalFilename()).build();
    //
    // // 持久化
    // databaseDriverRepository.save(databaseDriver);
    // }
    //
    // public Page<PageDatabaseDriverRes> pageDatabaseDriver(PageDatabaseDriverReq
    // pageDatabaseDriverReq) {
    //
    // JPA_TENANT_MODE.set(false);
    // Page<DatabaseDriverEntity> pageDatabaseDriver =
    // databaseDriverRepository.searchAll(pageDatabaseDriverReq.getSearchKeyWord(), TENANT_ID.get(),
    // PageRequest.of(pageDatabaseDriverReq.getPage(), pageDatabaseDriverReq.getPageSize()));
    //
    // Page<PageDatabaseDriverRes> map =
    // pageDatabaseDriver.map(datasourceMapper::dataDriverEntityToPageDatabaseDriverRes);
    //
    // map.getContent().forEach(e -> e.setCreateUsername(userService.getUserName(e.getCreateBy())));
    //
    // return map;
    // }
    //
    // public void deleteDatabaseDriver(DeleteDatabaseDriverReq deleteDatabaseDriverReq) {
    //
    // // 支持查询所有的数据
    // JPA_TENANT_MODE.set(false);
    // DatabaseDriverEntity driver =
    // databaseDriverService.getDriver(deleteDatabaseDriverReq.getDriverId());
    // JPA_TENANT_MODE.set(true);
    //
    // // 系统驱动无法删除
    // if ("SYSTEM_DRIVER".equals(driver.getDriverType())) {
    // throw new IsxAppException("系统数据源驱动无法删除");
    // }
    //
    // // 判断驱动是否被别人使用，使用则不能删除
    // List<AiEntity> allDrivers = datasourceRepository.findAllByDriverId(driver.getId());
    // if (!allDrivers.isEmpty()) {
    // throw new IsxAppException("有数据源已使用当前驱动，无法删除");
    // }
    //
    // // 卸载Map中的驱动
    // ALL_EXIST_DRIVER.remove(driver.getId());
    //
    // // 将文件名改名字 xxx.jar ${driverId}_xxx.jar_bak
    // try {
    // String jdbcDirPath = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) +
    // File.separator
    // + "jdbc" + File.separator + TENANT_ID.get();
    // Files.copy(Paths.get(jdbcDirPath).resolve(driver.getFileName()),
    // Paths.get(jdbcDirPath).resolve(driver.getId() + "_" + driver.getFileName() + "_bak"),
    // StandardCopyOption.REPLACE_EXISTING);
    // Files.delete(Paths.get(jdbcDirPath).resolve(driver.getFileName()));
    // } catch (IOException e) {
    // log.debug(e.getMessage(), e);
    // throw new IsxAppException("删除驱动文件异常");
    // }
    //
    // // 删除数据库
    // databaseDriverRepository.deleteById(driver.getId());
    // }
    //
    // public void settingDefaultDatabaseDriver(SettingDefaultDatabaseDriverReq
    // settingDefaultDatabaseDriverReq) {
    //
    // JPA_TENANT_MODE.set(false);
    // Optional<DatabaseDriverEntity> databaseDriverEntityOptional =
    // databaseDriverRepository.findById(settingDefaultDatabaseDriverReq.getDriverId());
    // JPA_TENANT_MODE.set(true);
    //
    // if (!databaseDriverEntityOptional.isPresent()) {
    // throw new IsxAppException("数据源驱动不存在");
    // }
    //
    // DatabaseDriverEntity databaseDriver = databaseDriverEntityOptional.get();
    //
    // if ("SYSTEM_DRIVER".equals(databaseDriver.getDriverType())) {
    // throw new IsxAppException("系统驱动无法默认");
    // }
    //
    // if (settingDefaultDatabaseDriverReq.getIsDefaultDriver()) {
    // // 将租户中其他的同类型驱动，默认状态都改成false
    // List<DatabaseDriverEntity> allDriver =
    // databaseDriverRepository.findAllByDbType(databaseDriver.getDbType());
    // allDriver.forEach(e -> e.setIsDefaultDriver(false));
    // databaseDriverRepository.saveAll(allDriver);
    // }
    //
    // databaseDriver.setIsDefaultDriver(settingDefaultDatabaseDriverReq.getIsDefaultDriver());
    // databaseDriverRepository.save(databaseDriver);
    // }
    //
    // public GetDefaultDatabaseDriverRes getDefaultDatabaseDriver(
    // GetDefaultDatabaseDriverReq getDefaultDatabaseDriverReq) {
    //
    // // 先查询租户的如果有直接返回
    // Optional<DatabaseDriverEntity> defaultDriver =
    // databaseDriverRepository.findByDriverTypeAndDbTypeAndIsDefaultDriver("TENANT_DRIVER",
    // getDefaultDatabaseDriverReq.getDbType(), true);
    // if (defaultDriver.isPresent()) {
    // return datasourceMapper.databaseDriverEntityToGetDefaultDatabaseDriverRes(defaultDriver.get());
    // }
    //
    // // 查询系统默认的返回
    // JPA_TENANT_MODE.set(false);
    // Optional<DatabaseDriverEntity> systemDriver =
    // databaseDriverRepository.findByDriverTypeAndDbTypeAndIsDefaultDriver("SYSTEM_DRIVER",
    // getDefaultDatabaseDriverReq.getDbType(), true);
    // return datasourceMapper.databaseDriverEntityToGetDefaultDatabaseDriverRes(systemDriver.get());
    // }

}
