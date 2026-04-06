package org.sixpang.commonserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomPageableArgumentResolver());
    }

    public static class CustomPageableArgumentResolver extends PageableHandlerMethodArgumentResolver {
        private static final List<Integer> ALLOWED_SIZES = Arrays.asList(10, 30, 50); // 사이즈 제한
        private static final int DEFAULT_SIZE = 10; // 기본값

        @Override
        public Pageable resolveArgument(MethodParameter methodParameter,
                                        ModelAndViewContainer mavContainer,
                                        NativeWebRequest webRequest,
                                        WebDataBinderFactory binderFactory) {

            // 기본 Pageable 파싱 로직
            Pageable pageable = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

            int size = pageable.getPageSize();
            if (!ALLOWED_SIZES.contains(size)) {
                size = DEFAULT_SIZE; // 10, 30, 50이 아니면 10으로 강제 고정
            }

            // 검증된 사이즈로 새로운 Pageable 객체 반환
            return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
        }
    }
}
