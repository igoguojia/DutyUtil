package com.jnu.tokenHandle.pojo;

public enum AccessLevel {
    //操作权限为前五位  0：0：0 ：0：0  删除：修改：创建：群体：读取
    createTask("00101", new String[]{"00100", "00150"}),
    startTask("01001", new String[]{"00100", "00150"}),
    stopTask("01001", new String[]{"00100", "00150"}),
    deleteTask("10001", new String[]{"00100", "00150"}),
    updateTask("01001", new String[]{"00100", "00150"}),
    retrieveAllTasks("00001", new String[]{"00100", "00150"}),
    retrieveTaskByAlias("00001", new String[]{"00100", "00150"}),
    retrieveLogs("00001", new String[]{"00100", "00150"}),
    deleteLogByAlias("10001", new String[]{"00100", "00150"}),
    deleteLogs("10001", new String[]{"00100", "00150"}),

    //UserController user
//    getUserNNByGroupName("00011", new String[]{"00100"}),

    // AuthorityController /authority
    createAuthority("00101", new String[]{"00100"}),
    deleteAuthorityById("10001", new String[]{"00100"}),
    retrieveAuthorityById("00001", new String[]{"00100"}),
    retrieveAllAuthoritys("00011", new String[]{"00100"}),
    updateAuthority("01001", new String[]{"00100"}),

    // GroupController /group
    createGroup("00101", new String[]{"00100"}),
    deleteGroupByName("10001", new String[]{"00100"}),
    retrieveGroupByName("00001", new String[]{"00100"}),
    retrieveAllGroups("00011", new String[]{"00100"}),
    updateGroup("01001", new String[]{"00100"}),


    // CharacterAuthorityController /characterAuthority
    createCharacterAuthority("00101", new String[]{"00100"}),
    deleteCharacterAuthority("10001", new String[]{"00100"}),
    retrieveAllCharacterAuthority("00011", new String[]{"00100"}),
    retrieveCharacterAuthorityByCharacterId("00001", new String[]{"00100"}),
    retrieveCharacterAuthorityByAuthorityId("00001", new String[]{"00100"}),

    // CharacterController /character
    createCharacter("00101", new String[]{"00100"}),
    retrieveAllCharacters("00011", new String[]{"00100"}),
    retrieveCharacterById("00001", new String[]{"00100"}),
    updateCharacter("01001", new String[]{"00100"}),
    deleteCharacterById("10001", new String[]{"00100"}),

    // UserAuthorityController /userAuthority
    retrieveAuthorityInfoByUserId("00011", new String[]{"00100"}),
    createUserAuthority("00101", new String[]{"00100"}),
    deleteUserAuthority("10001", new String[]{"00100"}),
    retrieveAllUserAuthority("00011", new String[]{"00100"}),
    retrieveUserAuthorityByUserId("00001", new String[]{"00100"}),
    retrieveUserAuthorityByAuthorityId("00001", new String[]{"00100"}),

    // UserCharacterController /userCharacter
    createUserCharacter("00101", new String[]{"00100"}),
    deleteUserCharacter("10001", new String[]{"00100"}),
    retrieveAllUserCharacter("00011", new String[]{"00100"}),
    retrieveUserControllerByUserId("00001", new String[]{"00100"}),
    retrieveUserControllerByCharacterId("00001", new String[]{"00100"}),

    // VariableController /variable
    retrieveVariable("00001", new String[]{"00100"}),
    fuzzyVariable("00001", new String[]{"00100"}),
    createVariable("00101", new String[]{"00100"}),
    deleteVariable("10001", new String[]{"00100"}),
    updateVariable("01001", new String[]{"00100"}),

    // DingTalkController /dingTalk
    insertDingTalk("00101", new String[]{"00100", "00160"}),
    deleteDingTalk("10001", new String[]{"00100", "00160"}),
    updateDingTalk("01001", new String[]{"00100", "00160"}),
    selectAllDT("00001", new String[]{"00100", "00160"}),
    selectByGroupName("00001", new String[]{"00100", "00160"});


    final String auth;
    final String[] authChara;
    // String msg;

    AccessLevel(String auth, String[] authChara) {
        this.auth = auth;
        this.authChara = authChara;
    }

    public String getAuth() {
        return auth;
    }

    public String[] getAuthChara() {
        return authChara;
    }
}

