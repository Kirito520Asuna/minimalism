package com.minimalism.key_pair.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class KeyVo {
    @Schema(description = "algorithm")
    private String algorithm;
    @Schema(description = "公钥(base64)")
    private String publicKey;
}
