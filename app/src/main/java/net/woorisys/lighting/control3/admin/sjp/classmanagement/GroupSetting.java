package net.woorisys.lighting.control3.admin.sjp.classmanagement;

import java.io.File;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
public class GroupSetting implements Serializable {

    @Getter
    @Setter
    String SectionId;
    @Getter
    @Setter
    String Transmission;
    @Getter
    @Setter
    File file;
}
