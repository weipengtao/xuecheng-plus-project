package com.xuecheng.content.service.impl;

import com.alibaba.fastjson2.JSON;
import com.xuecheng.base.model.MemoryMultipartFile;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.clients.MediaServiceClient;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.mapper.MqMessageMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.CoursePreviewDTO;
import com.xuecheng.content.model.dto.TeachplanTreeNodeDTO;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.system.model.enums.CourseAuditStatus;
import com.xuecheng.system.model.enums.CoursePublishStatus;
import com.xuecheng.system.model.enums.MessageType;
import com.xuecheng.system.model.enums.TaskStatus;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoursePublishServiceImpl implements CoursePublishService {

    private final CourseBaseService courseBaseService;
    private final TeachplanService teachplanService;

    private final CourseMarketMapper courseMarketMapper;
    private final CoursePublishPreMapper coursePublishPreMapper;
    private final CoursePublishMapper coursePublishMapper;
    private final MqMessageMapper mqMessageMapper;

    private final MediaServiceClient mediaServiceClient;

    @Override
    public CoursePreviewDTO getCoursePreview(Long courseId) {
        CourseBaseInfoDTO courseBaseInfoDTO = courseBaseService.getCourseBaseInfoById(courseId);
        List<TeachplanTreeNodeDTO> tree = teachplanService.getTeachplanTreeNodesByCourseId(courseId);
        return CoursePreviewDTO.builder()
                .courseBase(courseBaseInfoDTO)
                .teachplans(tree)
                .build();
    }

    @Override
    public void commitAudit(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseService.getById(courseId);

        if (courseBase == null) {
            throw new IllegalArgumentException("课程不存在");
        }

        if (!courseBase.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("只能提交本机构的课程");
        }

        if (Objects.equals(courseBase.getAuditStatus(), CourseAuditStatus.SUBMITTED.getCode())) {
            throw new IllegalArgumentException("课程已提交，请勿重复提交");
        }

        CoursePublishPre coursePublishPre = new CoursePublishPre();
        CourseBaseInfoDTO courseBaseInfoDTO = courseBaseService.getCourseBaseInfoById(courseId);
        BeanUtils.copyProperties(courseBaseInfoDTO, coursePublishPre);

        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        coursePublishPre.setMarket(JSON.toJSONString(courseMarket));

        List<TeachplanTreeNodeDTO> tree = teachplanService.getTeachplanTreeNodesByCourseId(courseId);
        if (tree == null || tree.isEmpty()) {
            throw new IllegalArgumentException("课程计划为空，无法提交");
        }
        coursePublishPre.setTeachplan(JSON.toJSONString(tree));

        coursePublishPre.setStatus(CourseAuditStatus.SUBMITTED.getCode());
        coursePublishPre.setCompanyId(companyId);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        coursePublishPreMapper.insertOrUpdate(coursePublishPre);

        courseBase.setAuditStatus(CourseAuditStatus.SUBMITTED.getCode());
        courseBaseService.updateById(courseBase);
    }

    @Transactional
    @Override
    public void coursePublish(Long companyId, Long courseId) {
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            throw new IllegalArgumentException("请先提交审核");
        }
        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("只能发布本机构的课程");
        }
        if (!Objects.equals(coursePublishPre.getStatus(), CourseAuditStatus.APPROVED.getCode())) {
            throw new IllegalArgumentException("课程未审核通过，无法发布");
        }

        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setStatus(CoursePublishStatus.PUBLISHED.getCode());
        coursePublishMapper.insertOrUpdate(coursePublish);

        CourseBase courseBase = courseBaseService.getById(courseId);
        courseBase.setStatus(CoursePublishStatus.PUBLISHED.getCode());
        courseBaseService.updateById(courseBase);

        MqMessage mqMessage = MqMessage.builder()
                .messageType(MessageType.COURSE_PUBLISH.getCode())
                .businessKey1(courseId.toString())
                .build();
        mqMessageMapper.insert(mqMessage);

        coursePublishPreMapper.deleteById(courseId);
    }

    @Override
    public void processCoursePublishTask(MqMessage mqMessage) {
        Long courseId = Long.valueOf(mqMessage.getBusinessKey1());
        try {
            if (Objects.equals(mqMessage.getState(), TaskStatus.SUCCESS.getCode())) {
                log.info("课程发布任务已处理，课程ID：{}", courseId);
                return;
            }
            log.info("开始处理课程发布任务，课程ID：{}", courseId);
            if (Objects.equals(mqMessage.getStageState1(), TaskStatus.INIT.getCode())) {
                log.info("生成课程静态页面并上传 MinIO，课程ID：{}", courseId);
                generateHtmlAndUpload(courseId);
                mqMessage.setStageState1(TaskStatus.SUCCESS.getCode());
            }

            if (Objects.equals(mqMessage.getStageState2(), TaskStatus.INIT.getCode())) {
                log.info("将课程信息索引到 Elasticsearch，课程ID：{}", courseId);
                // TODO 调用搜索服务接口，将课程信息索引到 Elasticsearch
                // mqMessage.setStageState2(TaskStatus.SUCCESS.getCode());
            }

            if (Objects.equals(mqMessage.getStageState3(), TaskStatus.INIT.getCode())) {
                log.info("将课程信息缓存到 Redis，课程ID：{}", courseId);
                // TODO 调用缓存服务接口，将课程信息缓存到 Redis
                // mqMessage.setStageState3(TaskStatus.SUCCESS.getCode());
            }
            // mqMessageMapper.deleteById(mqMessage.getId());
        } catch (Exception e) {
            log.error("处理课程发布任务失败，课程ID：{}", courseId, e);
        } finally {
            mqMessageMapper.updateById(mqMessage);
        }
    }

    private void generateHtmlAndUpload(Long courseId) throws IOException, TemplateException {
        // 1. 创建 FreeMarker 配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());

        // 2. 设置模板文件所在路径（指向 resources/templates）
        configuration.setClassLoaderForTemplateLoading(
                this.getClass().getClassLoader(),
                "templates"   // resources/templates
        );

        // 3. 设置字符集
        configuration.setDefaultEncoding("utf-8");

        // 4. 设置异常处理方式
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // 5. 加载模板文件
        Template template = configuration.getTemplate("course_template.ftl", StandardCharsets.UTF_8.name());

        // 6. 准备数据模型
        Map<String, Object> model = new HashMap<>();
        CoursePreviewDTO coursePreviewDTO = this.getCoursePreview(courseId);
        model.put("model", coursePreviewDTO);

        // 7. 生成静态 HTML 内容
        StringWriter stringWriter = new StringWriter();
        template.process(model, stringWriter);
        byte[] htmlBytes = stringWriter.toString().getBytes(StandardCharsets.UTF_8);

        // 8. 将生成的 HTML 上传到 MinIO
        MultipartFile multipartFile = new MemoryMultipartFile(
                "file",
                "course_" + courseId + ".html",
                "text/html",
                htmlBytes
        );

        RestResponse<Boolean> response = mediaServiceClient.uploadOtherFile(
                multipartFile,
                "course/" + courseId + ".html"
        );

        if (response == null || !Boolean.TRUE.equals(response.getResult())) {
            throw new RuntimeException("上传静态页面失败");
        }

        log.info("课程 [{}] 静态页面生成并上传成功", courseId);
    }
}
