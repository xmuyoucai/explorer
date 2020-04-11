package com.muyoucai.storage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LzyDataSource {

    private String url, user, pass, driver;

}
