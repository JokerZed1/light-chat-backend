package com.yougame.common.config;


import com.yougame.common.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/*
 * Web MVC 配置类
 * 【作用】：注册拦截器、配置跨域、静态资源映射等
 */

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    /*
     * 注册拦截器
     * 【拦截路径说明】：
     *   addPathPatterns("/api/**")：拦截所有 /api/ 开头的请求
     *   excludePathPatterns(...)：排除不需要认证的接口（登录、注册、公开资源）
     */

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        // 登录注册（全部包含，永不401）
                        "/api/auth/login",           //登录
                        "/api/auth/register",        //注册

                        // 公开页面：游客也能看
                        "/api/categories",           // 分类
                        "/api/posts/list",           // 帖子列表
                        //"/api/posts/{id}",           // 帖子详情（GET）
                        //"/api/comments/**",

                        // 接口文档
                        "/doc.html",                  // Knife4j 文档
                        "/webjars/**",                // 静态资源
                        "/v3/api-docs/**",            // OpenAPI 文档

                        // ... 其他
                        "/api/uploads/**",
                        "/ws/**"
                        );
    }

    /*
     * 跨域配置（为前端调用做准备）
     * 【注意】：生产环境应该指定具体的允许域名，而不是 *
     */

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")       // 开发环境允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
