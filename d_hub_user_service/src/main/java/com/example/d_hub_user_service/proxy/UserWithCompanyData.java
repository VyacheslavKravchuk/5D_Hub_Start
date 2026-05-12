package com.example.d_hub_user_service.proxy;

import com.example.d_hub_user_service.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserWithCompanyData {

    private User user;

    private CompanyData companyData;


}
