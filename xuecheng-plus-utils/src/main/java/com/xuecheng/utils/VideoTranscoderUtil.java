package com.xuecheng.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoTranscoderUtil {
    // 默认转码参数
    private static final String DEFAULT_VIDEO_CODEC = "libx264";
    private static final String DEFAULT_AUDIO_CODEC = "aac";
    private static final String DEFAULT_PRESET = "medium";
    private static final int DEFAULT_CRF = 23;

    /**
     * 转码配置类
     */
    public static class TranscodeConfig {
        private String videoCodec = DEFAULT_VIDEO_CODEC;
        private String audioCodec = DEFAULT_AUDIO_CODEC;
        private String preset = DEFAULT_PRESET;
        private Integer crf = DEFAULT_CRF;
        private Integer width;
        private Integer height;
        private String videoBitrate;
        private String audioBitrate = "128k";
        private Integer frameRate;
        private final List<String> extraOptions = new ArrayList<>();

        // 链式设置方法
        public TranscodeConfig videoCodec(String codec) {
            this.videoCodec = codec;
            return this;
        }

        public TranscodeConfig audioCodec(String codec) {
            this.audioCodec = codec;
            return this;
        }

        public TranscodeConfig preset(String preset) {
            this.preset = preset;
            return this;
        }

        public TranscodeConfig crf(Integer crf) {
            this.crf = crf;
            return this;
        }

        public TranscodeConfig resolution(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public TranscodeConfig videoBitrate(String bitrate) {
            this.videoBitrate = bitrate;
            return this;
        }

        public TranscodeConfig audioBitrate(String bitrate) {
            this.audioBitrate = bitrate;
            return this;
        }

        public TranscodeConfig frameRate(int frameRate) {
            this.frameRate = frameRate;
            return this;
        }

        public TranscodeConfig addExtraOption(String option) {
            this.extraOptions.add(option);
            return this;
        }

        public TranscodeConfig addExtraOption(String key, String value) {
            this.extraOptions.add(key);
            this.extraOptions.add(value);
            return this;
        }
    }

    /**
     * 转码进度监听器
     */
    public interface ProgressListener {
        void onProgress(double progress);  // 进度 0.0 - 1.0

        void onComplete(File outputFile);

        void onError(String errorMessage);
    }

    /**
     * 转码结果
     */
    public static class TranscodeResult {
        private final boolean success;
        private final String message;
        private final File outputFile;
        private final long duration; // 转码耗时(毫秒)

        public TranscodeResult(boolean success, String message, File outputFile, long duration) {
            this.success = success;
            this.message = message;
            this.outputFile = outputFile;
            this.duration = duration;
        }

        // getter方法
        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public File getOutputFile() {
            return outputFile;
        }

        public long getDuration() {
            return duration;
        }
    }

    /**
     * 简单的视频转码 - 使用默认配置
     *
     * @param inputFile  输入文件
     * @param outputFile 输出文件
     * @return 转码结果
     */
    public static TranscodeResult transcode(File inputFile, File outputFile) {
        return transcode(inputFile, outputFile, new TranscodeConfig(), null);
    }

    /**
     * 带配置的视频转码
     *
     * @param inputFile  输入文件
     * @param outputFile 输出文件
     * @param config     转码配置
     * @return 转码结果
     */
    public static TranscodeResult transcode(File inputFile, File outputFile, TranscodeConfig config) {
        return transcode(inputFile, outputFile, config, null);
    }

    /**
     * 带进度监听的视频转码
     *
     * @param inputFile  输入文件
     * @param outputFile 输出文件
     * @param config     转码配置
     * @param listener   进度监听器
     * @return 转码结果
     */
    public static TranscodeResult transcode(File inputFile, File outputFile,
                                            TranscodeConfig config, ProgressListener listener) {
        long startTime = System.currentTimeMillis();

        // 验证输入文件
        if (!inputFile.exists()) {
            String error = "输入文件不存在: " + inputFile.getAbsolutePath();
            if (listener != null) listener.onError(error);
            return new TranscodeResult(false, error, null, 0);
        }

        // 创建输出目录
        File outputDir = outputFile.getParentFile();
        if (outputDir != null && !outputDir.exists()) {
            outputDir.mkdirs();
        }

        try {
            // 构建FFmpeg命令
            List<String> command = buildFFmpegCommand(inputFile, outputFile, config);

            // 执行转码
            return executeTranscode(command, outputFile, startTime, listener);

        } catch (Exception e) {
            String error = "转码过程发生异常: " + e.getMessage();
            if (listener != null) listener.onError(error);
            return new TranscodeResult(false, error, null, System.currentTimeMillis() - startTime);
        }
    }

    /**
     * 构建FFmpeg命令
     */
    private static List<String> buildFFmpegCommand(File inputFile, File outputFile, TranscodeConfig config) {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-y"); // 覆盖输出文件
        command.add("-i");
        command.add(inputFile.getAbsolutePath());

        // 视频编码参数
        command.add("-c:v");
        command.add(config.videoCodec);

        // 音频编码参数
        command.add("-c:a");
        command.add(config.audioCodec);

        // 预设和CRF（质量系数）
        if (config.preset != null) {
            command.add("-preset");
            command.add(config.preset);
        }

        if (config.crf != null) {
            command.add("-crf");
            command.add(config.crf.toString());
        }

        // 分辨率调整
        if (config.width != null && config.height != null) {
            command.add("-vf");
            command.add("scale=" + config.width + ":" + config.height);
        }

        // 视频码率
        if (config.videoBitrate != null) {
            command.add("-b:v");
            command.add(config.videoBitrate);
        }

        // 音频码率
        if (config.audioBitrate != null) {
            command.add("-b:a");
            command.add(config.audioBitrate);
        }

        // 帧率
        if (config.frameRate != null) {
            command.add("-r");
            command.add(config.frameRate.toString());
        }

        // 额外参数
        command.addAll(config.extraOptions);

        // 输出文件
        command.add(outputFile.getAbsolutePath());

        return command;
    }

    /**
     * 执行转码过程
     */
    private static TranscodeResult executeTranscode(List<String> command, File outputFile,
                                                    long startTime, ProgressListener listener) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = null;

        try {
            // 启动进程
            process = processBuilder.start();

            // 读取错误流（FFmpeg的输出信息）
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            Double duration = null;
            Pattern durationPattern = Pattern.compile("Duration: (\\d+):(\\d+):(\\d+)\\.(\\d+)");
            Pattern timePattern = Pattern.compile("time=(\\d+):(\\d+):(\\d+)\\.(\\d+)");

            while ((line = errorReader.readLine()) != null) {
                System.out.println("[FFmpeg] " + line); // 调试输出

                // 解析视频总时长
                if (duration == null) {
                    Matcher durationMatcher = durationPattern.matcher(line);
                    if (durationMatcher.find()) {
                        int hours = Integer.parseInt(durationMatcher.group(1));
                        int minutes = Integer.parseInt(durationMatcher.group(2));
                        int seconds = Integer.parseInt(durationMatcher.group(3));
                        duration = (double) (hours * 3600 + minutes * 60 + seconds);
                    }
                }

                // 解析当前处理时间并计算进度
                if (duration != null && listener != null) {
                    Matcher timeMatcher = timePattern.matcher(line);
                    if (timeMatcher.find()) {
                        int hours = Integer.parseInt(timeMatcher.group(1));
                        int minutes = Integer.parseInt(timeMatcher.group(2));
                        int seconds = Integer.parseInt(timeMatcher.group(3));
                        double currentTime = hours * 3600 + minutes * 60 + seconds;

                        double progress = currentTime / duration;
                        progress = Math.min(1.0, Math.max(0.0, progress)); // 限制在0-1之间
                        listener.onProgress(progress);
                    }
                }
            }

            // 等待进程结束
            boolean finished = process.waitFor(10, TimeUnit.MINUTES);
            if (!finished) {
                process.destroy();
                String error = "转码超时";
                if (listener != null) listener.onError(error);
                return new TranscodeResult(false, error, null, System.currentTimeMillis() - startTime);
            }

            int exitCode = process.exitValue();
            long durationTime = System.currentTimeMillis() - startTime;

            if (exitCode == 0 && outputFile.exists()) {
                String message = String.format("转码成功，耗时: %.2f秒", durationTime / 1000.0);
                if (listener != null) {
                    listener.onComplete(outputFile);
                }
                return new TranscodeResult(true, message, outputFile, durationTime);
            } else {
                String error = "转码失败，退出码: " + exitCode;
                if (listener != null) listener.onError(error);
                return new TranscodeResult(false, error, null, durationTime);
            }

        } catch (Exception e) {
            String error = "转码过程发生异常: " + e.getMessage();
            if (listener != null) listener.onError(error);
            return new TranscodeResult(false, error, null, System.currentTimeMillis() - startTime);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 获取视频信息（简化版）
     */
    public static String getVideoInfo(File videoFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", videoFile.getAbsolutePath());
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            StringBuilder info = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Duration:") || line.contains("Stream") || line.contains("Video:") || line.contains("Audio:")) {
                    info.append(line).append("\n");
                }
            }

            process.waitFor();
            return info.toString();

        } catch (Exception e) {
            return "无法获取视频信息: " + e.getMessage();
        }
    }
}
