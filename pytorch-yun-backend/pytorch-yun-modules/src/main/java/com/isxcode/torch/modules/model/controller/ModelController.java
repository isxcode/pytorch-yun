package com.isxcode.torch.modules.model.controller;

import com.isxcode.torch.api.datasource.req.*;
import com.isxcode.torch.api.datasource.res.*;
import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.api.model.req.PageModelReq;
import com.isxcode.torch.api.model.res.PageModelRes;
import com.isxcode.torch.api.user.constants.RoleType;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import com.isxcode.torch.modules.model.service.ModelBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Tag(name = "模型仓库模块")
@RequestMapping(ModuleCode.MODEL)
@RestController
@RequiredArgsConstructor
public class ModelController {

    private final ModelBizService modelBizService;
    //
    // @Operation(summary = "添加数据源接口")
    // @PostMapping("/addDatasource")
    // @SuccessResponse("添加成功")
    // public void addDatasource(@Valid @RequestBody AddDatasourceReq addDatasourceReq) {
    //
    // datasourceBizService.addDatasource(addDatasourceReq);
    // }

    // @Secured({RoleType.TENANT_ADMIN})
    // @Operation(summary = "更新数据源接口")
    // @PostMapping("/updateDatasource")
    // @SuccessResponse("更新成功")
    // public void updateDatasource(@Valid @RequestBody UpdateDatasourceReq updateDatasourceReq) {
    //
    // datasourceBizService.updateDatasource(updateDatasourceReq);
    // }

    @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
    @Operation(summary = "查询模型仓库")
    @PostMapping("/pageModel")
    @SuccessResponse("查询成功")
    public Page<PageModelRes> pageModel(@Valid @RequestBody PageModelReq pageModelReq) {

        return modelBizService.pageModel(pageModelReq);
    }

    // @Secured({RoleType.TENANT_ADMIN})
    // @Operation(summary = "删除数据源接口")
    // @PostMapping("/deleteDatasource")
    // @SuccessResponse("删除成功")
    // public void deleteDatasource(@Valid @RequestBody DeleteDatasourceReq deleteDatasourceReq) {
    //
    // datasourceBizService.deleteDatasource(deleteDatasourceReq);
    // }
}
