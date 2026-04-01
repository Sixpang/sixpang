package org.sixpang.deliveryservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name="p_company_delivery_manager", schema = "delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyDeliveryManger extends DeliveryBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
