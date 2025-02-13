//package com.minimalism.im.domain.security;
//
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson.annotation.JSONField;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonView;
//import com.minimalism.view.BaseJsonView;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
///**
// * @Author minimalism
// * @Date 2023/5/12 0012 16:18
// * @Description
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class LoginUser implements UserDetails {
//
//    //判断帐号是否已经过期
//    private Boolean accountNonExpired;
//    //判断帐号是否已被锁定
//    private Boolean accountNonLocked;
//    //判断用户凭证是否已经过期
//    private Boolean credentialsNonExpired;
//    //用户是否可用
//    private Boolean enabled;
//    @Schema(description = "账号")
//    @JsonView(BaseJsonView.RegisterView.class)
//    private String username;
//    @Schema(description = "昵称")
//    @JsonView(BaseJsonView.RegisterView.class)
//    private String nickname;
//    @Schema(description = "密码")
//    @JsonView(BaseJsonView.RegisterView.class)
//    private String password;
//    /**
//     * 用户
//     */
////    @JsonIgnore//排除展示
////    @JSONField(serialize = false)
//    private User user;
//    /**
//     * 权限信息字符串
//     */
//    private List<String> roles;
//
//    /**
//     * 权限信息对象
//     */
//    @JsonIgnore//排除展示
//    @JSONField(serialize = false)//serialize = false不会序列化防止设置到redis报错
//    private List<GrantedAuthority> authorities;
//
//    public LoginUser(User user, List<String> roles) {
//        this.user = user;
//        this.roles = roles;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        if (authorities == null && roles != null && roles.size() > 0) {
//            //把roles中的String类型的权限信息封装成SimpleGrantedAuthority对象
//            authorities = roles.stream()
//                    .map(SimpleGrantedAuthority::new)
//                    .distinct().collect(Collectors.toList());
//        }
//        return authorities;
//    }
//
//    @Override
//    public String getPassword() {
//        if (StrUtil.isNotBlank(this.password)){
//            return this.password;
//        }
//        return Objects.nonNull(user)?user.getPassword():null;
//    }
//
//    @Override
//    public String getUsername() {
//        return Objects.nonNull(user)?user.getUsername():null;
//    }
//    public String getNickname() {
//        return Objects.nonNull(user)?user.getNickname():null;
//    }
//
//    /*判断帐号是否已经过期*/
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    /*判断帐号是否已被锁定*/
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    /*判断用户凭证是否已经过期*/
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    /*用户是否可用*/
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//    public Boolean getAccountNonExpired() {
//        return isAccountNonExpired();
//    }
//
//    public Boolean getAccountNonLocked() {
//        return isAccountNonLocked();
//    }
//
//    public Boolean getCredentialsNonExpired() {
//        return isCredentialsNonExpired();
//    }
//
//    public Boolean getEnabled() {
//        return isEnabled();
//    }
//
//
//}
