package com.yigou.common.feign.utils;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassPathScanner {
    private final Environment environment;
    private final ResourceLoader resourceLoader;
    public ClassPathScanner(Environment environment, ResourceLoader resourceLoader){
        this.environment=environment;
        this.resourceLoader=resourceLoader;
    }
    private ClassPathScanningCandidateComponentProvider classPathScanner(Class<? extends Annotation> annotation){
        ClassPathScanningCandidateComponentProvider classPathScanningCandidateComponentProvider = new ClassPathScanningCandidateComponentProvider(false, environment) {
            protected boolean isCandidateComponent(@NonNull AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation()) {
                    isCandidate = true;
                }
                return isCandidate;
            }
        };
        classPathScanningCandidateComponentProvider.setEnvironment(this.environment);
        classPathScanningCandidateComponentProvider.setResourceLoader(resourceLoader);
        if (annotation!=null){
            classPathScanningCandidateComponentProvider.addIncludeFilter(new AnnotationTypeFilter(annotation));
        }
        return classPathScanningCandidateComponentProvider;
    }


    /**
     * 获取指定注解所在基础包路径
     * @param annotation 指定注解类：注解类的配置有可能有：value basePackage basePackageClasses 字段就使用指定字段没有就直接得到环境最顶包名
     * @return
     */
    public Set<String> getBasePackageByAnnotation(AnnotationMetadata metadata,Class<? extends Annotation> annotation) {
       return this.getBasePackages(metadata,annotation);
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata,Class<? extends Annotation> annotation) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(annotation.getCanonicalName());
        if (attributes==null){
            return new HashSet<>();
        }
        Set<String> basePackages = new HashSet<>();
        String[] var4 = (String[]) attributes.get("basePackages");
        int var5 = var4.length;

        int var6;
        String pkg;
        for (var6 = 0; var6 < var5; ++var6) {
            pkg = var4[var6];
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        if (CollectionUtils.isEmpty(basePackages)) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }

        return basePackages;
    }

    /**
     * 获取指定路径下所有Bean信息
     * @param packagePath 路径
     * @return
     */
    public Set<BeanDefinition> getAllBeanDefinitionByPackagePath(String packagePath,Class<? extends Annotation> annotation){
        return classPathScanner(annotation).findCandidateComponents(packagePath);
    }


}
