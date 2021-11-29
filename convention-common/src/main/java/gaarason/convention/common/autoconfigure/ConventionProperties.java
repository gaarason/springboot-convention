package gaarason.convention.common.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xt
 */
@ConfigurationProperties(prefix = "convention")
public class ConventionProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * http 相关配置
     */
    private Http http = new Http();

    /**
     * dubbo 相关配置
     */
    private Dubbo dubbo = new Dubbo();

    /**
     * Swagger 相关配置
     */
    private Swagger swagger = new Swagger();

    /**
     * xml 中可使用的变量(默认值在xml中配置,此处的默认值仅做提示)
     */
    private LogSpring logSpring = new LogSpring();

    @Override
    public String toString() {
        return "ConventionProperties{" + "http=" + http + ", dubbo=" + dubbo + ", swagger=" + swagger + ", logSpring=" + logSpring + '}';
    }

    public static class Swagger implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 允许访问 Swagger 页面的用户,密码, 多个使用半角逗号分割
         */
        private Map<String, String> accounts = new HashMap<>(16);

        @Override
        public String toString() {
            return "Swagger{" + "accounts=" + accounts + '}';
        }

        public Map<String, String> getAccounts() {
            return accounts;
        }

        public void setAccounts(Map<String, String> accounts) {
            this.accounts = accounts;
        }
    }

    public static class Http implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 出入日志
         */
        private Log log = new Log();

        /**
         * 启用对于`query`,`form-data`,`x-www-form-urlencoded`,`json` 4种入参格式进行下划线到小驼峰转化
         * 其中`json`依赖 jackson 相关注解实现, 其他的不需要注解
         */
        private boolean enableArgumentResolver = true;

        /**
         * 启用对于控制器的响应封装
         */
        private boolean enableReturnValueHandler = true;

        /**
         * 对于/favicon.ico返回空白
         */
        private boolean generateBlankFaviconIco = true;

        public Log getLog() {
            return log;
        }

        public void setLog(Log log) {
            this.log = log;
        }

        public boolean isEnableArgumentResolver() {
            return enableArgumentResolver;
        }

        public void setEnableArgumentResolver(boolean enableArgumentResolver) {
            this.enableArgumentResolver = enableArgumentResolver;
        }

        public boolean isEnableReturnValueHandler() {
            return enableReturnValueHandler;
        }

        public void setEnableReturnValueHandler(boolean enableReturnValueHandler) {
            this.enableReturnValueHandler = enableReturnValueHandler;
        }

        public boolean isGenerateBlankFaviconIco() {
            return generateBlankFaviconIco;
        }

        public void setGenerateBlankFaviconIco(boolean generateBlankFaviconIco) {
            this.generateBlankFaviconIco = generateBlankFaviconIco;
        }

        @Override
        public String toString() {
            return "Http{" + "log=" + log + ", enableArgumentResolver=" + enableArgumentResolver + ", enableReturnValueHandler=" + enableReturnValueHandler
                    + ", generateBlankFaviconIco=" + generateBlankFaviconIco + '}';
        }
    }

    public static class Dubbo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 出入日志
         */
        private Log log = new Log();

        public Log getLog() {
            return log;
        }

        public void setLog(Log log) {
            this.log = log;
        }

        @Override
        public String toString() {
            return "Dubbo{" + "log=" + log + '}';
        }
    }

    public static class Log implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 服务端 接收请求 日志
         */
        private boolean providerReceivedRequest = true;

        /**
         * 服务端 接收请求 请求体 日志
         */
        private boolean providerReceivedRequestBody = true;

        /**
         * 服务端 发送响应 日志
         */
        private boolean providerSendingResponse = true;

        /**
         * 客户端 发送请求 日志
         */
        private boolean consumerSendingRequest = true;

        /**
         * 客户端 发送请求 重试日志
         */
        private boolean consumerSendingRequestRetry = true;

        /**
         * 客户端 接收响应 日志
         */
        private boolean consumerReceivedResponse = true;

        public boolean isProviderReceivedRequest() {
            return providerReceivedRequest;
        }

        public void setProviderReceivedRequest(boolean providerReceivedRequest) {
            this.providerReceivedRequest = providerReceivedRequest;
        }

        public boolean isProviderReceivedRequestBody() {
            return providerReceivedRequestBody;
        }

        public void setProviderReceivedRequestBody(boolean providerReceivedRequestBody) {
            this.providerReceivedRequestBody = providerReceivedRequestBody;
        }

        public boolean isProviderSendingResponse() {
            return providerSendingResponse;
        }

        public void setProviderSendingResponse(boolean providerSendingResponse) {
            this.providerSendingResponse = providerSendingResponse;
        }

        public boolean isConsumerSendingRequest() {
            return consumerSendingRequest;
        }

        public void setConsumerSendingRequest(boolean consumerSendingRequest) {
            this.consumerSendingRequest = consumerSendingRequest;
        }

        public boolean isConsumerSendingRequestRetry() {
            return consumerSendingRequestRetry;
        }

        public void setConsumerSendingRequestRetry(boolean consumerSendingRequestRetry) {
            this.consumerSendingRequestRetry = consumerSendingRequestRetry;
        }

        public boolean isConsumerReceivedResponse() {
            return consumerReceivedResponse;
        }

        public void setConsumerReceivedResponse(boolean consumerReceivedResponse) {
            this.consumerReceivedResponse = consumerReceivedResponse;
        }

        @Override
        public String toString() {
            return "Log{" + "providerReceivedRequest=" + providerReceivedRequest + ", providerReceivedRequestBody=" + providerReceivedRequestBody
                    + ", providerSendingResponse=" + providerSendingResponse + ", consumerSendingRequest=" + consumerSendingRequest + ", consumerReceivedResponse="
                    + consumerReceivedResponse + '}';
        }
    }

    public Http getHttp() {
        return http;
    }

    public void setHttp(Http http) {
        this.http = http;
    }

    public Dubbo getDubbo() {
        return dubbo;
    }

    public void setDubbo(Dubbo dubbo) {
        this.dubbo = dubbo;
    }

    public Swagger getSwagger() {
        return swagger;
    }

    public void setSwagger(Swagger swagger) {
        this.swagger = swagger;
    }

    public LogSpring getLogSpring() {
        return logSpring;
    }

    public void setLogSpring(LogSpring logSpring) {
        this.logSpring = logSpring;
    }

    /**
     * 注意: 此处的默认值仅仅用做生成提示,而生效的默认值是在xml中定义,但是为了交互的友好,强烈建议2处保持一致
     */
    public static class LogSpring implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 日志文件所保存的路径
         */
        private String fileDir = "./log";

        /**
         * 控制台的日志格式
         */
        private String consolePattern =
            "%style{%d{yyyy-MM-dd HH:mm:ss.SSS}}{} %highlight{%-5level} %style{[%thread]}{} %style{%c}{magenta} %style{%X{CAN_CROSS_PROCESS|TRACE_ID} @ %X{CAN_CROSS_PROCESS|TENANT_ID} @ %X{CAN_CROSS_PROCESS|TENANT_CODE} @ %X{CAN_CROSS_PROCESS|USER_ID} @ %X{CAN_CROSS_PROCESS|USER_ACCOUNT}}{Cyan} %style{%msg}{green} %n";

        /**
         * 控制台的日志级别
         */
        private String consoleLevel = "INFO";

        /**
         * json文件日志格式
         */
        private String jsonPattern =
            "{\"@timestamp\":\"%d{yyyy-MM-dd HH:mm:ss.SSS}\",\"level\":\"%level\",\"thread\":\"%thread\",\"class\":\"%c\",\"trace_id\":\"%X{CAN_CROSS_PROCESS|TRACE_ID}\",\"tenant_code\":\"%X{CAN_CROSS_PROCESS|TENANT_CODE}\",\"tenant_id\":\"%X{CAN_CROSS_PROCESS|TENANT_ID}\",\"user_id\":\"%X{CAN_CROSS_PROCESS|USER_ID}\",\"user_account\":\"%X{CAN_CROSS_PROCESS|USER_ACCOUNT}\",\"msg\":\"%msg\",\"exception\":\"%exception\",\"application\":\"${sys:spring.application.name}\",\"env\":\"${sys:spring.profiles.active}\"}%n";

        /**
         * json文件日志级别
         */
        private String jsonLevel = "INFO";

        /**
         * skyWalking日志格式
         */
        private String skywalkingPattern = "%m%n";

        /**
         * skyWalking日志级别
         */
        private String skywalkingLevel = "INFO";


        public String getFileDir() {
            return fileDir;
        }

        public void setFileDir(String fileDir) {
            this.fileDir = fileDir;
        }

        public String getConsolePattern() {
            return consolePattern;
        }

        public void setConsolePattern(String consolePattern) {
            this.consolePattern = consolePattern;
        }

        public String getConsoleLevel() {
            return consoleLevel;
        }

        public void setConsoleLevel(String consoleLevel) {
            this.consoleLevel = consoleLevel;
        }

        public String getJsonPattern() {
            return jsonPattern;
        }

        public void setJsonPattern(String jsonPattern) {
            this.jsonPattern = jsonPattern;
        }

        public String getJsonLevel() {
            return jsonLevel;
        }

        public void setJsonLevel(String jsonLevel) {
            this.jsonLevel = jsonLevel;
        }

        public String getSkywalkingPattern() {
            return skywalkingPattern;
        }

        public void setSkywalkingPattern(String skywalkingPattern) {
            this.skywalkingPattern = skywalkingPattern;
        }

        public String getSkywalkingLevel() {
            return skywalkingLevel;
        }

        public void setSkywalkingLevel(String skywalkingLevel) {
            this.skywalkingLevel = skywalkingLevel;
        }

        @Override
        public String toString() {
            return "LogSpring{" + "fileDir='" + fileDir + '\'' + ", consolePattern='" + consolePattern + '\''
                + ", consoleLevel='" + consoleLevel + '\'' + ", jsonPattern='" + jsonPattern + '\'' + ", jsonLevel='" + jsonLevel + '\''
                + ", skyWalkingPattern='" + skywalkingPattern + '\'' + ", skyWalkingLevel='" + skywalkingLevel + '\'' + '}';
        }
    }
}
