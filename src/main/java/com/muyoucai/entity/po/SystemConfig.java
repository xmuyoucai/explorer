package com.muyoucai.entity.po;

import com.muyoucai.framework.Settings;
import com.muyoucai.framework.annotation.FileSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 16:05
 * @Version 1.0
 **/
@FileSettings(Settings.STATUS_FILENAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig {

    private String key;

    private String value;

}
