package com.isxcode.torch.api.tenant.pojos.res;

import lombok.Data;

@Data
public class PageTenantUserRes {

    private String id;

    private String username;

    private String account;

    private String status;

    private String roleCode;

    private String createDateTime;

    private String phone;

    private String email;

    public PageTenantUserRes(String id, String account, String username, String phone, String email, String roleCode) {
        this.id = id;
        this.username = username;
        this.account = account;
        this.roleCode = roleCode;
        this.phone = phone;
        this.email = email;
    }
}
