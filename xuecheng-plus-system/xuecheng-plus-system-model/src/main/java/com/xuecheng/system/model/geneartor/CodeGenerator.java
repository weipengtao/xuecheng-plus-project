package com.xuecheng.system.model.geneartor;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.model.ClassAnnotationAttributes;

public class CodeGenerator {

    static private final String USERNAME = "root";
    static private final String PASSWORD = "123456";
    static private final String MODULE_NAME = "system"; // 根据模块名更改

    static private final String AUTHOR = "weipengtao";

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/xcplus_" + MODULE_NAME + "?serverTimezone=UTC";

        String outputDir = System.getProperty("user.dir") + "/xuecheng-plus-" + MODULE_NAME + "/xuecheng-plus-" + MODULE_NAME + "-model" + "/src/main/java";
        String parentPackage = "com.xuecheng." + MODULE_NAME;

        FastAutoGenerator.create(url, USERNAME, PASSWORD)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author(AUTHOR)
                            .outputDir(outputDir) // 输出路径
                            .disableOpenDir(); // 生成后不自动打开文件夹
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent(parentPackage) // 父包名
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
                        .controllerBuilder().disable()
                        .serviceBuilder().disable()
                        .enableFileOverride()
                )
                // 模板引擎（默认 Velocity）
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }
}
