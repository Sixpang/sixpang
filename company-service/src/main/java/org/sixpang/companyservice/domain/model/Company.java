package org.sixpang.companyservice.domain.model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="p_company")
public class Company extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id")
    private UUID id;

    @Column(name="company_name",nullable = false, length = 225)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="company_type", nullable = false)
    private CompanyType type;

    @Column(name = "hub_id",nullable = false)
    private UUID hubId;

    @Column(name="company_address",nullable = false, length = 225)
    private String address;

    public Company(String name, String address, CompanyType type, UUID hubId){
        this.name = name;
        this.address = address;
        this.type = type;
        this.hubId = hubId;
    }

    public void update(String name, String address, CompanyType type, UUID hubId){
        if(name != null){
            this.name = name;
        }
        if(address != null){
            this.address = address;
        }
        if(type != null){
            this.type = type;
        }
        if(hubId != null){
            this.hubId = hubId;
        }
    }

    // 삭제자 UUID 받을 수 있을 때 수정
    public void delete(){
        softDelete();
    }
}
