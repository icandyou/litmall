package litmall.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import domain.User;
import org.apache.ibatis.type.Alias;

/**
 * @author liznsalt
 */
@Alias("mallUser")
public class MallUser extends User implements IMember {
    @JsonIgnore
    @Override
    public String getUsername() {
        return getName();
    }

    @Override
    public void setUsername(String username) {
        setName(username);
    }

    @Override
    public boolean beEnable() {
        return !getBeDeleted();
    }
}
