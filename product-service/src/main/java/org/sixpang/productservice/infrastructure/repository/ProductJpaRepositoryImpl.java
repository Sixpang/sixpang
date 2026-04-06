package org.sixpang.productservice.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sixpang.productservice.application.dto.ProductSearchRequest;
import org.sixpang.productservice.domain.model.Product;
import org.sixpang.productservice.domain.model.QProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class ProductJpaRepositoryImpl implements ProductJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> searchProducts(ProductSearchRequest request, Pageable pageable) {
        QProduct product = QProduct.product;

        List<Product> content = queryFactory
                .selectFrom(product)
                .where(
                        product.deletedAt.isNull(),
                        request.name() == null || request.name().isBlank() ? null : product.name.containsIgnoreCase(request.name()),
                        request.price() == null ? null : product.price.eq(request.price()),
                        request.companyId() == null ? null : product.companyId.eq(request.companyId()),
                        request.hubId() == null ? null : product.hubId.eq(request.hubId())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        product.deletedAt.isNull(),
                        request.name() == null || request.name().isBlank() ? null : product.name.containsIgnoreCase(request.name()),
                        request.price() == null ? null : product.price.eq(request.price()),
                        request.companyId() == null ? null : product.companyId.eq(request.companyId()),
                        request.hubId() == null ? null : product.hubId.eq(request.hubId())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}