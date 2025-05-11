package com.example.d_hub_company_service.proxy;

import com.example.d_hub_company_service.entity.Company;
import lombok.Data;

import java.util.Map;

@Data
//@RequiredArgsConstructor
public class EmployeesOfTheCompany {

    private Company company;

    private Map<Long, UserData> employees;


}
