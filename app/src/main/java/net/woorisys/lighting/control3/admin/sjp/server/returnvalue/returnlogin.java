package net.woorisys.lighting.control3.admin.sjp.server.returnvalue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class returnlogin {

    @Getter
    @Setter
    @SerializedName("id")
    @Expose
    int id;

    @Getter
    @Setter
    @SerializedName("userId")
    @Expose
    String userId;

    @Getter
    @Setter
    @SerializedName("name")
    @Expose
    String name;

    @Getter
    @Setter
    @SerializedName("role")
    @Expose
    String role;

    @Getter
    @Setter
    @SerializedName("password")
    @Expose
    String password;

    @Getter
    @Setter
    @SerializedName("reTypePassword")
    @Expose
    String reTypePassword;

    @Getter
    @Setter
    @SerializedName("underFloorsCount")
    @Expose
    int underFloorsCount;

    @Getter
    @Setter
    @SerializedName("startFloor")
    @Expose
    int startFloor;
}
