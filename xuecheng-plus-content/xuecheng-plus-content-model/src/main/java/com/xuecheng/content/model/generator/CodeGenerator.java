package com.xuecheng.content.model.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.model.ClassAnnotationAttributes;

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/content?serverTimezone=UTC", "root", "123456")
                // 全局配置
                .globalConfig(builder -> {
                    builder.author("weipengtao") // 作者
                            .outputDir("/Users/wpt/project/xuecheng-plus-project/xuecheng-plus-content/xuecheng-plus-content-model/src/main/java") // 输出路径
                            .disableOpenDir(); // 生成后不自动打开文件夹
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent("com.xuecheng.content") // 父包名
                            .entity("model.po");
                })
                // 策略配置
                .strategyConfig(builder -> builder.entityBuilder()
                        .enableLombok(
                                new ClassAnnotationAttributes("@Data", "lombok.Data"),
                                new ClassAnnotationAttributes("@NoArgsConstructor", "lombok.NoArgsConstructor"),
                                new ClassAnnotationAttributes("@AllArgsConstructor", "lombok.AllArgsConstructor"),
                                new ClassAnnotationAttributes("@Builder", "lombok.Builder")
                        )
                        .enableSerialAnnotation()
                        .enableFileOverride()
                        .mapperBuilder().disable()
                        // .serviceBuilder().disable()
                        // .controllerBuilder().disable()
                        .enableFileOverride()
                )
                // 模板引擎（默认 Velocity）
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }
}
