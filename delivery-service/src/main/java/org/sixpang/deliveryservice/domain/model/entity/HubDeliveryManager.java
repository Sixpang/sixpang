package org.sixpang.deliveryservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="p_hub_delivery_manager", schema = "delivery")
public class HubDeliveryManager extends DeliveryBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="hub_id", nullable = false)
    private UUID hub_id;
}