package com.jintoufs.zj.transfercabinet.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by zj on 2017/9/14.
 */

public class CabinetModel {

    private static OutputStream os;
    private static InputStream is;

    /**
     * 向柜子发送信息
     *
     * @param socket
     * @param buf
     * @return
     * @throws Exception
     */
    private static String sendDataToCabinet(Socket socket, byte[] buf) throws Exception {
        String backInfo = null;
        if (socket == null) {
            return null;
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
    public static void openDrawer(Socket socket, String row, String column) throws Exception {
        int intRow = new Integer(row).intValue();
        int intColumn = new Integer(column).intValue();
        String strHex = Integer.toHexString(intRow);
        String strColumn = Integer.toHexString(intColumn);
        int hexRow = Integer.parseInt(strHex, 16);
        int hexColumn = Integer.parseInt(strColumn, 16);
        byte byteRow = new Integer(hexRow).byteValue();
        byte byteColumn = new Integer(hexColumn).byteValue();
        byte[] buf = {0x01, 0x00, 0x02, byteColumn, byteRow};
        sendDataToCabinet(socket, buf);
    }

    /**
     * 关闭指定行列的抽屉
     *
     * @param row
     * @param column
     */
    public static void closeDrawer(Socket socket, String row, String column) throws Exception {
        int intRow = new Integer(row).intValue();
        int intColumn = new Integer(column).intValue();
        String strHex = Integer.toHexString(intRow);
        String strColumn = Integer.toHexString(intColumn);
        int hexRow = Integer.parseInt(strHex, 16);
        int hexColumn = Integer.parseInt(strColumn, 16);
        byte byteRow = new Integer(hexRow).byteValue();
        byte byteColumn = new Integer(hexColumn).byteValue();
        byte[] buf = {0x02, 0x00, 0x02, byteColumn, byteRow};
        sendDataToCabinet(socket, buf);
    }

    /**
     * 测试指定行列的抽屉
     *
     * @param socket
     * @param row
     * @param column
     * @return
     * @throws Exception
     */
    public static boolean testDrawer(Socket socket, String row, String column) throws Exception {
        int intRow = new Integer(row).intValue();
        int intColumn = new Integer(column).intValue();
        String strHex = Integer.toHexString(intRow);
        String strColumn = Integer.toHexString(intColumn);
        int hexRow = Integer.parseInt(strHex, 16);
        int hexColumn = Integer.parseInt(strColumn, 16);
        byte byteRow = new Integer(hexRow).byteValue();
        byte byteColumn = new Integer(hexColumn).byteValue();
        byte[] buf = {0x03, 0x00, 0x02, byteColumn, byteRow};
        String backInfo = sendDataToCabinet(socket, buf);
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

    public static boolean isOpen(Socket socket, String row, String column) throws Exception {
        int intRow = new Integer(row).intValue();
        int intColumn = new Integer(column).intValue();
        String strHex = Integer.toHexString(intRow);
        String strColumn = Integer.toHexString(intColumn);
        int hexRow = Integer.parseInt(strHex, 16);
        int hexColumn = Integer.parseInt(strColumn, 16);
        byte byteRow = new Integer(hexRow).byteValue();
        byte byteColumn = new Integer(hexColumn).byteValue();
        byte[] buf = {0x03, 0x00, 0x02, byteColumn, byteRow};
        String backInfo = sendDataToCabinet(socket, buf);
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
     * 红外测试
     * @param socket
     * @param row
     * @param column
     */
    public static String textInfr(Socket socket,String row, String column) throws Exception {
        int intRow = new Integer(row).intValue();
        int intColumn = new Integer(column).intValue();
        String strHex = Integer.toHexString(intRow);
        String strColumn = Integer.toHexString(intColumn);
        int hexRow = Integer.parseInt(strHex, 16);
        int hexColumn = Integer.parseInt(strColumn, 16);
        byte byteRow = new Integer(hexRow).byteValue();
        byte byteColumn = new Integer(hexColumn).byteValue();
        byte[] buf = {0x04, 0x00, 0x02, byteColumn, byteRow};
        return sendDataToCabinet(socket, buf);
    }


    /**
     * 关闭连接
     *
     * @param socket
     * @throws IOException
     */
    public static void closeConnection(Socket socket) throws IOException {
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
