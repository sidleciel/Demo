package com.rhodes.demo.Util;

/**
 * Created by lala on 15/7/23.
 */
public class StartGameMeta {
    /**
     *      * @param gameId      游戏编号
     * @param packageName 包名
     * @param launch      景哥传递的启动activity
     * @param filePath    应用程序文件路径
     * @param serverIP    null
     * @param plugin_area_val    专区id
     */

    private String packageName;
    private String activityName;
    private String romPath;
    private int startMode;
    private String userID;
    private String gameID;
    private int version=15;
    private String roomID;
    private String serverIP;
    private String serverPort;
    private int porder;
    private int type;
    private long plugin_area_val;

    private String p1Name;
    private String p2Name;
    public long getPlugin_area_val() {
        return plugin_area_val;
    }

    public String getP1Name() {
        return p1Name;
    }
    public void setPlugin_area_val(long plugin_area_val) {
        this.plugin_area_val = plugin_area_val;
    }

    public void setP1Name(String p1Name) {
        this.p1Name = p1Name;
    }

    public String getP2Name() {
        return p2Name;
    }

    public void setP2Name(String p2Name) {
        this.p2Name = p2Name;
    }
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getRomPath() {
        return romPath;
    }

    public void setRomPath(String romPath) {
        this.romPath = romPath;
    }

    public int getStartMode() {
        return startMode;
    }

    public void setStartMode(int startMode) {
        this.startMode = startMode;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public int getPorder() {
        return porder;
    }

    public void setPorder(int porder) {
        this.porder = porder;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
