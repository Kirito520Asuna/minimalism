package com.minimalism.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/7/19 0019 9:48:54
 * @Description
 */
@NoArgsConstructor
@Data
public class WXPushMessage {
    /**
     * 必须 :否
     * 成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
     */
    private String touser;
    /**
     * 必须 :否
     * 部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
     */
    private String toparty;
    /**
     * 必须 :否
     * 标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
     */
    private String totag;
    /**
     * 必须 :是
     *消息类型，此时固定为：template_card
     */
    private String msgtype;
    /**
     * 必须 :是
     * 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
     */
    private Integer agentid;
    private TemplateCard templateCard;
    private Integer enableIdTrans;
    private Integer enableDuplicateCheck;
    private Integer duplicateCheckInterval;


}
@NoArgsConstructor
@Data
class TemplateCard {
    /**
     * 必须 :是
     * 模板卡片类型，文本通知型卡片填写 "text_notice"
     */
    private String cardType;
    /**
     * 必须 :否
     * 卡片来源样式信息，不需要来源样式可不填写
     */
    private Source source;
    /**
     * 必须 :否
     * 卡片右上角更多操作按钮
     */
    private ActionMenu actionMenu;
    private String taskId;
    private MainTitle mainTitle;
    /**
     * 必须 :否
     * 引用文献样式
     */
    private QuoteArea quoteArea;
    /**
     * 必须 :否
     * 关键数据样式
     */
    private EmphasisContent emphasisContent;
    /**
     * 必须 :否
     * 二级普通文本，建议不超过160个字，（支持id转译）
     */
    private String subTitleText;
    /**
     * 必须 :否
     * 二级标题+文本列表，该字段可为空数组，但有数据的话需确认对应字段是否必填，列表长度不超过6
     */
    private List<HorizontalContentList> horizontalContentList;
    /**
     * 必须 :否
     * 跳转指引样式的列表，该字段可为空数组，但有数据的话需确认对应字段是否必填，列表长度不超过3
     */
    private List<JumpList> jumpList;
    /**
     * 必须 :是
     * 整体卡片的点击跳转事件，text_notice必填本字段
     */
    private CardAction cardAction;


}
@NoArgsConstructor
@Data
class Source {
    /**
     * 必须 :否
     * 来源图片的url，来源图片的尺寸建议为72*72
     */
    private String iconUrl;
    /**
     * 必须 :否
     * 来源图片的描述，建议不超过20个字，（支持id转译）
     */
    private String desc;
    /**
     * 必须 :否
     * 来源文字的颜色，目前支持：0(默认) 灰色，1 黑色，2 红色，3 绿色
     */
    private Integer descColor;
}

@NoArgsConstructor
@Data
class ActionMenu {
    /**
     * 必须 :否
     * 更多操作界面的描述
     */
    private String desc;
    /**
     * 必须 :是
     * 操作列表，列表长度取值范围为 [1, 3]
     */
    private List<ActionList> actionList;


}
@NoArgsConstructor
@Data
class ActionList {
    /**
     * 必须 :是
     * 操作的描述文案
     */
    private String text;
    /**
     * 必须 :是
     * 操作key值，用户点击后，会产生回调事件将本参数作为EventKey返回，回调事件会带上该key值，最长支持1024字节，不可重复
     */
    private String key;
}
@NoArgsConstructor
@Data
class MainTitle {
    /**
     * 必须 :否
     * 一级标题，建议不超过36个字，文本通知型卡片本字段非必填，但不可本字段和sub_title_text都不填，（支持id转译）
     */
    private String title;
    /**
     * 必须 :否
     * 标题辅助信息，建议不超过44个字，（支持id转译）
     */
    private String desc;
}

@NoArgsConstructor
@Data
class QuoteArea {
    /**
     * 必须 :否
     * 引用文献样式区域点击事件，0或不填代表没有点击事件，1 代表跳转url，2 代表跳转小程序
     */
    private Integer type;
    /**
     * 必须 :否
     * 点击跳转的url，quote_area.type是1时必填
     */
    private String url;
    /**
     * 必须 :否
     * 引用文献样式的标题
     */
    private String title;
    /**
     * 必须 :否
     * 引用文献样式的引用文案
     */
    private String quoteText;
}

@NoArgsConstructor
@Data
class EmphasisContent {
    /**
     * 必须 :否
     * 关键数据样式的数据内容，建议不超过14个字
     */
    private String title;
    /**
     * 必须 :否
     * 关键数据样式的数据描述内容，建议不超过22个字
     */
    private String desc;
}

@NoArgsConstructor
@Data
class CardAction {
    private Integer type;
    private String url;
    private String appid;
    private String pagepath;
}

@NoArgsConstructor
@Data
class HorizontalContentList {
    private String keyname;
    private String value;
    private Integer type;
    private String url;
    private String mediaId;
    private String userid;
}

@NoArgsConstructor
@Data
class JumpList {
    /**
     * 必须 :否
     * 跳转链接类型，0或不填代表不是链接，1 代表跳转url，2 代表跳转小程序
     */
    private Integer type;
    /**
     * 必须 :是
     * 跳转链接样式的文案内容，建议不超过18个字
     */
    private String title;
    /**
     * 必须 :否
     * 跳转链接的url，type是1时必填
     */
    private String url;
    /**
     * 必须 :否
     * 跳转链接的小程序的appid，必须是与当前应用关联的小程序，type是2时必填
     */
    private String appid;
    /**
     * 必须 :否
     * 跳转链接的小程序的pagepath，type是2时选填
     */
    private String pagepath;
}