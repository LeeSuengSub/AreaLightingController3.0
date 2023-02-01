package net.woorisys.lighting.control3.admin.sjp;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

public class RememberData {

    @Getter@Setter
    private File savefilepath=new File("NULL");

    private static final RememberData ourInstance = new RememberData();

    public static RememberData getInstance() {
        return ourInstance;
    }

    private RememberData() {
    }
}
