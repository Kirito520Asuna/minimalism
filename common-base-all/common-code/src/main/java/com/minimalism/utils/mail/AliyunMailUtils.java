package com.minimalism.utils.mail;

import cn.hutool.core.collection.CollUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;

import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/3/16 1:11:20
 * @Description
 */
@Slf4j
public class AliyunMailUtils {

    // 配置区域、AccessKey 和 AccessKeySecret
    private static final String REGION_ID = "cn-hangzhou";
    private static final String ACCESS_KEY_ID = "your_access_key_id";
    private static final String ACCESS_KEY_SECRET = "your_access_key_secret";

    // 发信配置
    private static final String ACCOUNT_NAME = "your_sender@example.com";
    private static final String FROM_ALIAS = "发信人别名";
    private static final String TAG_NAME = "邮件标签";

    @Data
    @Accessors(chain = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    static class MailClient {
        private IAcsClient iAcsClient;
        /**
         * 发信地址/发信人账号 (控制台创建的发信地址)
         */
        private String sendAddress;
        /**
         * 收件人邮箱
         */
        private List<String> toAddressList;

        /**
         * 标签
         */
        private String tagName;
        /**
         * 发信人昵称
         */
        private String senderName;
        /**
         * 邮件主题
         */
        private String subject;

        /**
         * 邮件内容
         */
        private String content;

        boolean sendMail() {
            return AliyunMailUtils.sendMail(this);
        }
    }

    /**
     * 创建客户端
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param endpoint
     * @return
     */

    public static IAcsClient createIAcsClient(String accessKeyId, String accessKeySecret, String endpoint) {
        //"cn-hangzhou"
        IClientProfile profile = DefaultProfile.getProfile(endpoint,
                accessKeyId,
                accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        return client;
    }

    /**
     * 发送邮件
     *
     * @param client
     * @param toAddressList 收件人地址
     * @param subject       主题
     * @param accountName   发信人账号 (控制台创建的发信地址)
     * @param senderName    发信人昵称
     * @param content       邮件内容
     */
    @SneakyThrows
    public static boolean sendMail(IAcsClient client, List<String> toAddressList, String subject, String accountName, String senderName, String tagName, String content) {
        String address = toAddressList.stream().collect(Collectors.joining(","));
        SingleSendMailRequest request = new SingleSendMailRequest();
        request.setAccountName(accountName);//控制台创建的发信地址
        request.setFromAlias(senderName);//发信人昵称
        request.setAddressType(1);// 1 表示发信地址
        request.setReplyToAddress(true);
        request.setTagName(tagName);
        request.setToAddress(address);
        request.setSubject(subject);//邮件主题
        request.setHtmlBody(content);//邮件正文
        //SingleSendMailResponse httpResponse = client.getAcsResponse(request);
        SingleSendMailResponse response = client.getAcsResponse(request);
        if (response.getRequestId() != null && !response.getRequestId().isEmpty()) {
            log.info("邮件发送成功，RequestId：{}" ,response.getRequestId());
            return true;
        }
        log.error("邮件发送失败");
        return false;
    }

    /**
     * 发送邮件
     *
     * @param mailClient
     * @return
     */

    public static boolean sendMail(MailClient mailClient) {
        IAcsClient iAcsClient = mailClient.getIAcsClient();
        String content = mailClient.getContent();
        String sendAddress = mailClient.getSendAddress();
        String senderName = mailClient.getSenderName();
        String subject = mailClient.getSubject();
        String tagName = mailClient.getTagName();
        List<String> toAddressList = mailClient.getToAddressList();
        return sendMail(iAcsClient, toAddressList, subject, sendAddress, senderName, tagName, content);
    }


    public static void main(String[] args) {

        String accessKeyId = "accessKeyId";
        String accessKeySecret = "accessKeySecret";
        String endpoint = "cn-hangzhou";
        String tagName = "1";
        String content = "测试";
        String subject = "测试主题";


        ArrayList<String> toAddressList = CollUtil.newArrayList("xxx@qq.com");
        String sendAddress = "your_sender@example.com";

        String senderName = "测试";

        boolean sendMail = false;
        MailClient build = MailClient.builder()
                .iAcsClient(AliyunMailUtils.createIAcsClient(accessKeyId, accessKeySecret, endpoint))
                .content(content)
                .subject(subject)
                .tagName(tagName)
                .toAddressList(toAddressList)
                .sendAddress(sendAddress)
                .senderName(senderName)
                .build();
        sendMail = build.sendMail();
        // sendMail = AliyunMailUtils.sendMail(build);
        System.err.println("sendMail:" + sendMail);
    }

}
