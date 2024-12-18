package com.minimalism.utils.sms;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * @Author yan
 * @Date 2024/7/24 0024 16:31:44
 * @Description
 */
@Data
//@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Slf4j
public class SmsHelper {
    @AllArgsConstructor
    @Getter
    enum SmsType {
        RONG_LIAN_YUN("容联云");
        private String desc;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Slf4j
    static class SmsSDK {
        //短信云类型
        SmsType smsType;
        //容联云
        SmsRLYSDK smsRLYSDK;
    }

    @Data
    //@NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Slf4j
    static class SmsRLYSDK extends CCPRestSmsSDK {
        public static SmsRLYSDK getSmsSDK(String accountId, String authToken, String appId) {
            //初始化容联云短信SDK
            SmsRLYSDK smsSDK = new SmsRLYSDK();
            //初始化服务器地址和端口，生产环境配置成app.cloopen.com，端口是8883。
            smsSDK.init(SmsConstant.SMS_URL_RONG_LIAN_YUN, SmsConstant.SMS_PORT_RONG_LIAN_YUN);
            // 初始化服务器地址和端口，生产环境配置成app.cloopen.com，端口是8883。
            // 初始化主账号名称和主账号令牌，登陆云通讯网站后，可在"控制台-应用"中看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN。
            smsSDK.setAccount(accountId, authToken);
            // 初始化应用ID
            smsSDK.setAppId(appId);

            return smsSDK;
        }

        /**
         * 发送模板短信
         *
         * @param smsSDK     sdk
         * @param to         手机号 多个号码用逗号分隔
         * @param templateId 模板id
         * @param datas      模板参数
         * @return
         */
        public static boolean sendTemplateSMS(SmsRLYSDK smsSDK, String to, String templateId, String[] datas) {
            boolean sendOk = false;
            try {
                // 调用发送模板短信的接口发送短信
                HashMap<String, Object> map = smsSDK.sendTemplateSMS(to, templateId, datas);
                if (SmsConstant.SUCCESS_CODE_RONG_LIAN_YUN.equals(map.get(SmsConstant.STATUS_CODE_RONG_LIAN_YUN))) {
                    sendOk = true;
                }else {
                    log.error("容联云 短信{}发送失败 错误码={}, 错误信息= {}"
                            , templateId, map.get(SmsConstant.STATUS_CODE_RONG_LIAN_YUN), map.get(SmsConstant.STATUS_MSG_RONG_LIAN_YUN));
                }
            } catch (Exception e) {
                log.error("容联云 短信{}发送异常 error={}", templateId, e);
                e.printStackTrace();
            }
            return sendOk;
        }
    }

    interface SmsConstant {
        //private static final Logger LOGGER = LoggerFactory.getLogger(SmsHelper.class);
        // 容联云API
        String SMS_URL_RONG_LIAN_YUN = "app.cloopen.com";
        String SMS_PORT_RONG_LIAN_YUN = "8883";
        String STATUS_CODE_RONG_LIAN_YUN = "statusCode";
        String STATUS_MSG_RONG_LIAN_YUN = "statusMsg";
        String SUCCESS_CODE_RONG_LIAN_YUN = "000000";
    }

}
