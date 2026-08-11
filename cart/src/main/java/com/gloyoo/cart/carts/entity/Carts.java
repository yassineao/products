package com.gloyoo.cart.carts.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigInteger;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@Table(
        name = "carts"
)
@NoArgsConstructor
@AllArgsConstructor
public class Carts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Builder.Default
    private UUID[] productIds = new UUID[0];

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Stats stat = Stats.saved;

    @Column(nullable = false)
    @Builder.Default
    private BigInteger amount = BigInteger.ZERO;


}
