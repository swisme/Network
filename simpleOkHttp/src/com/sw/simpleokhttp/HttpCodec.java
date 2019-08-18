package com.sw.simpleokhttp;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class HttpCodec {
    public static final String CRLF = "\r\n";
    public static final int CR = 13;//ENTER ASCII CODE
    public static final int LF = 10;// NEW LINE ASCII CODE
    public static final String SPACE = " ";
    public static final String HTTP_VERSION = "HTTP/1.1";
    public static final String COLON = ":";

    public static final String HEADER_HOST = "Host";
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String HEADER_KEEP_ALIVE = "Keep-Alive";
    public static final String HEADER_CHUNKED = "chunked";
    public static final String PROTOCOL_HTTP = "http";
    public static final String PROTOCOL_HTTPS = "https";
    public static final String ENCODE_CHARSET = "UTF-8";
    private final ByteBuffer mByteBuffer;

    public HttpCodec() {
        mByteBuffer = ByteBuffer.allocate(10 * 1024);
    }

    /**
     * write request to socket
     *
     * @param os
     * @param request
     * @throws IOException
     */
    public void writeRequest(OutputStream os, Request request) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod().name).append(SPACE).append(request.getHttpUrl().getFile()).append(SPACE)
                .append(HTTP_VERSION).append(CRLF);

        //add request header
        Map<String, String> headers = request.getHeaders();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(COLON).append(SPACE).append(entry.getValue()).append(CRLF);
        }
        //add request blank line
        sb.append(CRLF);

        //add request body(POST)
        RequestBody body = request.getBody();
        if (body != null) {
            sb.append(body.getBody());
        }
        os.write(sb.toString().getBytes());
        os.flush();
    }

    public String readChunkedBody(InputStream is, int length) throws IOException {
        int len = -1;
        boolean isEmptyData = false;
        StringBuffer chunked = new StringBuffer();
        while (true) {
            if (len < 0) {
                String line = readLine(is);
                length += line.length();
                line = line.substring(0, line.length() - 2);//去掉CRLF
                //获得长度 16进制字符串转成10进制整型
                len = Integer.valueOf(line, 16);
                //如果读到的是0，那么再读一个响应空行CRLF就结束了
                isEmptyData = len == 0;
            } else {
                length += (len + 2);
                byte[] bytes = readBytes(is, len + 2);
                chunked.append(new String(bytes, ENCODE_CHARSET));
                len = -1;
                if (isEmptyData) {
                    return chunked.toString();
                }
            }
        }
    }

    public Map<String, String> readHeaders(InputStream is) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (true) {
            String line = readLine(is);
            if (isEmptyLine(line)) {
                break;
            }
            int indexOfColon = line.indexOf(COLON);
            if (indexOfColon > 0) {
                String key = line.substring(0, indexOfColon);
                //value前面有一个冒号和一个空格，所以要加2，value后面有一个CRLF所以要减2
                String value = line.substring(indexOfColon + 2, line.length() - 2);
                headers.put(key, value);
            }
        }
        return headers;
    }

    public String readLine(InputStream is) throws IOException {
        mByteBuffer.clear();
        mByteBuffer.mark();
        boolean isEofLine = false;//当出现一个\r的时候置为true,如果紧接着的是\n那就说明结束了
        byte b;
        while ((b = (byte) is.read()) != -1) {
            mByteBuffer.put(b);
            if (b == CR) {
                isEofLine = true;
            } else if (isEofLine) {
                if (b == LF) {
                    byte[] lineBytes = new byte[mByteBuffer.position()];
                    mByteBuffer.reset();//与上面的mark配合使用,告诉ByteBuffer将要使用从mark到当前的数据
                    mByteBuffer.get(lineBytes);
                    mByteBuffer.clear();
                    mByteBuffer.mark();
                    return new String(lineBytes, ENCODE_CHARSET);
                }
                //如果不是\n则将标志置为false
                isEofLine = false;
            }
        }
        throw new IOException("read line error");
    }

    public byte[] readBytes(InputStream is, int length) throws IOException {
        byte[] bytes = new byte[length];
        int readLength = 0;
        while (true) {
            readLength += is.read(bytes, readLength, length - readLength);
            if (readLength == length) {
                return bytes;
            }
        }
    }

    private boolean isEmptyLine(String line) {
        return TextUtils.equals(line, CRLF);
    }
}
