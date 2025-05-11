package com.example.d_hub_company_service.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long id;

    @Column(name = "name_company", unique = true, nullable = false)
    private String name;

    @Column(name = "budget", columnDefinition = "NUMERIC")
    private Double budget;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "company_employee_ids", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "employee_id")
    private List<Long> employeeIds;

}
