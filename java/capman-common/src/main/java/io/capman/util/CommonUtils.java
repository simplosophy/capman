package io.capman.util;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * User: shangrenxiang
 * Date: 2014-11-27
 * Time: 10:13
 */
public class CommonUtils {

    private static Random RAND = new Random();

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public static class HostPort {
        public String host;
        public int port;

        public HostPort() {
            this.host = "0.0.0.0";
            this.port = 0;
        }


        public HostPort(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public final static HostPort DEFAULT = new HostPort();

        @Override
        public String toString() {
            return host + ":" + port;
        }
    }

    public static HostPort parseHostPort(String hp) {
        if (hp.startsWith("tcp://")) {
            hp = hp.substring(6);
        }
        String[] ss = hp.split(":");
        if (ss.length < 2) {
            return HostPort.DEFAULT;
        }
        HostPort rtn = new HostPort();

        rtn.host = ss[0];
        rtn.port = Integer.parseInt(ss[1]);
        return rtn;
    }


    /**
     * read resouce
     *
     * @param classpathResource
     * @return
     * @throws IOException
     */
    public static String resourceAsString(String classpathResource) throws
            IOException {
        InputStream inputStream =
                CommonUtils.class.getClassLoader().getResourceAsStream
                        (classpathResource);
        InputStreamReader inputStreamReader = new InputStreamReader
                (inputStream, "utf8");
        BufferedReader br = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    public static InputStream resourceAsStream(String classpathResource)
            throws IOException {
        InputStream inputStream =
                CommonUtils.class.getClassLoader().getResourceAsStream
                        (classpathResource);
        return inputStream;
    }

    /**
     * read resouce
     *
     * @param classpathResource
     * @return
     * @throws IOException
     */
    public static byte[] resourceAsBytes(String classpathResource) throws
            IOException {
        InputStream inputStream =
                CommonUtils.class.getClassLoader().getResourceAsStream
                        (classpathResource);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int r = 0;
        while ((r = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, r);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }

    public static byte[] fileAsBytes(String filePath) throws IOException {
        InputStream inputStream =
                new FileInputStream(new File(filePath));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int r = 0;
        while ((r = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, r);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }

    public static String genBillNO(String merchantId) throws IOException {
        long current = System.currentTimeMillis();
        String billNO = merchantId + DATE_FORMAT.format(new Date(current));
        int millSecondsInDay = (int) (current % 86400 * 1000);
        int millSecondsLen = String.valueOf(millSecondsInDay).length();
        if (millSecondsLen < 8) {
            for (int i = 0; i < 8 - millSecondsLen; i++) {
                billNO += "0";
            }
        }
        billNO += millSecondsInDay;
        for (int i = 0; i < 2; i++) {
            billNO += RAND.nextInt(10);
        }
        return billNO;
    }

    public static String genBillNO() throws IOException {
        return genBillNO("1279319901");
    }

    public static String readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int l = 0;
        byte[] buf = new byte[1024];
        while ( (l = inputStream.read(buf)) > 0){
            baos.write(buf, 0, l);
        }
        byte[] bytes = baos.toByteArray();
        String respStr = new String(bytes, "utf-8");
        return respStr;
    }


}
