package com.jintoufs.zj.transfercabinet.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by zj on 2017/9/14.
 */

public class CabinetModel {
    public String cabinet_ip;
    public static final int PORT = 5000;

    private static OutputStream os;
    private static InputStream is;
    private Socket socket;

    public CabinetModel(String cabinetIP) throws IOException {
        this.cabinet_ip = cabinetIP;
        socket =  new Socket(cabinet_ip, PORT);
    }

    /**
     * 向柜子发送信息
     *
     * @param buf
     * @return
     * @throws Exception
     */
    private String sendDataToCabinet(byte[] buf) throws Exception {
        String backInfo = null;
        if (socket == null) {
            socket = new Socket(cabinet_ip, PORT);
        }
        if (socket.isConnected()) {
            os = socket.getOutputStream();
            os.write(buf);
            os.flush();
            byte[] bytes = new byte[10];
            is = socket.getInputStream();
            is.read(bytes);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String value = new String(new Integer(bytes[i]).toString());
                sb.append(value + " ");
            }
            backInfo = sb.toString();
        } else {
            backInfo = "连接失败";
        }
        return backInfo;
    }

    /**
     * 打开指定行列的抽屉
     *
     * @param row
     * @param column
     */
    public String openDrawer(String row, String column) throws Exception {
        int intRow = new Integer(row).intValue();
        int intColumn = new Integer(column).intValue();
        String strHex = Integer.toHexString(intRow);
        String strColumn = Integer.toHexString(intColumn);
        int hexRow = Integer.parseInt(strHex, 16);
        int hexColumn = Integer.parseInt(strColumn, 16);
        byte byteRow = new Integer(hexRow).byteValue();
        byte byteColumn = new Integer(hexColumn).byteValue();
        byte[] buf = {0x01, 0x00, 0x02, byteColumn, byteRow};
        return sendDataToCabinet(buf);
    }

    /**
     * 关闭指定行列的抽屉
     *
     * @param row
     * @param column
     */
    public String closeDrawer(String row, String column) throws Exception {
        int intRow = new Integer(row).intValue();
        int intColumn = new Integer(column).intValue();
        String strHex = Integer.toHexString(intRow);
        String strColumn = Integer.toHexString(intColumn);
        int hexRow = Integer.parseInt(strHex, 16);
        int hexColumn = Integer.parseInt(strColumn, 16);
        byte byteRow = new Integer(hexRow).byteValue();
        byte byteColumn = new Integer(hexColumn).byteValue();
        byte[] buf = {0x02, 0x00, 0x02, byteColumn, byteRow};
        return sendDataToCabinet(buf);
    }

    /**
     * 测试指定行列的抽屉
     *
     * @param row
     * @param column
     * @return
     * @throws Exception
     */
    public String testDrawer(String row, String column) throws Exception {
        int intRow = new Integer(row).intValue();
        int intColumn = new Integer(column).intValue();
        String strHex = Integer.toHexString(intRow);
        String strColumn = Integer.toHexString(intColumn);
        int hexRow = Integer.parseInt(strHex, 16);
        int hexColumn = Integer.parseInt(strColumn, 16);
        byte byteRow = new Integer(hexRow).byteValue();
        byte byteColumn = new Integer(hexColumn).byteValue();
        byte[] buf = {0x03, 0x00, 0x02, byteColumn, byteRow};
        return sendDataToCabinet(buf);
    }

    public boolean isOpen(String row, String column) throws Exception {
        int intRow = new Integer(row).intValue();
        int intColumn = new Integer(column).intValue();
        String strHex = Integer.toHexString(intRow);
        String strColumn = Integer.toHexString(intColumn);
        int hexRow = Integer.parseInt(strHex, 16);
        int hexColumn = Integer.parseInt(strColumn, 16);
        byte byteRow = new Integer(hexRow).byteValue();
        byte byteColumn = new Integer(hexColumn).byteValue();
        byte[] buf = {0x03, 0x00, 0x02, byteColumn, byteRow};
        String backInfo = sendDataToCabinet(buf);
        String[] strs = backInfo.split(" ");
        if (strs.length > 1) {
            String flag = strs[0];
            if (flag.equals("-127")) {
                return true;
            } else if (flag.equals("-126")) {
                return false;
            }
        }
        return false;
    }

    /**
     * 关闭连接
     *
     * @param socket
     * @throws IOException
     */
    public void closeConnection(Socket socket) throws IOException {
        if (os != null) {
            os.close();
        }
        if (is != null) {
            is.close();
        }
        if (socket != null) {
            socket.close();
        }
    }
}
