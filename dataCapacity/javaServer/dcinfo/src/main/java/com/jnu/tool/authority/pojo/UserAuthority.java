package com.jnu.tool.authority.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author duya001
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthority {
    private String userID;
    private String authorityId;
}
