package net.woorisys.lighting.control3.admin.sjp.sharedpreference;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserData {

    @Getter
    @Setter
    String Id;
    String Password;
}
