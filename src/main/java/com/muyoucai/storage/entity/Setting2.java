package com.muyoucai.storage.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "setting")
public class Setting2 {

    @Id
    @TableId
    private String key;
    private String value;

}
