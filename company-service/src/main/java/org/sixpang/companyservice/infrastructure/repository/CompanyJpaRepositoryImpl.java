package org.sixpang.companyservice.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sixpang.companyservice.application.dto.CompanySearchRequest;
import org.sixpang.companyservice.domain.model.Company;
import org.sixpang.companyservice.domain.model.QCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class CompanyJpaRepositoryImpl implements CompanyJpaRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Company> searchCompanies(CompanySearchRequest request, Pageable pageable) {
        QCompany company = QCompany.company;

        List<Company> content = queryFactory
                .selectFrom(company)
                .where(
                        company.deletedAt.isNull(),
                        request.name() == null || request.name().isBlank() ? null : company.name.containsIgnoreCase(request.name()),
                        request.address() == null || request.address().isBlank() ? null : company.address.containsIgnoreCase(request.address()),
                        request.type() == null ? null : company.type.eq(request.type()),
                        request.hubId() == null ? null : company.hubId.eq(request.hubId())

                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(company.count())
                .from(company)
                .where(
                        company.deletedAt.isNull(),
                        request.name() == null || request.name().isBlank() ? null : company.name.containsIgnoreCase(request.name()),
                        request.address() == null || request.address().isBlank() ? null : company.address.containsIgnoreCase(request.address()),
                        request.type() == null ? null : company.type.eq(request.type()),
                        request.hubId() == null ? null : company.hubId.eq(request.hubId())
                )
                .fetchOne();
        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
